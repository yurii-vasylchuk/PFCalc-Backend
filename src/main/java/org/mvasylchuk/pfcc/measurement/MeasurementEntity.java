package org.mvasylchuk.pfcc.measurement;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.mvasylchuk.pfcc.domain.entity.FoodEntity;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "measurement")
public class MeasurementEntity {
    private static final String ID_GENERATOR_NAME = "measurement_id_gen";
    private static final String ID_SEQ_NAME = "measurement_id_seq";
    @Id
    @GeneratedValue(generator = ID_GENERATOR_NAME)
    @SequenceGenerator(name = ID_GENERATOR_NAME, sequenceName = ID_SEQ_NAME, allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;
    @ManyToOne(optional = false)
    @JoinColumn(name = "food_id", nullable = false)
    private FoodEntity food;
    @Column(name = "name")
    private String name;
    @Column(name="to_gram_multiplier")
    private BigDecimal toGramMultiplier;
    @Column(name="default_value")
    private BigDecimal defaultValue;
}
