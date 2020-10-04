package kr.per.james.kakaopay.sprinkling.domain;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Getter
@Table(name = "sprinkling_order")
public class SprinklingOrder implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "token", length = 3, nullable = false)
    private String token;

    @Column(name = "room_id", nullable = false)
    private String roomId;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "count", nullable = false)
    private int count;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "sprinklingOrder", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<SprinklingAssign> sprinklingAssigns = new ArrayList<>();

    public void addSprinklingAssign(final SprinklingAssign sprinklingAssign) {
        sprinklingAssigns.add(sprinklingAssign);
        sprinklingAssign.setSprinklingOrder(this);
    }

    @Builder
    private SprinklingOrder(final String token, final String roomId, final Integer userId, final BigDecimal amount,
                            final Integer count, final LocalDateTime createdAt) {
        this.token = token;
        this.roomId = roomId;
        this.userId = userId;
        this.amount = amount;
        this.count = count;
        this.createdAt = createdAt;
    }

    public boolean isExpired(final int minutes) {
        return createdAt.isBefore(LocalDateTime.now().minusMinutes(minutes));
    }

    public static SprinklingOrder create(final Integer userId, final String roomId, final BigDecimal amount, final int count) {
        SprinklingOrder sprinklingOrder = SprinklingOrder.builder()
                .token(createToken(3)).roomId(roomId).userId(userId).amount(amount).count(count)
                .createdAt(LocalDateTime.now())
                .build();
        long[] randomAmounts = getRandomDivideAmounts(amount.longValue(), count);
        for (long randomAmount : randomAmounts) {
            sprinklingOrder.addSprinklingAssign(SprinklingAssign.create(new BigDecimal(randomAmount)));
        }
        return sprinklingOrder;
    }

    static String createToken(final int length) {
        return RandomStringUtils.random(length, true, true);
    }

    static long[] getRandomDivideAmounts(long totalAmount, final int count) {
        if (count > totalAmount) {
            throw new IllegalArgumentException("받을 사용자 수가 뿌릴 금액 보다 클 수는 없습니다.");
        }
        long initBoundAmount = getInitBoundAmount(totalAmount, count);
        log.debug("initBoundAmount:{}", initBoundAmount);
        long[] randomAmounts = new long[count];
        for (int i = 0; i < randomAmounts.length - 1; i++) {
            randomAmounts[i] = ThreadLocalRandom.current().nextLong(1, Math.min(totalAmount, initBoundAmount));
            totalAmount -= randomAmounts[i];
        }
        randomAmounts[randomAmounts.length - 1] = totalAmount;
        return randomAmounts;
    }

    static long getInitBoundAmount(long totalAmount, int count) {
        // 총금액 > 받을 사용자 수 * 2 보다 작을 경우 최소 분배 되도록 bound 금액을 설정 한다.
        long initBoundAmount = ThreadLocalRandom.current().nextLong((totalAmount / count), totalAmount > count * 2 ? (totalAmount / count) * 2 :
                (totalAmount / count) + 1);
        return (initBoundAmount > 1) ? initBoundAmount : initBoundAmount + 1;
    }
}
