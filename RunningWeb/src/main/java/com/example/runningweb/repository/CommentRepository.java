package com.example.runningweb.repository;


import com.example.runningweb.domain.Board;
import com.example.runningweb.domain.Comment;
import com.example.runningweb.dto.CommentDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {


    @Query("select c from Comment c join fetch c.member where c.board = :board order by c.createdAt desc ")
    List<Comment> findByBoardId(@Param("board") Board boardId);

    //삭제할 쿼리 하나를 가져옴 --> 없으면 NULL;
    // @Query("select c from (select cc from Comment as cc where cc.id = :comment_id) as c join fetch c.member ")
    @Query("select c from Comment c join fetch c.member where c.id = :comment_id")
    Optional<Comment> getDeletedComment(@Param("comment_id") Long comment_id);


}
