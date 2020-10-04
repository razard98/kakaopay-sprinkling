package kr.per.james.kakaopay.sprinkling.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("뿌리기주문도메인")
class SprinklingOrderTest {

    private Integer userId;
    private String roomId;
    private Integer count;
    private BigDecimal totalAmount;

    @BeforeEach
    void setUp() {
        userId = 1004;
        roomId = "room-01";
        count = 10;
        totalAmount = new BigDecimal(10);
    }

    @Test
    @DisplayName("사용자에게 금액 분배 로직을 검증 합니다.(분배 후 합계)")
    void testRandomDivideAmounts() {
        long sumAmount = 0L;
        final long[] randomAmounts = SprinklingOrder.getRandomDivideAmounts(totalAmount.longValue(), count);
        for (long randomAmount : randomAmounts) {
            sumAmount += randomAmount;
        }
        assertThat(totalAmount.longValue()).isEqualTo(sumAmount);
    }

    @Test
    @DisplayName("Bound 금액 설정을 검증 합니다.")
    void testInitBoundAmount() {
        final long expectedBoundAmount = SprinklingOrder.getInitBoundAmount(100, 10);
        assertThat(expectedBoundAmount).isGreaterThanOrEqualTo(totalAmount.longValue() / count);
    }

    @Test
    @DisplayName("받을 사용자 수가 뿌릴 금액 보다 클 수는 없습니다.")
    void testExpectedIllegalArgumentException() {
        final long totalAmount = 10;
        final int count = 11;
        assertThatThrownBy(() -> SprinklingOrder.getRandomDivideAmounts(totalAmount, count))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("토큰생성 3자릿수를 검증 합니다.")
    void testGetToken() {
        final String token = SprinklingOrder.createToken(3);
        assertThat(token.length()).isEqualTo(3);
    }

    @Test
    @DisplayName("뿌리기 기능 검증을 합니다.")
    void testCreate() {
        final SprinklingOrder sprinklingOrder = SprinklingOrder.create(userId, roomId, totalAmount, count);
        assertThat(sprinklingOrder.getToken().length()).isEqualTo(3);
    }

}