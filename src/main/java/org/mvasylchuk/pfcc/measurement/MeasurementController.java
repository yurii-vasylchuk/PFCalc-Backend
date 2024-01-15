package org.mvasylchuk.pfcc.measurement;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.mvasylchuk.pfcc.common.dto.BaseResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/measurement")
@RequiredArgsConstructor
public class MeasurementController {
    private final MeasurementService measurementService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public BaseResponse<MeasurementDto> save(@RequestBody @Valid MeasurementDto request) {
        return BaseResponse.success(measurementService.saveMeasurement(request));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public BaseResponse<List<MeasurementDto>> get(@RequestParam("foodId") Long foodId) {
        return BaseResponse.success(measurementService.getMeasurementList(foodId));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public BaseResponse<Void> delete(@PathVariable Long id) {
        measurementService.remove(id);
        return BaseResponse.success(null);
    }

}
