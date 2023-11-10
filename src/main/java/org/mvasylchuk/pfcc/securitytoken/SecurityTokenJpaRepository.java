package org.mvasylchuk.pfcc.securitytoken;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface SecurityTokenJpaRepository extends JpaRepository<SecurityTokenEntity, Long> {
    @Query("FROM SecurityTokenEntity t where t.code = :code AND t.type = :type AND t.isActive AND (t.validUntil IS NULL OR t.validUntil >= current_timestamp)")
    Optional<SecurityTokenEntity> findValid(String code, SecurityTokenType type);
}
