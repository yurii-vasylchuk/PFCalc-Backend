package org.mvasylchuk.pfcc.domain.repository;

import org.mvasylchuk.pfcc.domain.entity.IngredientEntity;
import org.mvasylchuk.pfcc.domain.entity.IngredientPrimaryKey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IngredientRepository extends JpaRepository<IngredientEntity, IngredientPrimaryKey> {
}
