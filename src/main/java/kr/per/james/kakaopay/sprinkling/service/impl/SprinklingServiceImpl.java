package kr.per.james.kakaopay.sprinkling.service.impl;

import kr.per.james.kakaopay.sprinkling.constant.AssignStatus;
import kr.per.james.kakaopay.sprinkling.domain.SprinklingAssign;
import kr.per.james.kakaopay.sprinkling.domain.SprinklingOrder;
import kr.per.james.kakaopay.sprinkling.dto.SprinklingInquiryDto;
import kr.per.james.kakaopay.sprinkling.exception.*;
import kr.per.james.kakaopay.sprinkling.repository.SprinklingAssignRepository;
import kr.per.james.kakaopay.sprinkling.repository.SprinklingOrderRepository;
import kr.per.james.kakaopay.sprinkling.service.SprinklingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SprinklingServiceImpl implements SprinklingService {

    private final static int INQUIRY_DAYS = 7;
    private final static int EXPIRED_MIN = 10;
    private final SprinklingOrderRepository sprinklingOrderRepository;
    private final SprinklingAssignRepository sprinklingAssignRepository;

    @Override
    @Transactional
    public String orderSprinkling(final Integer userId, final String roomId, final Integer count, final BigDecimal amount) {
        SprinklingOrder sprinklingOrder = SprinklingOrder.create(userId, roomId, amount, count);
        sprinklingOrderRepository.save(sprinklingOrder);
        return sprinklingOrder.getToken();
    }

    @Override
    @Transactional
    public BigDecimal assignSprinkling(final Integer userId, final String roomId, final String token) {

        Optional<SprinklingOrder> optSprinklingOrder = sprinklingOrderRepository.findByRoomIdAndToken(roomId, token);
        //대화방 식별값과 토큰이 일치하지 않으면, 대화방에 속한 사용자가 아님
        optSprinklingOrder.orElseThrow(NotBelongRoomException::new);
        //뿌리기 획득 만료 시간
        optSprinklingOrder.filter(o -> !o.isExpired(EXPIRED_MIN)).orElseThrow(ExpiredSprinklingException::new);
        //셀프 받기 확인
        SprinklingOrder sprinklingOrder = optSprinklingOrder.filter(o -> !o.getUserId().equals(userId)).orElseThrow(SelfAssignedException::new);
        //이미 받았는지 확인
        Optional<SprinklingAssign> optAssigned =
                sprinklingAssignRepository.findOneBySprinklingOrderAndUserIdAndStatus(
                        new SprinklingOrder(sprinklingOrder.getOrderId()), userId, AssignStatus.ASSIGNED);
        if (optAssigned.isPresent()) {
            throw new AlreadyAssignedException();
        }
        return doSprinklingAssign(userId, sprinklingOrder);
    }

    @Transactional
    public BigDecimal doSprinklingAssign(Integer userId, SprinklingOrder sprinklingOrder) {
        //할당 되지 않은 1건을 가져온다.
        Optional<SprinklingAssign> optSprinklingAssign =
                sprinklingAssignRepository.findTop1BySprinklingOrderAndStatus(new SprinklingOrder(sprinklingOrder.getOrderId()),
                        AssignStatus.NOT_ASSIGN);

        //할당 가능 건이 없으면 Exception 발생
        SprinklingAssign sprinklingAssign = optSprinklingAssign.orElseThrow(SprinklingCompletedException::new);
        sprinklingAssign.assign(userId);
        return sprinklingAssign.getAmount();
    }

    @Override
    @Transactional(readOnly = true)
    public SprinklingInquiryDto.Response inquireSprinkling(final Integer userId, final String roomId, final String token) {
        SprinklingOrder sprinklingOrder;
        Optional<SprinklingOrder> optSprinklingOrder = sprinklingOrderRepository.findByUserIdAndRoomIdAndToken(userId, roomId, token);
        optSprinklingOrder.orElseThrow(InquiryForbiddenException::new);
        sprinklingOrder =
                optSprinklingOrder.filter(o -> o.getCreatedAt().compareTo(LocalDateTime.now().minusDays(INQUIRY_DAYS)) > 0).orElseThrow(ExpiredInquiryException::new);
        return SprinklingInquiryDto.Response.of(sprinklingOrder);
    }

}
