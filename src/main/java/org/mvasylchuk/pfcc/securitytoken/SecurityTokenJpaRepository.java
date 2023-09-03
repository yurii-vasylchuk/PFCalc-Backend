package org.mvasylchuk.pfcc.securitytoken;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SecurityTokenJpaRepository extends JpaRepository<SecurityTokenEntity, Long> {
    Optional<SecurityTokenEntity> findByCodeAndTypeAndIsActiveIsTrue(String code, SecurityTokenType type);
}
