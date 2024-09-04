package com.example.runningweb.repository;

import com.example.runningweb.domain.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {

    //@Query("select b from (select bb from Board bb where bb.id = :boardId) as b join fetch b.member")
    @Query("select b from Board b left join fetch b.member where b.id = :boardId")
    List<Board> getBoardWithUser(@Param("boardId") Long boardId);

    // 만약 첨부파일이 하나도 없는 경우 join fetch를 걸어버리면 board를 못가져오므로 left join fetch를 하자.
    @Query("select b from Board b left join fetch b.files where b.id = :boardId")
    Board getBoardWithFile(@Param("boardId") Long boardId);

    // 삭제하려는 게시판의 모든 정보를 가져온다,
    @Query("select b from Board b join fetch b.member left join fetch b.files where b.id = :boardId")
    Optional<Board> findBoardAllInfo(@Param("boardId") Long boardId);

    @Modifying
    @Query("update Board b set b.view = b.view + 1 where b.id = :boardId")
    void updateViewCount(@Param("boardId") Long boardId);

    //페이징 처리를 이용한 게시판 조회
    // 1:N fetch join 후 paging 이므로  firstResult/maxResults specified with collection fetch; applying in memory 경고 발생
    // 조인 제거 후 default_batch_size 를 이용하자
    // Board만 paging 처리 후 페이징된 게시판의 댓글은  In절로 가져오고 comment를 넣어줌
    @Query(value = "select b from Board b ",
            countQuery = "select count(b) from Board b")
    Page<Board> findByPagingBoard(Pageable pageable);
}
