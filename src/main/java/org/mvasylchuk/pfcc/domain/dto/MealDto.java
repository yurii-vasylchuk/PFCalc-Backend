package org.mvasylchuk.pfcc.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.mvasylchuk.pfcc.common.dto.PfccDto;
import org.mvasylchuk.pfcc.domain.entity.MealEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MealDto {
    private Long id;
    private LocalDateTime eatenOn;
    private BigDecimal weight;
    private PfccDto pfcc;
    private Long foodId;
    private Long dishId;

    public static MealDto fromMealEntity(MealEntity mealEntity)
    {
        return new MealDto(mealEntity.getId(),
                mealEntity.getEatenOn(),
                mealEntity.getWeight(),
                PfccDto.fromPfcc(mealEntity.getPfcc()),
                mealEntity.getFood().getId(),
                mealEntity.getDish().getId());
    }
}
