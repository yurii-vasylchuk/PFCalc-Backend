package org.mvasylchuk.pfcc.measurement;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MeasurementInternalController {
    private final MeasurementJooqRepository repository;
    private final MeasurementMappingService mappingService;

    public Optional<MeasurementDto> findById(Long foodId, Long measurementId) {
        return this.repository.findById(foodId, measurementId);
    }

    public List<MeasurementDto> byFoodId(Long foodId) {
        return this.repository.getMeasurementList(foodId);
    }
}
