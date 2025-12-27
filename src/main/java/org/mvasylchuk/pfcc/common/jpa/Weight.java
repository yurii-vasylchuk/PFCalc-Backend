package org.mvasylchuk.pfcc.common.jpa;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.mvasylchuk.pfcc.measurement.MeasurementEntity;

import java.math.BigDecimal;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Weight {
    @Column(name = "weight_in_gram", nullable = false)
    private BigDecimal inGram;

    @JoinColumn(name = "measurement_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private MeasurementEntity measurement;

    @Column(name = "measurement_name")
    private String measurementName;

    @Column(name = "measurement_weight")
    private BigDecimal measurementCount;

    public Weight multiply(BigDecimal coef) {
        return new Weight(
                this.inGram == null ? null : this.inGram.multiply(coef),
                this.measurement,
                this.measurementName,
                this.measurementCount == null ? null : this.measurementCount.multiply(coef)
        );
    }
}
