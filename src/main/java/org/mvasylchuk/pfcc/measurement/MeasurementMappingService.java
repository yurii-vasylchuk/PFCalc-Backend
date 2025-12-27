package org.mvasylchuk.pfcc.measurement;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.mvasylchuk.pfcc.domain.entity.FoodEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MeasurementMappingService {
    @Transactional(rollbackOn = Exception.class)
    public MeasurementEntity toEntity(MeasurementDto measurementDto) {
        MeasurementEntity result = new MeasurementEntity();
        result.setId(measurementDto.getId());
        result.setFood(new FoodEntity(measurementDto.getFoodId()));
        result.setName(measurementDto.getName().trim());
        result.setToGramMultiplier(measurementDto.getToGramMultiplier());
        result.setDefaultValue(measurementDto.getDefaultValue());

        return result;
    }

    @Transactional(rollbackOn = Exception.class)
    public MeasurementDto toDto(MeasurementEntity measurementEntity) {
        MeasurementDto result = new MeasurementDto();
        result.setId(measurementEntity.getId());
        result.setFoodId(measurementEntity.getFood().getId());
        result.setToGramMultiplier(measurementEntity.getToGramMultiplier());
        result.setName(measurementEntity.getName());
        result.setDefaultValue(measurementEntity.getDefaultValue());

        return result;
    }
}
