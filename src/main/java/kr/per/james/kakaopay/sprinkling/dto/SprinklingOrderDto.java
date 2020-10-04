package kr.per.james.kakaopay.sprinkling.dto;

import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class SprinklingOrderDto {

    @Getter
    @Builder
    public static class Request {
        @NotNull
        private Integer count;
        @NotNull
        private BigDecimal amount;
    }

    @Getter
    @Builder
    public static class Response {
        private String token;
    }
}
