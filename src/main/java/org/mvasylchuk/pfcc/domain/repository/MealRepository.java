package org.mvasylchuk.pfcc.domain.repository;

import org.mvasylchuk.pfcc.domain.entity.MealEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MealRepository extends JpaRepository<MealEntity, Long> {
}
