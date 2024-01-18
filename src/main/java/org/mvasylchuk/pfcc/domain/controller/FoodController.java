package org.mvasylchuk.pfcc.domain.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.mvasylchuk.pfcc.common.dto.BaseResponse;
import org.mvasylchuk.pfcc.common.dto.Page;
import org.mvasylchuk.pfcc.domain.dto.FoodDto;
import org.mvasylchuk.pfcc.domain.entity.FoodType;
import org.mvasylchuk.pfcc.domain.service.FoodService;
import org.mvasylchuk.pfcc.domain.service.FoodSyncService;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/food")
@RequiredArgsConstructor
public class FoodController {
    private final FoodService foodService;
    private final FoodSyncService foodSyncService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("isAuthenticated()")
    public BaseResponse<FoodDto> save(@RequestBody @Valid FoodDto request) {
        return BaseResponse.success(foodService.saveFood(request));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public BaseResponse<Void> sync(@RequestParam("file") MultipartFile file) throws IOException {
        foodSyncService.sync(file.getBytes());
        return BaseResponse.success(null);
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public BaseResponse<Void> remove(@PathVariable Long id) {
        foodService.remove(id);
        return BaseResponse.success(null);
    }

    @GetMapping()
    @PreAuthorize("isAuthenticated()")
    public BaseResponse<Page<FoodDto>> get(@RequestParam(name = "page",
                                                         required = false,
                                                         defaultValue = "0") Integer page,
                                           @RequestParam(name = "pageSize",
                                                         required = false,
                                                         defaultValue = "10") Integer pageSize,
                                           @RequestParam(name = "name", required = false) String name,
                                           @RequestParam(name = "type", required = false) FoodType type) {
        return BaseResponse.success(foodService.getFoodList(page, pageSize, name, type));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public BaseResponse<FoodDto> getById(@PathVariable Long id) {
        return BaseResponse.success(foodService.getFoodById(id));

    }
}
