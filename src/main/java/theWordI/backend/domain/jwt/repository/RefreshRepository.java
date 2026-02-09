package theWordI.backend.domain.jwt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import theWordI.backend.domain.jwt.entity.RefreshEntity;

import java.time.LocalDateTime;
import java.util.Optional;

public interface RefreshRepository extends JpaRepository<RefreshEntity, Long> {

    Boolean existsByRefreshToken(String refreshToken);

    Optional<RefreshEntity> findByRefreshToken(String refreshToken);

    @Transactional
    void deleteByRefreshToken(String refreshToken);

    @Transactional
    void deleteByUserId(Long userId);

    // 특정일 지난 refresh 토큰 삭제
    @Transactional
    void deleteByCreatedDtBefore(LocalDateTime createdDt);

}
