package org.mvasylchuk.pfcc.domain.controller;

import lombok.RequiredArgsConstructor;
import org.mvasylchuk.pfcc.common.dto.BaseResponse;
import org.mvasylchuk.pfcc.common.dto.Page;
import org.mvasylchuk.pfcc.domain.dto.DishDto;
import org.mvasylchuk.pfcc.domain.service.DishService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dish")
@RequiredArgsConstructor
public class DishController {
    private final DishService dishService;


    @PostMapping()
    @PreAuthorize("isAuthenticated()")
    public BaseResponse<DishDto> add(@RequestBody DishDto request) {
        return BaseResponse.success(dishService.addDish(request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public BaseResponse<Void> remove(@PathVariable Long id) {
        dishService.remove(id);
        return BaseResponse.success(null);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public BaseResponse<DishDto> getById(@PathVariable Long id) {
        return BaseResponse.success(dishService.getDishById(id));
    }

    @GetMapping()
    @PreAuthorize("isAuthenticated()")
    public Page<DishDto> get(@RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
                             @RequestParam(name = "pageSize", required = false, defaultValue = "10") Integer pageSize) {
        return dishService.getDishList(page, pageSize);
    }

}
