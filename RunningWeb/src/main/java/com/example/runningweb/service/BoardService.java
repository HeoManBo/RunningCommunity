package com.example.runningweb.service;

import com.example.runningweb.domain.AttachFile;
import com.example.runningweb.domain.Board;
import com.example.runningweb.domain.Member;
import com.example.runningweb.dto.BoardDto;
import com.example.runningweb.dto.BoardListDto;
import com.example.runningweb.dto.BoardViewDto;
import com.example.runningweb.repository.BoardRepository;
import com.example.runningweb.repository.FileRepository;
import com.example.runningweb.util.FileUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class BoardService {

    private final BoardRepository boardRepository;
    private final FileRepository fileRepository;
    private final FileUtil fileUtil;


    /**
     * 파일을 만들려다가 실패하는 경우 이전파일을 삭제 해야함
     * --> 일단 파일은 하나만 저장이 가능하므로 추후 개발
     */
    public Long createBoard(BoardDto boardDto, Member member) {
        // board 생성
        Board board = Board.builder()
                .title(boardDto.getTitle())
                .content(boardDto.getContent())
                .writer(member.getNickname())
                .build();

        AttachFile attachFile = null;
        try{
            attachFile = fileUtil.attachFile(boardDto.getAttachFile());
        } catch (IOException e) {
            throw new RuntimeException("내부 서버 에러입니다. 다시 시도해주세요.");
        }

        if(attachFile != null){
            board.saveFiles(attachFile);
        }

        boardRepository.save(board);
        board.UpdateWriter(member);
        return board.getId();
    }

    @Transactional(readOnly = true)
    public BoardViewDto findByBoardId(Long boardId) {
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시판 입니다."));
        List<AttachFile> attachFiles = fileRepository.getAttachFiles(board);

        return BoardViewDto.builder()
                .title(board.getTitle())
                .content(board.getContent())
                .attachFiles(attachFiles)
                .writer(board.getWriter())
                .writeAt(formattingDate(board.getCreatedAt())).build();
    }

    //추후 페이징 처리 필요함.
    public List<BoardListDto> boardList() {
        List<Board> all = boardRepository.findAll();

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
}
