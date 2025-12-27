package org.mvasylchuk.pfcc.domain.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.mvasylchuk.pfcc.common.dto.BaseResponse;
import org.mvasylchuk.pfcc.common.dto.Page;
import org.mvasylchuk.pfcc.domain.dto.CommandMealDto;
import org.mvasylchuk.pfcc.domain.dto.MealDto;
import org.mvasylchuk.pfcc.domain.dto.MealOptionDto;
import org.mvasylchuk.pfcc.domain.service.MealService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/meal")
@RequiredArgsConstructor

public class MealController {
    private final MealService mealService;

    @PostMapping()
    @PreAuthorize("isAuthenticated()")
    public BaseResponse<MealDto> save(@RequestBody @Valid CommandMealDto request) {
        return BaseResponse.success(mealService.save(request));
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

    @GetMapping()
    @PreAuthorize("isAuthenticated()")
    public BaseResponse<Page<MealDto>> get(@RequestParam(name = "page",
                                                   required = false,
                                                   defaultValue = "0") Integer page,
                                           @RequestParam(name = "pageSize", required = false) Integer pageSize,
                                           @RequestParam(name = "from", required = false) LocalDateTime from,
                                           @RequestParam(name = "to", required = false) LocalDateTime to) {

        return BaseResponse.success(mealService.getMealList(page, pageSize, from, to));
    }

    @GetMapping("/options")
    @PreAuthorize("isAuthenticated()")
    public BaseResponse<Page<MealOptionDto>> getOptions(@RequestParam(name = "filter", required = false) String filter,
                                                        @RequestParam(name = "page",
                                                                required = false,
                                                                defaultValue = "0") Integer page,
                                                        @RequestParam(name = "pageSize",
                                                                required = false,
                                                                defaultValue = "10") Integer pageSize) {
        return BaseResponse.success(this.mealService.getMealOptions(filter, page, pageSize));
    }
}
