package org.mvasylchuk.pfcc.domain.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.mvasylchuk.pfcc.common.dto.Page;
import org.mvasylchuk.pfcc.domain.dto.FoodDto;
import org.mvasylchuk.pfcc.domain.entity.FoodEntity;
import org.mvasylchuk.pfcc.domain.entity.FoodType;
import org.mvasylchuk.pfcc.domain.repository.FoodJooqRepository;
import org.mvasylchuk.pfcc.domain.repository.FoodRepository;
import org.mvasylchuk.pfcc.platform.error.ApiErrorCode;
import org.mvasylchuk.pfcc.platform.error.PfccException;
import org.mvasylchuk.pfcc.user.UserService;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class FoodService {
    private final UserService userService;
    private final FoodRepository foodRepository;
    private final FoodJooqRepository foodJooqRepository;
    private final FoodMappingService foodMappingService;


    @Transactional(rollbackOn = Exception.class)
    public FoodDto saveFood(FoodDto request) {
        Long currentUserId = userService.currentUser().getId();
        FoodEntity foodEntity = foodMappingService.toEntity(request);
        if (!Objects.equals(foodEntity.getOwner().getId(), currentUserId)) {
            throw new PfccException(ApiErrorCode.USER_IS_NOT_OWNER);
        }
        foodRepository.save(foodEntity);

        return foodMappingService.toDto(foodEntity);
    }

    @Transactional(rollbackOn = Exception.class)
    public void remove(Long id) {
        FoodEntity food = foodRepository.findById(id).orElseThrow(() -> new PfccException(ApiErrorCode.FOOD_IS_NOT_FOUND));

        Long foodOwner = food.getOwner().getId();
        Long currentUserId = userService.currentUser().getId();
        if (!Objects.equals(foodOwner, currentUserId)) {
            throw new PfccException(ApiErrorCode.USER_IS_NOT_OWNER);
        }

        food.setIsDeleted(true);
        foodRepository.save(food);
    }

    @Transactional(rollbackOn = Exception.class)
    public Page<FoodDto> getFoodList(Integer page, Integer size, String name, FoodType type) {
        Long userId = userService.currentUser().getId();
        return foodJooqRepository.getFoodList(page, size, name, type, userId);
    }

    @Transactional(rollbackOn = Exception.class)
    public FoodDto getFoodById(Long id) {
        Long userId = userService.currentUser().getId();
        return foodJooqRepository.getFoodById(id, userId);
    }
}
