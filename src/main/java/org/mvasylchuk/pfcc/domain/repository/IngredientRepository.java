package org.mvasylchuk.pfcc.domain.repository;

import org.mvasylchuk.pfcc.domain.entity.FoodIngredientEntity;
import org.mvasylchuk.pfcc.domain.entity.IngredientPrimaryKey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IngredientRepository extends JpaRepository<FoodIngredientEntity, IngredientPrimaryKey> {
}
