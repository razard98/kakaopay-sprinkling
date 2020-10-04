package kr.per.james.kakaopay.sprinkling.repository;

import kr.per.james.kakaopay.sprinkling.domain.SprinklingOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SprinklingOrderRepository extends JpaRepository<SprinklingOrder, Long> {

    Optional<SprinklingOrder> findByRoomIdAndToken(String roomId, String token);

    Optional<SprinklingOrder> findByUserIdAndRoomIdAndToken(Integer userId, String roomId, String token);

}
