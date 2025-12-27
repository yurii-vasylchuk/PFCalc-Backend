package org.mvasylchuk.pfcc.domain.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.mvasylchuk.pfcc.common.dto.WeightDto;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
@JsonSubTypes({
        @JsonSubTypes.Type(value = CommandMealDto.SimpleCommandMealDto.class),
        @JsonSubTypes.Type(value = CommandMealDto.CustomizedCommandMealDto.class)
})

public abstract sealed class CommandMealDto {
    private Long id;
    private LocalDateTime eatenOn;
    private Long foodId;

    @Getter
    @Setter
    public static final class SimpleCommandMealDto extends CommandMealDto {
        private WeightDto weight;
    }

    @Getter
    @Setter
    public static final class CustomizedCommandMealDto extends CommandMealDto {
        private List<MealIngredientDto> ingredients;
    }
}
