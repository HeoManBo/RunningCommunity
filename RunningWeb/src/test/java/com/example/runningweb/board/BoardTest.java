package com.example.runningweb.board;


import com.example.runningweb.domain.Board;
import com.example.runningweb.domain.Member;
import com.example.runningweb.repository.BoardRepository;
import com.example.runningweb.repository.MemberRepository;
import com.example.runningweb.service.BoardService;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
//@Transactional // @Transactional을 주석처리하면 테스트가 수행됨.
public class BoardTest {

    @Autowired
    private BoardService boardService;

    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private EntityManager em;

    @Autowired
    private MemberRepository memberRepository;


    @Test
    //@Transactional //주석 처리하면 테스트 통과됨
    public void 조회수_테스트() throws InterruptedException {
        Board board = createAndSaveBoardForTest(); // 게시판을 저장하는 함수
        Long viewId = board.getId();
        List<Board> all = boardRepository.findAll(); //여기서 1개가 찍힘 --> 로컬에 반영되어있기 때문
        //em.flush(); // DB에 반영

        // 쓰레드 2개가 조회를 1000번씩 총 2000회 조회
        Runnable viewBoard = () -> {
            for(int count=1; count<=1000; count++){
                //System.out.println("In Thread viewId : " +  viewId);
                boardService.findByBoardId(viewId);
            }
        };

        Thread threadA = new Thread(viewBoard);
        Thread threadB = new Thread(viewBoard);

        //조회 시작
        threadA.start();
        threadB.start();

        //조회 종료 대기
        threadA.join();
        threadB.join();

        //최종적으로 DB를 조회했을 때 조회수는 2000이어야한다.
        Board resultBoard = boardRepository.findById(viewId).get();
        Assertions.assertThat(resultBoard.getView()).isEqualTo(2000);
    }

    private Member createMockUser(String username) {
        Member member = Member.builder()
                .userName(username)
                .password("1234")
                .nickname("test")
                .email("test@test.com")
                .build();
        return memberRepository.save(member);
    }

    private Board createAndSaveBoardForTest() {
        Member testUser = createMockUser("test");
        Board board = Board.builder()
                .title("TEST")
                .content("TEST")
                .writer("TEST")
                .member(testUser)
                .build();
        return boardRepository.save(board);
    }
}
