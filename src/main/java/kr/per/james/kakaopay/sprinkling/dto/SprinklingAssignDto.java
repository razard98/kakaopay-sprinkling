package kr.per.james.kakaopay.sprinkling.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

public class SprinklingAssignDto {

    @Getter
    @Builder
    public static class Response {
        private BigDecimal amount;
    }

}
