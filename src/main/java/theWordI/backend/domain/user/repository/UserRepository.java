package theWordI.backend.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import theWordI.backend.domain.user.entity.UserEntity;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Boolean existsByUsername(String username);

    Optional<UserEntity> findByUserId(Long userId);

    Optional<UserEntity> findByUsernameAndIsLockAndIsSocial(String username,
                                                            Boolean isLock,
                                                            Boolean isSocial);

    Optional<UserEntity> findByUsernameAndIsSocial(String username, Boolean social);


    @Transactional
    void deleteByUserId(Long userId);
}
