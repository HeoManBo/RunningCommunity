package com.example.runningweb.repository;

import com.example.runningweb.domain.ChattingRoom;
import com.example.runningweb.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChattingRoomRepository extends JpaRepository<ChattingRoom, Long> {


    ChattingRoom findByUuid(String uuid);


}
