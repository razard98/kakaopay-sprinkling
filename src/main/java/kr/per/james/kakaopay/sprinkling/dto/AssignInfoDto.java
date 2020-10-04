package kr.per.james.kakaopay.sprinkling.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
public class AssignInfoDto {
    private Integer userId;
    private BigDecimal amount;
    private LocalDateTime assignedAt;

    @Builder
    public AssignInfoDto(final Integer userId, final BigDecimal amount, final LocalDateTime assignedAt) {
        this.userId = userId;
        this.amount = amount;
        this.assignedAt = assignedAt;
    }
}
