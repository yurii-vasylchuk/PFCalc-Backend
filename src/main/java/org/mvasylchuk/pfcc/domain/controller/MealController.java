package org.mvasylchuk.pfcc.domain.controller;

import lombok.RequiredArgsConstructor;
import org.mvasylchuk.pfcc.common.dto.BaseResponse;
import org.mvasylchuk.pfcc.common.dto.Page;
import org.mvasylchuk.pfcc.domain.dto.MealDto;
import org.mvasylchuk.pfcc.domain.service.MealService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/meal")
@RequiredArgsConstructor

public class MealController {
    private final MealService mealService;

    @PostMapping("/")
    @PreAuthorize("isAuthenticated()")
    public BaseResponse<MealDto> add(@RequestBody MealDto request) {
        return BaseResponse.success(mealService.addMeal(request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public BaseResponse<Void> remove(@PathVariable Long id) {
        mealService.remove(id);
        return BaseResponse.success(null);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public BaseResponse<MealDto> getById(@PathVariable Long id) {
        return BaseResponse.success(mealService.getById(id));
    }

    @GetMapping("/")
    @PreAuthorize("isAuthenticated()")
    public BaseResponse <Page<MealDto>> get(@RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
                             @RequestParam(name = "pageSize", required = false) Integer pageSize,
                             @RequestParam(name = "from", required = false) LocalDateTime from,
                             @RequestParam(name = "to", required = false) LocalDateTime to) {

        return BaseResponse.success(mealService.getMealList(page, pageSize, from, to));
    }
}
