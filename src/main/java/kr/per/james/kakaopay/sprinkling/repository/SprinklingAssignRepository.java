package kr.per.james.kakaopay.sprinkling.repository;

import kr.per.james.kakaopay.sprinkling.constant.AssignStatus;
import kr.per.james.kakaopay.sprinkling.domain.SprinklingAssign;
import kr.per.james.kakaopay.sprinkling.domain.SprinklingOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import javax.persistence.LockModeType;
import java.util.Optional;

public interface SprinklingAssignRepository extends JpaRepository<SprinklingAssign, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<SprinklingAssign> findTop1BySprinklingOrderAndStatus(SprinklingOrder orderId, AssignStatus status);

    Optional<SprinklingAssign> findOneBySprinklingOrderAndUserIdAndStatus(SprinklingOrder orderId, Integer userId, AssignStatus status);

}
