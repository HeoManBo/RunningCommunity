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
import com.example.runningweb.util.FileUtil;
import lombok.RequiredArgsConstructor;
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
public class BoardService {

    private final BoardRepository boardRepository;
    private final FileRepository fileRepository;
    private final CommentService commentService;
    private final FileUtil fileUtil;


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
        board.UpdateWriter(member);
        return board.getId();
    }

    @Transactional(readOnly = true)
    public BoardViewDto findByBoardId(Long boardId) {
        List<Board> boards = boardRepository.getBoardWithUser(boardId); // pk로 조회하므로 1개밖에 없어야 함
        if(boards.size() != 1){
            throw new IllegalArgumentException("잘못된 게시판 번호입니다.");
        }
        Board board = boards.get(0);

        List<AttachFile> attachFiles = fileRepository.getAttachFiles(board);

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
    public List<BoardListDto> boardList() {
        List<Board> all = boardRepository.findAll();
        all.sort((c1, c2) -> c2.getCreatedAt().compareTo(c1.getCreatedAt()));

        return all.stream().map(board -> BoardListDto.builder()
                        .boardId(board.getId())
                    .title(board.getTitle())
                    .writer(board.getWriter())
                    .wroteAt(formattingDate(board.getCreatedAt()))
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

}
