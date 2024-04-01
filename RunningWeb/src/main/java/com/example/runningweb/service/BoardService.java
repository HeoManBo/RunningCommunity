package com.example.runningweb.service;

import com.example.runningweb.domain.AttachFile;
import com.example.runningweb.domain.Board;
import com.example.runningweb.domain.Member;
import com.example.runningweb.dto.BoardDto;
import com.example.runningweb.dto.BoardListDto;
import com.example.runningweb.dto.BoardUpdateRequest;
import com.example.runningweb.dto.BoardViewDto;
import com.example.runningweb.repository.BoardRepository;
import com.example.runningweb.repository.FileRepository;
import com.example.runningweb.repository.MemberRepository;
import com.example.runningweb.util.FileUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class BoardService {

    private static final int PAGE_SIZE = 10; //한 페이지당 10 페이지를 보여줌

    private final BoardRepository boardRepository;
    private final FileRepository fileRepository;
    private final CommentService commentService;
    private final FileUtil fileUtil;
    private final MemberRepository memberRepository;

    /**
     * 파일을 만들려다가 실패하는 경우 이전파일을 삭제 해야함
     * --> 일단 파일은 하나만 저장이 가능하므로 추후 개발
     */
    // Checked Exception은
    @Transactional(rollbackFor = {IOException.class})
    public Long createBoard(BoardDto boardDto, Member member) {

        // board 생성
        Board board = Board.builder()
                .title(boardDto.getTitle())
                .content(boardDto.getContent())
                .writer(member.getNickname())
                .build();

        saveFiles(boardDto.getAttachFile(), board);

        boardRepository.save(board);
        board.updateWriter(member);
        return board.getId();
    }

    @Transactional
    public BoardViewDto findByBoardId(Long boardId) {
        List<Board> boards = boardRepository.getBoardWithUser(boardId); // pk로 조회하므로 1개밖에 없어야 함

        if(boards.size() != 1){
            log.info("board Size : {}", boards.size());
            throw new IllegalArgumentException("잘못된 게시판 번호입니다.");
        }

        Board board = boards.get(0);

        List<AttachFile> attachFiles = fileRepository.getAttachFiles(board);

        // view 업데이트
        //board.addView();
        boardRepository.updateViewCount(boardId);

        return BoardViewDto.builder()
                .boardId(boardId)
                .title(board.getTitle())
                .content(board.getContent())
                .attachFiles(attachFiles)
                .writer(board.getWriter())
                .writeAt(formattingDate(board.getCreatedAt()))
                .username(board.getMember().getUsername()).build();
    }

    //추후 페이징 처리 필요함.
    public List<BoardListDto> boardList(Integer pageNum) {
        Pageable paging = PageRequest
                .of(pageNum - 1, PAGE_SIZE, Sort.by("createdAt").descending());

        Page<Board> pagingBoards = boardRepository.findByPagingBoard(paging);
        if(pagingBoards.getTotalPages() < pageNum-1) {
            throw new IllegalArgumentException("너무 큰 페이지 번호입니다.");
        }

        int endPage = (int)(Math.ceil(pageNum / 10.0)) * 10;
        endPage = Math.min(endPage, pagingBoards.getTotalPages()); //마지막 페이지 번호
        int startPage = endPage - 9 <= 0 ? 1 : endPage - 9;
        List<Board> all = pagingBoards.getContent();
        all.get(0).getComments(); // in Query로 처리

        int finalEndPage = endPage;
        return all.stream().map(board -> BoardListDto.builder()
                        .boardId(board.getId())
                        .title(board.getTitle())
                        .writer(board.getWriter())
                        .wroteAt(formattingDate(board.getCreatedAt()))
                        .commentCnt(board.getComments().size())
                        .startPage(startPage)
                        .endPage(finalEndPage)
                        .lastPageNum(pagingBoards.getTotalPages())
                        .build())
                .collect(Collectors.toList());
    }

    private String formattingDate(LocalDateTime localDateTime){
        return localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    public Long updateBoard(BoardUpdateRequest boardUpdateRequest) {
        BoardViewDto boardViewDto = boardUpdateRequest.getBoardViewDto();

        //기존 파일 삭제는 동적으로 처리했으므로 파일 추가만 여기서 작업하자.
        Board board = boardRepository.getBoardWithFile(boardViewDto.getBoardId());

        saveFiles(boardUpdateRequest.getNewAttachFiles(), board);

        board.updateBoard(boardUpdateRequest.getBoardViewDto());

        return board.getId();
    }

    private void saveFiles(List<MultipartFile> files,Board board){
        List<String> storedFileName = new ArrayList<>(); // Rollback시 삭제할 파일

        for(MultipartFile file : files){
            AttachFile attachFile = null;
            try{
                attachFile = fileUtil.attachFile(file);
                if(attachFile != null) {
                    storedFileName.add(attachFile.getServerFileName());
                    board.saveFiles(attachFile);
                }
            } catch (IOException e) {
                //삭제 처리
                fileUtil.deleteFiles(storedFileName);
                throw new RuntimeException("내부 서버 에러입니다. 다시 시도해주세요.");
            }
        }
    }

    //게시판 삭제 하기
    //게시판은 1 (member) : N (file)이므로 join fetch가 가능하다.
    public void deleteBoard(Long boardId, String username) {
        Board board = boardRepository.findBoardAllInfo(boardId)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 게시판 삭제 요청입니다."));

        //삭제가능한지 확인
        if (!board.getMember().getUsername().equals(username)) {
            throw new IllegalArgumentException("다른 사람의 게시글은 삭제할 수 없습니다.");
        }

        //파일 삭제 처리
        fileUtil.deleteFiles(board.getFiles().stream().map(AttachFile::getServerFileName)
                .collect(Collectors.toList()));

        //게시글 삭제
        commentService.deleteComments(board);

        //삭제 처리.
        boardRepository.delete(board);

    }

    public void makeDummyBoard() {
        Member member = memberRepository.findById(1L).get();
        List<Board> dummies = new ArrayList<>();
        for(int i=1; i<=100; i++){
            Board board = Board.builder()
                    .title(String.valueOf(i))
                    .content(String.valueOf(i))
                    .writer(member.getNickname())
                    .build();

            board.updateWriter(member);
            dummies.add(board);
        }
        boardRepository.saveAll(dummies);
    }
}
