package org.mvasylchuk.pfcc.domain.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.mvasylchuk.pfcc.common.dto.Page;
import org.mvasylchuk.pfcc.domain.dto.FoodDto;
import org.mvasylchuk.pfcc.domain.entity.FoodEntity;
import org.mvasylchuk.pfcc.domain.repository.FoodJooqRepository;
import org.mvasylchuk.pfcc.domain.repository.FoodRepository;
import org.mvasylchuk.pfcc.user.UserService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FoodService {
    private final UserService userService;
    private final FoodRepository foodRepository;
    private final FoodJooqRepository foodJooqRepository;
    private final FoodMappingService foodMappingService;


    @Transactional(rollbackOn = Exception.class)
    public FoodDto addFood(FoodDto request) {
       FoodEntity foodEntity= foodMappingService.toEntity(request);

        foodRepository.save(foodEntity);

        return foodMappingService.toDto(foodEntity);
    }

    @Transactional(rollbackOn = Exception.class)
    public void remove(Long id) {
        FoodEntity food = foodRepository.findById(id).orElseThrow();
        food.setIsDeleted(true);
        foodRepository.save(food);
    }

    @Transactional(rollbackOn = Exception.class)
    public Page<FoodDto> getFoodList(Integer page, Integer size) {
        Long userId = userService.currentUser().getId();
        return foodJooqRepository.getFoodList(page, size, userId);
    }

    @Transactional(rollbackOn = Exception.class)
    public FoodDto getFoodById(Long id) {
        Long userId = userService.currentUser().getId();
        return foodJooqRepository.getFoodById(id, userId);
    }
}
