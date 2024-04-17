package com.example.runningweb.repository;

import com.example.runningweb.domain.ChattingRoom;
import com.example.runningweb.domain.EnteredRoom;
import com.example.runningweb.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface EnteredRoomRepository extends JpaRepository<EnteredRoom, Long> {


    boolean existsByChattingRoomAndMember(ChattingRoom room, Member member);

    @Modifying
    @Query("delete from EnteredRoom er where er.chattingRoom = :room and er.member = :member")
    int exitRoom(@Param("room") ChattingRoom room, @Param("member") Member member);

    @Modifying
    @Query("update EnteredRoom er set er.updatedAt = now() where er.chattingRoom = :room and er.member = :member")
    int saveExitTime(@Param("room") ChattingRoom room, @Param("member") Member member);

    @Query("select er from EnteredRoom er join fetch er.chattingRoom where er.member = :member")
    List<EnteredRoom> findAttendingRoom(@Param("member") Member member);


    //채팅방에 몇 명 있는지
    @Query("select count(er) from EnteredRoom er where er.chattingRoom = :chattingRoom")
    long countInUser(@Param("chattingRoom") ChattingRoom chattingRoom);


    //roomId에 참여중인 멤버들의 정보를 반환한다.
    @Query("select er.member.nickname from EnteredRoom er join er.member where er.chattingRoom = :chattingRoom")
    List<String> findParticipants(@Param("chattingRoom") ChattingRoom chattingRoom);

    @Query("select er from EnteredRoom er where er.chattingRoom = :room and er.member = :member")
    List<EnteredRoom> getEnteredInfo(@Param("room") ChattingRoom room, @Param("member") Member member);
}
