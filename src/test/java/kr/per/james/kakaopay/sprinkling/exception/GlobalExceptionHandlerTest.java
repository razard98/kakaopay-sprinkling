package kr.per.james.kakaopay.sprinkling.exception;

import kr.per.james.kakaopay.sprinkling.constant.Headers;
import kr.per.james.kakaopay.sprinkling.controller.SprinklingApiController;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@ExtendWith(SpringExtension.class)
@WebMvcTest(SprinklingApiController.class)
@DisplayName("예외처리테스트")
class GlobalExceptionHandlerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SprinklingApiController sprinklingApiController;

    private static final Integer userId = 1;
    private static final String roomId = "room-01";
    private static final String token = "ABC";

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(sprinklingApiController).setControllerAdvice(new GlobalExceptionHandler()).build();
    }

    @Test
    @DisplayName("뿌린 사람 자신만 조회를 할 수 있습니다.")
    void handleInquiryForbiddenException() throws Exception {
        //given
        //when
        when(sprinklingApiController.inquireSprinkling(any(Integer.class), any(String.class), any(String.class)))
                .thenThrow(new InquiryForbiddenException());
        //then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/sprinkling/tokens/{token}", token)
                .accept(MediaType.APPLICATION_JSON)
                .header(Headers.X_ROOMS_ID, roomId)
                .header(Headers.X_USER_ID, userId))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("뿌린 건에 대한 조회는 7일 동안 할 수 있습니다.")
    void handleExpiredInquiryException() throws Exception {
        //given
        //when
        when(sprinklingApiController.inquireSprinkling(any(Integer.class), any(String.class), any(String.class)))
                .thenThrow(new ExpiredInquiryException());

        //then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/sprinkling/tokens/{token}", token)
                .accept(MediaType.APPLICATION_JSON)
                .header(Headers.X_ROOMS_ID, roomId)
                .header(Headers.X_USER_ID, userId))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("받을 사용자 수가 뿌릴 금액 보다 클 수는 없습니다.")
    void handleIllegalArgumentException() throws Exception {
        //when
        when(sprinklingApiController.assignSprinkling(any(Integer.class), any(String.class), any(String.class)))
                .thenThrow(new IllegalArgumentException());
        //then
        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/sprinkling/tokens/{token}", token)
                .accept(MediaType.APPLICATION_JSON)
                .header(Headers.X_ROOMS_ID, roomId)
                .header(Headers.X_USER_ID, userId))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("뿌리기가 호출된 대화방과 동일한 대화방에 속한 사용자만 받을 수 있습니다.")
    void handleNotBelongRoomException() throws Exception {
        //given
        //when
        when(sprinklingApiController.assignSprinkling(any(Integer.class), any(String.class), any(String.class)))
                .thenThrow(new NotBelongRoomException());
        //then
        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/sprinkling/tokens/{token}", token)
                .accept(MediaType.APPLICATION_JSON)
                .header(Headers.X_ROOMS_ID, roomId)
                .header(Headers.X_USER_ID, userId))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("뿌린 건은 10분간만 유효합니다.")
    void handleExpiredSprinklingException() throws Exception {
        //given
        //when
        when(sprinklingApiController.assignSprinkling(any(Integer.class), any(String.class), any(String.class)))
                .thenThrow(new ExpiredSprinklingException());
        //then
        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/sprinkling/tokens/{token}", token)
                .accept(MediaType.APPLICATION_JSON)
                .header(Headers.X_ROOMS_ID, roomId)
                .header(Headers.X_USER_ID, userId))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("뿌리기 당 한번만 받을 수 있습니다.")
    void handleAlreadyAssignedException() throws Exception {
        //given
        //when
        when(sprinklingApiController.assignSprinkling(any(Integer.class), any(String.class), any(String.class)))
                .thenThrow(new AlreadyAssignedException());
        //then
        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/sprinkling/tokens/{token}", token)
                .accept(MediaType.APPLICATION_JSON)
                .header(Headers.X_ROOMS_ID, roomId)
                .header(Headers.X_USER_ID, userId))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("뿌리기가 모두 완료 되어, 받기에 실패하였습니다.")
    void handleSprinklingCompletedException() throws Exception {
        //given
        //when
        when(sprinklingApiController.assignSprinkling(any(Integer.class), any(String.class), any(String.class)))
                .thenThrow(new SprinklingCompletedException());
        //then
        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/sprinkling/tokens/{token}", token)
                .accept(MediaType.APPLICATION_JSON)
                .header(Headers.X_ROOMS_ID, roomId)
                .header(Headers.X_USER_ID, userId))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("자신이 뿌리기한 건은 자신이 받을 수 없습니다.")
    void handleSelfAssignedException() throws Exception {
        //given
        //when
        when(sprinklingApiController.assignSprinkling(any(Integer.class), any(String.class), any(String.class)))
                .thenThrow(new SelfAssignedException());
        //then
        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/sprinkling/tokens/{token}", token)
                .accept(MediaType.APPLICATION_JSON)
                .header(Headers.X_ROOMS_ID, roomId)
                .header(Headers.X_USER_ID, userId))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

}