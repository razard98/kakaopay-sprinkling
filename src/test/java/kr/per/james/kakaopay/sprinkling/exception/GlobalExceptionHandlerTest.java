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
@DisplayName("GlobalException")
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
    void handleNotBelongRoomException() throws Exception {
        //given
        //when
        when(sprinklingApiController.inquireSprinkling(any(Integer.class), any(String.class), any(String.class)))
                .thenThrow(new NotBelongRoomException());
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
    void handleExpiredSprinklingException() throws Exception {
        //given
        //when
        when(sprinklingApiController.inquireSprinkling(any(Integer.class), any(String.class), any(String.class)))
                .thenThrow(new ExpiredSprinklingException());
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
    void handleAlreadyAssignedException() throws Exception {
        //given
        //when
        when(sprinklingApiController.inquireSprinkling(any(Integer.class), any(String.class), any(String.class)))
                .thenThrow(new AlreadyAssignedException());
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
    void handleSprinklingCompletedException() throws Exception {
        //given
        //when
        when(sprinklingApiController.inquireSprinkling(any(Integer.class), any(String.class), any(String.class)))
                .thenThrow(new SprinklingCompletedException());
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
    void handleSelfAssignedException() throws Exception {
        //given
        //when
        when(sprinklingApiController.inquireSprinkling(any(Integer.class), any(String.class), any(String.class)))
                .thenThrow(new SelfAssignedException());
        //then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/sprinkling/tokens/{token}", token)
                .accept(MediaType.APPLICATION_JSON)
                .header(Headers.X_ROOMS_ID, roomId)
                .header(Headers.X_USER_ID, userId))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

}