package com.example.runningweb.repository;

import com.example.runningweb.domain.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {

    //@Query("select b from (select bb from Board bb where bb.id = :boardId) as b join fetch b.member")
    @Query("select b from Board b join fetch b.member where b.id = :boardId")
    List<Board> getBoardWithUser(@Param("boardId") Long boardId);

    // 만약 첨부파일이 하나도 없는 경우 join fetch를 걸어버리면 board를 못가져오므로 left join fetch를 하자.
    @Query("select b from Board b left join fetch b.files where b.id = :boardId")
    Board getBoardWithFile(@Param("boardId") Long boardId);

    // 삭제하려는 게시판의 모든 정보를 가져온다,
    @Query("select b from Board b join fetch b.member left join fetch b.files where b.id = :boardId")
    Optional<Board> findBoardAllInfo(@Param("boardId") Long boardId);

}
