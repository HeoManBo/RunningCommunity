package com.example.runningweb.repository.mysql;

import com.example.runningweb.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    @Query("select m from Member m where m.username = :username and m.isDeleted = 0")
    Member findByUsername(@Param("username") String username);

    @Query("select m from Member m where m.email = :email and m.isDeleted = 0")
    Optional<Member> findByEmail(@Param("email") String email);
}
