package org.mvasylchuk.pfcc.domain.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.mvasylchuk.pfcc.common.dto.Page;
import org.mvasylchuk.pfcc.domain.dto.CommandMealDto;
import org.mvasylchuk.pfcc.domain.dto.QueryMealDto;
import org.mvasylchuk.pfcc.domain.entity.MealEntity;
import org.mvasylchuk.pfcc.domain.repository.MealJooqRepository;
import org.mvasylchuk.pfcc.domain.repository.MealRepository;
import org.mvasylchuk.pfcc.user.UserService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MealService {
    private final MealRepository mealRepository;
    private final UserService userService;
    private final MealJooqRepository mealJooqRepository;
    private final MealMappingService mealMappingService;

    @Transactional(rollbackOn = Exception.class)
    public CommandMealDto addMeal(CommandMealDto request) {

        MealEntity mealEntity = mealMappingService.toEntity(request);

        mealRepository.save(mealEntity);

        return mealMappingService.toDto(mealEntity);
    }

    @Transactional(rollbackOn = Exception.class)
    public void remove(Long id) {
        mealRepository.delete(mealRepository.getReferenceById(id));
    }

    @Transactional(rollbackOn = Exception.class)
    public QueryMealDto getById(Long id) {
        return mealJooqRepository.getById(id);
    }

    @Transactional(rollbackOn = Exception.class)
    public Page<QueryMealDto> getMealList(Integer page, Integer pageSize, LocalDateTime from, LocalDateTime to) {
        Long userId = userService.currentUser().getId();

        return mealJooqRepository.getMealList(page, pageSize, from, to, userId);
    }
}
