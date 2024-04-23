package org.mvasylchuk.pfcc.common.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PfccDto {
    @NotNull
    @PositiveOrZero
    private BigDecimal protein;
    @NotNull
    @PositiveOrZero
    private BigDecimal fat;
    @NotNull
    @PositiveOrZero
    private BigDecimal carbohydrates;
    @NotNull
    @PositiveOrZero
    private BigDecimal calories;

    public static PfccDto zero() {
        return new PfccDto(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
    }

    public PfccDto add(PfccDto augend) {
        return new PfccDto(
                this.protein == null ? null : this.protein.add(augend.protein == null ? BigDecimal.ZERO : augend.protein),
                this.fat == null ? null : this.fat.add(augend.fat == null ? BigDecimal.ZERO : augend.fat),
                this.carbohydrates == null ? null : this.carbohydrates.add(augend.carbohydrates == null ? BigDecimal.ZERO : augend.carbohydrates),
                this.calories == null ? null : this.calories.add(augend.calories == null ? BigDecimal.ZERO : augend.calories)
        );
    }

    public PfccDto divide(long divisor) {
        return new PfccDto(
                this.protein == null ? null : this.protein.divide(BigDecimal.valueOf(divisor), this.protein.scale(), RoundingMode.HALF_UP),
                this.fat == null ? null : this.fat.divide(BigDecimal.valueOf(divisor), this.fat.scale(), RoundingMode.HALF_UP),
                this.carbohydrates == null ? null : this.carbohydrates.divide(BigDecimal.valueOf(divisor), this.carbohydrates.scale(), RoundingMode.HALF_UP),
                this.calories == null ? null : this.calories.divide(BigDecimal.valueOf(divisor), this.calories.scale(), RoundingMode.HALF_UP)
        );
    }

    public PfccDto divide(PfccDto divisor) {
        return new PfccDto(
                this.protein == null || divisor.protein == null ? null : this.protein.divide(divisor.protein, this.protein.scale(), RoundingMode.HALF_UP),
                this.fat == null || divisor.fat == null ? null : this.fat.divide(divisor.fat, this.fat.scale(), RoundingMode.HALF_UP),
                this.carbohydrates == null || divisor.carbohydrates == null ? null : this.carbohydrates.divide(divisor.carbohydrates, this.carbohydrates.scale(), RoundingMode.HALF_UP),
                this.calories == null || divisor.calories == null ? null : this.calories.divide(divisor.calories, this.calories.scale(), RoundingMode.HALF_UP)
        );
    }

    public PfccDto multiply(int multiplicand) {
        return new PfccDto(
                this.protein == null ? null : this.protein.multiply(BigDecimal.valueOf(multiplicand)),
                this.fat == null ? null : this.fat.multiply(BigDecimal.valueOf(multiplicand)),
                this.carbohydrates == null ? null : this.carbohydrates.multiply(BigDecimal.valueOf(multiplicand)),
                this.calories == null ? null : this.calories.multiply(BigDecimal.valueOf(multiplicand))
        );
    }

    public PfccDto scale(int scale) {
        return scale(scale, RoundingMode.HALF_UP);
    }

    public PfccDto scale(int scale, RoundingMode roundingMode) {
        return new PfccDto(
                this.protein == null ? null : this.protein.setScale(scale, roundingMode),
                this.fat == null ? null : this.fat.setScale(scale, roundingMode),
                this.carbohydrates == null ? null : this.carbohydrates.setScale(scale, roundingMode),
                this.calories == null ? null : this.calories.setScale(scale, roundingMode)
        );
    }
}
