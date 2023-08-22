package org.mvasylchuk.pfcc.domain.controller;

import lombok.RequiredArgsConstructor;
import org.mvasylchuk.pfcc.domain.dto.FoodDto;
import org.mvasylchuk.pfcc.domain.service.FoodService;
import org.mvasylchuk.pfcc.platform.dto.BaseResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/food")
@RequiredArgsConstructor
public class FoodController {
    private final FoodService foodService;

    @PostMapping("/add")
    public BaseResponse<FoodDto> add(@RequestBody FoodDto request) {
        return BaseResponse.success(foodService.addFood(request));
    }
    @DeleteMapping("/{id}")
    public BaseResponse<Void> remove(@PathVariable Long id){
        foodService.remove(id);
        return BaseResponse.success(null);
    }
}
