package kr.per.james.kakaopay.sprinkling.service;

import kr.per.james.kakaopay.sprinkling.domain.SprinklingOrder;
import kr.per.james.kakaopay.sprinkling.dto.SprinklingInquiryDto;

import java.math.BigDecimal;

public interface SprinklingService {

    String orderSprinkling(final Integer userId, final String roomId, final Integer count, final BigDecimal amount);

    BigDecimal assignSprinkling(final Integer userId, final String roomId, final String token);

    BigDecimal doSprinklingAssign(Integer userId, SprinklingOrder sprinklingOrder);

    SprinklingInquiryDto.Response inquireSprinkling(final Integer userId, final String roomId, final String token);
}
