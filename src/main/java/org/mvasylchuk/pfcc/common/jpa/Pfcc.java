package org.mvasylchuk.pfcc.common.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Embeddable
public class Pfcc {
    @Column(name = "protein")
    private BigDecimal protein;
    @Column(name = "fat")
    private BigDecimal fat;
    @Column(name = "carbohydrates")
    private BigDecimal carbohydrates;
    @Column(name = "calories")
    private BigDecimal calories;

}
