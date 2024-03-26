package com.example.runningweb.service;

import com.example.runningweb.domain.Board;
import com.example.runningweb.domain.Comment;
import com.example.runningweb.domain.Member;
import com.example.runningweb.dto.CommentDto;
import com.example.runningweb.repository.BoardRepository;
import com.example.runningweb.repository.CommentRepository;
import jakarta.validation.constraints.Null;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;

    public List<CommentDto> commentDtos(Long boardId) {
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new IllegalArgumentException("잘못된 Board번호입니다."));
        List<Comment> comments = commentRepository.findByBoardId(board);

        return comments.stream()
                .map(c -> CommentDto.builder()
                        .writer(c.getMember().getNickname())
                        .content(c.getContent())
                        .wroteAt(c.getCreatedAt())
                        .comment_id(c.getId())
                        .writer_id(c.getMember().getId()).build())
                .collect(Collectors.toList());
    }

    public Long createComment(CommentDto commentDto, Long boardId, Member member){
        Optional<Board> findBoard = boardRepository.findById(boardId); //게시글 찾기
        if(findBoard.isEmpty()) throw new IllegalArgumentException("잘못된 게시글 번호입니다.");

        Comment comment = Comment.builder()
                .parent(null)
                .content(commentDto.getContent())
                .board(findBoard.get())
                .member(member).build();

        Comment save = commentRepository.save(comment);
        return save.getId();
    }

    //댓글 삭제
    public void deleteComment(Long commentId, Member member) {
        //잘못된 댓글 요청이면
        Comment comment = commentRepository.getDeletedComment(commentId)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 삭제 요청입니다."));

        //자신이 작성하지 않은 댓글을 삭제하려는 경우
        if(!comment.getMember().getId().equals(member.getId())){
            throw new IllegalArgumentException("다른 사람의 댓글을 삭제할 수 없습니다.");
        }

        //댓글 삭제 처리
        // findBy 이후에 Delete 로 select 쿼리가 한 번더 나감
        commentRepository.deleteById(commentId);
    }
}
