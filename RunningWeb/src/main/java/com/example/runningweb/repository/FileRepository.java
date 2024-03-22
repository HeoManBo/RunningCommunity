package com.example.runningweb.repository;

import com.example.runningweb.domain.AttachFile;
import com.example.runningweb.domain.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileRepository extends JpaRepository<AttachFile, Long> {

    @Query("select a from AttachFile a where a.board = :board")
    public List<AttachFile> getAttachFiles(@Param("board") Board board);

}
