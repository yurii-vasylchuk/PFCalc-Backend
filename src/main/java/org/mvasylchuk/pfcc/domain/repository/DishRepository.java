package org.mvasylchuk.pfcc.domain.repository;

import org.mvasylchuk.pfcc.domain.entity.DishEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DishRepository extends JpaRepository<DishEntity,Long> {
}
