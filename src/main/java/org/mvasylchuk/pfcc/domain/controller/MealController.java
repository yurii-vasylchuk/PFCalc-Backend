package org.mvasylchuk.pfcc.domain.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.mvasylchuk.pfcc.common.dto.BaseResponse;
import org.mvasylchuk.pfcc.common.dto.Page;
import org.mvasylchuk.pfcc.domain.dto.CommandMealDto;
import org.mvasylchuk.pfcc.domain.dto.MealOptionDto;
import org.mvasylchuk.pfcc.domain.dto.QueryMealDto;
import org.mvasylchuk.pfcc.domain.service.MealService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/meal")
@RequiredArgsConstructor

public class MealController {
    private final MealService mealService;

    @PostMapping()
    @PreAuthorize("isAuthenticated()")
    public BaseResponse<CommandMealDto> save(@RequestBody @Valid CommandMealDto request) {
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
    public BaseResponse<QueryMealDto> getById(@PathVariable Long id) {
        return BaseResponse.success(mealService.getById(id));
    }

    @GetMapping()
    @PreAuthorize("isAuthenticated()")
    public BaseResponse<Page<QueryMealDto>> get(@RequestParam(name = "page",
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
