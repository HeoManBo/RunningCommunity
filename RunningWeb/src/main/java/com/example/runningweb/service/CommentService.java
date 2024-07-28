package com.example.runningweb.service;

import com.example.runningweb.domain.Board;
import com.example.runningweb.domain.Comment;
import com.example.runningweb.domain.Member;
import com.example.runningweb.dto.CommentCreateRequest;
import com.example.runningweb.dto.CommentDto;
import com.example.runningweb.dto.UpdateCommentRequest;
import com.example.runningweb.repository.BoardRepository;
import com.example.runningweb.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
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

    @Transactional
    public Long createComment(CommentCreateRequest commentDto, Long boardId, Member member){
        Optional<Board> findBoard = boardRepository.findById(boardId); //게시글 찾기
        if(findBoard.isEmpty()) throw new IllegalArgumentException("잘못된 게시글 번호입니다.");

        //부모 댓글 찾기
        Optional<Comment> parentComment = commentRepository.findById(commentDto.getParentId());

        Comment comment = Comment.builder()
                .parent(parentComment.orElse(null))
                .content(commentDto.getContent())
                .board(findBoard.get())
                .member(member).build();

        Comment save = commentRepository.save(comment);
        return save.getId();
    }

    //댓글 삭제
    public void deleteComment(Long commentId, Member member) {
        //잘못된 댓글 요청이면
        Comment comment = commentRepository.getCommentWithMember(commentId)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 삭제 요청입니다."));

        //자신이 작성하지 않은 댓글을 삭제하려는 경우
        checkCommentOwner(member, comment);

        //댓글 삭제 처리
        // findBy 이후에 Delete 로 select 쿼리가 한 번더 나감
        commentRepository.deleteById(commentId);
    }

    // boardId 에 해당하는 모든 게시글을 삭제함.
    public void deleteComments(Board board) {
        commentRepository.deleteByBoard(board);
    }

    // 대댓글 조합
    public List<CommentDto> getCommentsWithHier(Long boardId) {
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new IllegalArgumentException("잘못된 Board번호입니다."));
        List<Comment> comments = commentRepository.findByHier(board);

        Map<Long, CommentDto> commentMap = new HashMap<>();
        List<CommentDto> commentDtos = new ArrayList<>();
        comments.forEach(c -> {
            CommentDto dto = CommentDto.builder()
                    .comment_id(c.getId())
                    .wroteAt(c.getCreatedAt())
                    .writer_id(c.getMember().getId())
                    .content(c.getContent())
                    .writer(c.getMember().getNickname()).build();
            commentMap.put(dto.getComment_id(), dto);
            if (c.getParent() != null) commentMap.get(c.getParent().getId()).addChild(dto);
            else commentDtos.add(dto);
        });

        return commentDtos;
    }

    @Transactional
    public void updateComment(Long commentId, Member member, UpdateCommentRequest request) {
        Comment comment = commentRepository
                .getCommentWithMember(commentId).orElseThrow(() -> new IllegalArgumentException("잘못된 댓글입니다."));

        checkCommentOwner(member, comment);

        comment.updateComment(request.getUpdateComment());
    }


    // 수정 가능한지 검증
    private void checkCommentOwner(Member member, Comment comment) {
        if(!comment.getMember().getId().equals(member.getId())){
            throw new IllegalArgumentException("다른 사람의 댓글을 조작할 수 없습니다.");
        }
    }

}
