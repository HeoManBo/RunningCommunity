package com.example.runningweb.repository;

import com.example.runningweb.domain.ChattingRoom;
import com.example.runningweb.domain.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {


    @Query("select m from Message m join fetch m.writer where m.chattingRoom = :chattingRoom order by m.createdAt desc")
    List<Message> findByChattingRoom(@Param("chattingRoom") ChattingRoom chattingRoom);
}
