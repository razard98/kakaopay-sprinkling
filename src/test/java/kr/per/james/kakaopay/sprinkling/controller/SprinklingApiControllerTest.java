package kr.per.james.kakaopay.sprinkling.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.per.james.kakaopay.sprinkling.constant.Headers;
import kr.per.james.kakaopay.sprinkling.dto.SprinklingInquiryDto;
import kr.per.james.kakaopay.sprinkling.dto.SprinklingOrderDto;
import kr.per.james.kakaopay.sprinkling.service.SprinklingService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.util.Collections;

import static org.hamcrest.Matchers.hasLength;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@ExtendWith(SpringExtension.class)
@WebMvcTest(SprinklingApiController.class)
@DisplayName("뿌리기 API")
class SprinklingApiControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private SprinklingService sprinklingService;
    private ObjectMapper mapper = new ObjectMapper();

    private static final Integer userId = 1;
    private static final Integer count = 10;
    private static final BigDecimal amount = new BigDecimal(10000);
    private static final String roomId = "room-01";
    private static final String token = "ABC";

    @BeforeEach
    void setUp() {

    }

    @Test
    @DisplayName("뿌리기 API")
    void testOrderSprinkling() throws Exception {
        //given
        final SprinklingOrderDto.Request request = SprinklingOrderDto.Request.builder().amount(amount).count(count).build();
        //when
        when(sprinklingService.orderSprinkling(any(Integer.class), any(String.class), any(Integer.class), any(BigDecimal.class)))
                .thenReturn(token);
        //then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/sprinkling")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header(Headers.X_ROOMS_ID, roomId)
                .header(Headers.X_USER_ID, userId)
                .content(mapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token", hasLength(3)))
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("받기 API")
    void testAssignSprinkling() throws Exception {
        //given
        final BigDecimal expectedAmount = new BigDecimal(10);
        //when
        when(sprinklingService.assignSprinkling(any(Integer.class), any(String.class), any(String.class)))
                .thenReturn(expectedAmount);
        //then
        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/sprinkling/tokens/{token}", token)
                .accept(MediaType.APPLICATION_JSON)
                .header(Headers.X_ROOMS_ID, roomId)
                .header(Headers.X_USER_ID, userId))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.amount", is(expectedAmount.intValue())))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("조회 API")
    void testInquireSprinkling() throws Exception {
        //given
        final SprinklingInquiryDto.Response response = SprinklingInquiryDto.Response.builder()
                .amount(new BigDecimal(10000))
                .assigns(Collections.emptyList())
                .build();
        //when
        when(sprinklingService.inquireSprinkling(any(Integer.class), any(String.class), any(String.class)))
                .thenReturn(response);
        //then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/sprinkling/tokens/{token}", token)
                .accept(MediaType.APPLICATION_JSON)
                .header(Headers.X_ROOMS_ID, roomId)
                .header(Headers.X_USER_ID, userId))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.amount", is(amount.intValue())))
                .andExpect(status().isOk());
    }
}