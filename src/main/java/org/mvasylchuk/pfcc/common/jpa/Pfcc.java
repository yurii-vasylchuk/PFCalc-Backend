package org.mvasylchuk.pfcc.common.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.stream.StreamSupport;

@Getter
@Setter
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class Pfcc {
    @Column(name = "protein")
    private BigDecimal protein;
    @Column(name = "fat")
    private BigDecimal fat;
    @Column(name = "carbohydrates")
    private BigDecimal carbohydrates;
    @Column(name = "calories")
    private BigDecimal calories;

    public Pfcc multiply(BigDecimal multiplier) {
        return new Pfcc(this.protein.multiply(multiplier),
                this.fat.multiply(multiplier),
                this.carbohydrates.multiply(multiplier),
                this.calories.multiply(multiplier));
    }

    public Pfcc divide(BigDecimal divisor) {

        return new Pfcc(this.protein.divide(divisor, 4, RoundingMode.HALF_UP),
                this.fat.divide(divisor, 4, RoundingMode.HALF_UP),
                this.carbohydrates.divide(divisor, 4, RoundingMode.HALF_UP),
                this.calories.divide(divisor, 4, RoundingMode.HALF_UP));
    }

    public static Pfcc combine(Iterable<Pfcc> inputs) {
        return StreamSupport.stream(inputs.spliterator(), true)
                .reduce(new Pfcc(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO),
                        (pfcc, pfcc2) -> new Pfcc(
                                pfcc.protein.add(pfcc2.protein),
                                pfcc.fat.add(pfcc2.fat),
                                pfcc.carbohydrates.add(pfcc2.carbohydrates),
                                pfcc.calories.add(pfcc2.calories)
                        ));

    }
}
