package com.example.runningweb.comment;


import com.example.runningweb.domain.Board;
import com.example.runningweb.domain.Comment;
import com.example.runningweb.domain.Member;
import com.example.runningweb.repository.BoardRepository;
import com.example.runningweb.repository.CommentRepository;
import com.example.runningweb.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;


import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class CommentTest {

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    // Spring security setup
    @BeforeEach
    public void before(){
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @Transactional(readOnly = true)
    void 대댓글테스트() throws Exception {
        //회원 가입
        Member member = createMockUser("test");
        Board board = createAndSaveBoardForTest();

        //최상위 댓글 2개
        Comment parent1 = createComment("1", null, member, board);
        Comment parent8 = createComment("8", null, member, board);

        Comment comment2 = createComment("2", parent1, member, board);
        Comment comment3 = createComment("3", parent1, member, board);
        Comment comment4 = createComment("4", comment2, member, board);
        Comment comment5 = createComment("5", comment2, member, board);
        Comment comment6 = createComment("6", comment4, member, board);
        Comment comment7 = createComment("7", comment3, member, board);

        ResultActions result = mockMvc.perform(get("/comment/test")
                .param("boardId", String.valueOf(board.getId()))
                .with(csrf()));


        result.andExpect(status().isOk())
                .andDo(print());
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

    //댓글 내용, 부모 내용
    private Comment createComment(String content, Comment parent, Member member, Board board) {
        Comment comment = Comment.builder()
                .content(content)
                .parent(parent)
                .member(member)
                .board(board)
                .build();
        commentRepository.save(comment);
        return comment;
    }
}
