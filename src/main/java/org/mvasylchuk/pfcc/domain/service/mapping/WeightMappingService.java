package org.mvasylchuk.pfcc.domain.service.mapping;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.mvasylchuk.pfcc.common.dto.WeightDto;
import org.mvasylchuk.pfcc.common.jpa.Weight;
import org.mvasylchuk.pfcc.measurement.MeasurementDto;
import org.mvasylchuk.pfcc.measurement.MeasurementEntity;
import org.mvasylchuk.pfcc.measurement.MeasurementInternalController;
import org.mvasylchuk.pfcc.platform.error.ApiErrorCode;
import org.mvasylchuk.pfcc.platform.error.PfccException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WeightMappingService {
    private final MeasurementInternalController measurementController;

    @Transactional(rollbackOn = Exception.class)
    public Weight toValueObject(Long foodId, WeightDto ingredientWeight) {

        if (ingredientWeight.getMeasurementId() != null) {
            MeasurementDto measurement = this.measurementController
                    .findById(foodId, ingredientWeight.getMeasurementId())
                    .orElseThrow(() -> new PfccException(
                            "Invalid measurement ID %d".formatted(ingredientWeight.getMeasurementId()),
                            ApiErrorCode.MEASUREMENT_IS_NOT_FOUND
                    ));

            MeasurementEntity measurementEntity = new MeasurementEntity();
            measurementEntity.setId(ingredientWeight.getMeasurementId());

            return new Weight(
                    measurement.getToGramMultiplier().multiply(ingredientWeight.getMeasurementCount()),
                    measurementEntity,
                    measurement.getName(),
                    ingredientWeight.getMeasurementCount()
            );
        } else {
            return new Weight(ingredientWeight.getInGram(), null, null, null);
        }
    }

    public WeightDto toDto(Weight weight) {
        return new WeightDto(
                weight.getMeasurementCount(),
                weight.getMeasurement() != null ? weight.getMeasurement().getId() : null,
                weight.getInGram(),
                weight.getMeasurementName()
        );
    }
}
