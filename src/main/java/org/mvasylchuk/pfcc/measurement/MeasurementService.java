package org.mvasylchuk.pfcc.measurement;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.mvasylchuk.pfcc.domain.service.FoodService;
import org.mvasylchuk.pfcc.platform.error.ApiErrorCode;
import org.mvasylchuk.pfcc.platform.error.PfccException;
import org.mvasylchuk.pfcc.user.UserService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MeasurementService {
    private final UserService userService;
    private final MeasurementJooqRepository measurementJooqRepository;
    private final MeasurementRepository measurementRepository;
    private final MeasurementMappingService measurementMappingService;
    private final FoodService foodService;

    @Transactional(rollbackOn = Exception.class)
    //TODO: Invalidate caches
    public MeasurementDto saveMeasurement(MeasurementDto request) {
        MeasurementEntity measurementEntity = measurementMappingService.toEntity(request);

        if (!foodService.getFoodById(request.getFoodId()).getOwnedByUser()) {
            throw new PfccException(ApiErrorCode.USER_IS_NOT_OWNER);
        }
        measurementRepository.save(measurementEntity);

        return measurementMappingService.toDto(measurementEntity);

    }

    //TODO: Invalidate caches
    public void remove(Long id) {
        measurementRepository.deleteById(id);
    }

    public List<MeasurementDto> getMeasurementList(Long foodId) {
        return measurementJooqRepository.getMeasurementList(foodId);
    }
}
