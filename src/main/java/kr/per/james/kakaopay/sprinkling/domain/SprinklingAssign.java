package kr.per.james.kakaopay.sprinkling.domain;

import kr.per.james.kakaopay.sprinkling.constant.AssignStatus;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Getter
@DynamicUpdate
@Table(name = "sprinkling_assign")
public class SprinklingAssign {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "assign_id")
    private Long assignId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orderId")
    @ToString.Exclude
    @Setter
    private SprinklingOrder sprinklingOrder;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AssignStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "assigned_at")
    private LocalDateTime assignedAt;
    
    public SprinklingAssign(final Integer userId, final BigDecimal amount) {
        this.amount = amount;
        this.userId = userId;
        this.status = AssignStatus.ASSIGNED;
    }

    private SprinklingAssign(final BigDecimal amount, final AssignStatus status) {
        this.amount = amount;
        this.status = status;
    }

    static SprinklingAssign create(final BigDecimal amount) {
        return new SprinklingAssign(amount, AssignStatus.NOT_ASSIGN);
    }

    public void assign(final Integer userId) {
        this.assignedAt = LocalDateTime.now();
        this.userId = userId;
        this.status = AssignStatus.ASSIGNED;
    }
}
