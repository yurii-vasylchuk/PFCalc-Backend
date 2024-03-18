package org.mvasylchuk.pfcc.domain.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.mvasylchuk.pfcc.common.dto.Page;
import org.mvasylchuk.pfcc.domain.dto.DishDto;
import org.mvasylchuk.pfcc.domain.entity.DishEntity;
import org.mvasylchuk.pfcc.domain.repository.DishJooqRepository;
import org.mvasylchuk.pfcc.domain.repository.DishRepository;
import org.mvasylchuk.pfcc.platform.error.ApiErrorCode;
import org.mvasylchuk.pfcc.platform.error.PfccException;
import org.mvasylchuk.pfcc.user.UserService;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class DishService {
    private final DishRepository dishRepository;
    private final UserService userService;
    private final DishJooqRepository jooqRepository;
    private final DishMappingService dishMappingService;

    @Transactional(rollbackOn = Exception.class)
    public DishDto addDish(DishDto request) {

        DishEntity dishEntity = dishMappingService.toEntity(request);

        dishRepository.save(dishEntity);

        return dishMappingService.toDto(dishEntity);
    }


    @Transactional(rollbackOn = Exception.class)
    public void remove(Long id) {
        DishEntity dish = dishRepository.findById(id)
                                        .orElseThrow(() -> new PfccException(ApiErrorCode.DISH_IS_NOT_FOUND));
        dish.setDeleted(true);
        dishRepository.save(dish);
    }

    @Transactional(rollbackOn = Exception.class)
    public DishDto getDishById(Long id) {
        Long userId = userService.currentUser().getId();
        return jooqRepository.getDishById(id, userId);
    }

    @Transactional(rollbackOn = Exception.class)
    public Page<DishDto> getDishList(Integer page, Integer pageSize) {
        Long userId = userService.currentUser().getId();
        return jooqRepository.getDishList(page, pageSize, userId);
    }

    @Transactional(rollbackOn = Exception.class)
    public DishDto update(Long id, DishDto req) {
        DishEntity origin = dishRepository.findById(id)
                                          .orElseThrow(() -> new PfccException(ApiErrorCode.DISH_IS_NOT_FOUND));

        Long userId = userService.currentUser().getId();
        if (!Objects.equals(userId, origin.getOwner().getId())) {
            throw new PfccException(ApiErrorCode.USER_IS_NOT_OWNER);
        }

        DishEntity newDish = dishMappingService.toEntity(req);
        newDish.setId(id);
        dishRepository.save(newDish);
        return dishMappingService.toDto(newDish);
    }
}
