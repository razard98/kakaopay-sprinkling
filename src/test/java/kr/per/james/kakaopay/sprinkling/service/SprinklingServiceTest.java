package kr.per.james.kakaopay.sprinkling.service;

import kr.per.james.kakaopay.sprinkling.constant.AssignStatus;
import kr.per.james.kakaopay.sprinkling.domain.SprinklingAssign;
import kr.per.james.kakaopay.sprinkling.domain.SprinklingOrder;
import kr.per.james.kakaopay.sprinkling.exception.*;
import kr.per.james.kakaopay.sprinkling.repository.SprinklingAssignRepository;
import kr.per.james.kakaopay.sprinkling.repository.SprinklingOrderRepository;
import kr.per.james.kakaopay.sprinkling.service.impl.SprinklingServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("뿌리기서비스")
class SprinklingServiceTest {

    @InjectMocks
    private SprinklingServiceImpl sprinklingService;

    @Mock
    private SprinklingOrderRepository sprinklingOrderRepository;

    @Mock
    private SprinklingAssignRepository sprinklingAssignRepository;

    private static final int userId = 1;
    private static final String roomId = "room-01";
    private static final String token = "ABC";

    @Test
    @DisplayName("뿌리기가 호출된 대화방과 동일한 대화방에 속한 사용자만 받을 수 있습니다.")
    void testExpectedNotBelongRoomException() {
        //given : Optional.empty()
        //when
        when(sprinklingOrderRepository.findByRoomIdAndToken(roomId, token)).thenReturn(Optional.empty());
        //then
        assertThatThrownBy(() -> sprinklingService.assignSprinkling(userId, roomId, token))
                .isInstanceOf(NotBelongRoomException.class);
    }

    @Test
    @DisplayName("뿌린 건은 10분간만 유효합니다.")
    void testExpectedExpiredSprinklingException() {
        //given
        final SprinklingOrder sprinklingOrder = SprinklingOrder.builder()
                .userId(userId).roomId(roomId).token(token).amount(new BigDecimal(10000)).count(10)
                .createdAt(LocalDateTime.now().minusMinutes(15))
                .build();
        //when
        when(sprinklingOrderRepository.findByRoomIdAndToken(roomId, token)).thenReturn(Optional.of(sprinklingOrder));
        //then
        assertThatThrownBy(() -> sprinklingService.assignSprinkling(userId, roomId, token))
                .isInstanceOf(ExpiredSprinklingException.class);
    }

    @Test
    @DisplayName("자신이 뿌리기한 건은 자신이 받을 수 없습니다.")
    void testExpectedSelfAssignedException() {
        //given
        final SprinklingOrder sprinklingOrder = SprinklingOrder.builder()
                .userId(userId).roomId(roomId).token(token).amount(new BigDecimal(10000)).count(10)
                .createdAt(LocalDateTime.now())
                .build();
        //when
        when(sprinklingOrderRepository.findByRoomIdAndToken(roomId, token)).thenReturn(Optional.of(sprinklingOrder));
        //then
        assertThatThrownBy(() -> sprinklingService.assignSprinkling(userId, roomId, token))
                .isInstanceOf(SelfAssignedException.class);
    }

    @Test
    @DisplayName("뿌리기 당 한번만 받을 수 있습니다.")
    void testExpectedAlreadyAssignedException() {
        //given
        final int expectUserId = 2;
        final SprinklingOrder sprinklingOrder = SprinklingOrder.builder()
                .userId(userId).roomId(roomId).token(token).amount(new BigDecimal(10000)).count(10)
                .createdAt(LocalDateTime.now())
                .build();
        final SprinklingAssign sprinklingAssign = new SprinklingAssign(expectUserId, new BigDecimal(10000));
        sprinklingOrder.addSprinklingAssign(new SprinklingAssign(expectUserId, new BigDecimal(10)));
        //when
        when(sprinklingOrderRepository.findByRoomIdAndToken(roomId, token)).thenReturn(Optional.of(sprinklingOrder));
        when(sprinklingAssignRepository.findOneBySprinklingOrderAndUserIdAndStatus(any(SprinklingOrder.class), any(Integer.class),
                any(AssignStatus.class)))
                .thenReturn(Optional.of(sprinklingAssign));
        //then
        assertThatThrownBy(() -> sprinklingService.assignSprinkling(expectUserId, roomId, token))
                .isInstanceOf(AlreadyAssignedException.class);
    }

    @Test
    @DisplayName("뿌리기가 모두 완료 되어, 받기에 실패하였습니다.")
    void testExpectedSprinklingCompletedException() {
        //given
        final int expectUserId = 4;
        final SprinklingOrder sprinklingOrder = SprinklingOrder.builder()
                .userId(userId).roomId(roomId).token(token).amount(new BigDecimal(10000)).count(2)
                .createdAt(LocalDateTime.now())
                .build();
        sprinklingOrder.addSprinklingAssign(new SprinklingAssign(2, new BigDecimal(5000)));
        sprinklingOrder.addSprinklingAssign(new SprinklingAssign(3, new BigDecimal(5000)));
        //when
        when(sprinklingOrderRepository.findByRoomIdAndToken(roomId, token)).thenReturn(Optional.of(sprinklingOrder));
        //then
        assertThatThrownBy(() -> sprinklingService.assignSprinkling(expectUserId, roomId, token))
                .isInstanceOf(SprinklingCompletedException.class);
    }

    @Test
    @DisplayName("뿌린 사람 자신만 조회를 할 수 있습니다.")
    void testExpectedInquiryForbiddenException() {
        //given : Optional.empty()
        //when
        when(sprinklingOrderRepository.findByUserIdAndRoomIdAndToken(userId, roomId, token)).thenReturn(Optional.empty());
        //then
        assertThatThrownBy(() -> sprinklingService.inquireSprinkling(userId, roomId, token))
                .isInstanceOf(InquiryForbiddenException.class);
    }

    @Test
    @DisplayName("뿌린 건에 대한 조회는 7일 동안 할 수 있습니다.")
    void testExpectedExpiredInquiryException() {
        //given
        final SprinklingOrder sprinklingOrder = SprinklingOrder.builder()
                .userId(userId).roomId(roomId).token(token).amount(new BigDecimal(10000)).count(10)
                .createdAt(LocalDateTime.now().minusDays(10))
                .build();
        //when
        when(sprinklingOrderRepository.findByUserIdAndRoomIdAndToken(userId, roomId, token))
                .thenReturn(Optional.of(sprinklingOrder));
        //then
        assertThatThrownBy(() -> sprinklingService.inquireSprinkling(userId, roomId, token))
                .isInstanceOf(ExpiredInquiryException.class);
    }
}