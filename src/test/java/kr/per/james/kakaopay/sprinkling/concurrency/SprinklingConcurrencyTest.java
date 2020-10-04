package kr.per.james.kakaopay.sprinkling.concurrency;

import kr.per.james.kakaopay.sprinkling.dto.SprinklingInquiryDto;
import kr.per.james.kakaopay.sprinkling.service.SprinklingService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Slf4j
@ActiveProfiles("test")
@DisplayName("동시성 테스트")
class SprinklingConcurrencyTest {
    @Autowired
    private SprinklingService sprinklingService;

    private Integer userId;
    private String roomId;
    private Integer count;
    private BigDecimal totalAmount;

    @BeforeEach
    void setUp() {
        userId = 1;
        roomId = "room-01";
        count = 100;
        totalAmount = new BigDecimal(100000000);
    }

    @Test
    //@Disabled
    @DisplayName("조회,받기 동시성 테스트")
    void testAssignConcurrency() throws InterruptedException {
        //given
        final int threadCount = 100;
        List<Integer> users = new ArrayList<>();
        for (int i = 2; i < count + 2; i++) {
            users.add(i);
        }
        final String token = sprinklingService.orderSprinkling(userId, roomId, count, totalAmount);
        //when
        final ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        for (Integer expectedUserId : users) {
            executor.execute(() -> sprinklingService.assignSprinkling(expectedUserId, roomId, token));
            executor.execute(() -> {
                SprinklingInquiryDto.Response inquiry = sprinklingService.inquireSprinkling(userId, roomId, token);
            });
        }
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);
        SprinklingInquiryDto.Response inquiry = sprinklingService.inquireSprinkling(userId, roomId, token);
        log.debug("========> assign size:{}", inquiry.getAssigns().size());
        //then
        assertThat(count).isEqualTo(inquiry.getAssigns().size());
    }
}
