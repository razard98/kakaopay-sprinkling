package kr.per.james.kakaopay.sprinkling.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import kr.per.james.kakaopay.sprinkling.domain.SprinklingOrder;
import kr.per.james.kakaopay.sprinkling.constant.AssignStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class SprinklingInquiryDto {

    @Getter
    @Setter
    @Builder
    @JsonPropertyOrder({"amount","createdAt","assigns"})
    public static class Response {

        private BigDecimal amount;
        private LocalDateTime createdAt;
        private List<AssignInfoDto> assigns;

        public static SprinklingInquiryDto.Response of(final SprinklingOrder sprinklingOrder) {
            return Response.builder()
                    .amount(sprinklingOrder.getAmount())
                    .createdAt(sprinklingOrder.getCreatedAt())
                    .assigns(sprinklingOrder.getSprinklingAssigns().stream().filter(assign -> assign.getStatus().equals(AssignStatus.ASSIGNED))
                            .map(assign -> AssignInfoDto.builder().userId(assign.getUserId()).amount(assign.getAmount()).assignedAt(assign.getAssignedAt()).build())
                            .collect(Collectors.toList())
                    ).build();
        }
    }

}
