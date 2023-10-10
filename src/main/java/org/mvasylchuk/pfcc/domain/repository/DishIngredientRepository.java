package org.mvasylchuk.pfcc.domain.repository;

import org.mvasylchuk.pfcc.domain.entity.DishIngredientEntity;
import org.mvasylchuk.pfcc.domain.entity.DishIngredientPrimaryKey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DishIngredientRepository extends JpaRepository<DishIngredientEntity, DishIngredientPrimaryKey> {
}
