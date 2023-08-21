package org.mvasylchuk.pfcc.user;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.mvasylchuk.pfcc.common.dto.PfccDto;
import org.mvasylchuk.pfcc.domain.dto.DishDto;
import org.mvasylchuk.pfcc.domain.dto.MealDto;
import org.mvasylchuk.pfcc.user.dto.ProfileDto;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

import static org.mvasylchuk.pfcc.jooq.tables.Dish.DISH;
import static org.mvasylchuk.pfcc.jooq.tables.Meal.MEAL;
import static org.mvasylchuk.pfcc.jooq.tables.Users.USERS;

@Component
@RequiredArgsConstructor
public class UserJooqRepository {
    private final DSLContext ctx;

    public ProfileDto getProfileByUserEmail(String email) {


        ProfileDto profileDto = ctx.select(USERS.EMAIL, USERS.PROFILE_CONFIGURED, USERS.CALORIES_AIM, USERS.CARBOHYDRATES_AIM, USERS.FAT_AIM, USERS.PROTEIN_AIM, USERS.PREFERRED_LANGUAGE).from(USERS).where(USERS.EMAIL.eq(email)).fetchOne((dbuser) -> {
            ProfileDto result = new ProfileDto();
            result.setProfileConfigured(dbuser.get(USERS.PROFILE_CONFIGURED, Boolean.class));
            result.setAims(new PfccDto(dbuser.get(USERS.PROTEIN_AIM), dbuser.get(USERS.FAT_AIM), dbuser.get(USERS.CARBOHYDRATES_AIM), dbuser.get(USERS.CALORIES_AIM)));
            result.setPreferredLanguage(Language.valueOf(dbuser.get(USERS.PREFERRED_LANGUAGE)));
            result.setEmail(dbuser.get(USERS.EMAIL));
            return result;
        });


        List<DishDto> dishesList = ctx.select(DISH.asterisk()).from(DISH).join(USERS).on(DISH.OWNER_ID.eq(USERS.ID)).where(USERS.EMAIL.eq(email).and(DISH.DELETED.isFalse())).fetch((dbdish) -> {
            DishDto result = new DishDto();
            result.setId(dbdish.get(DISH.ID));
            result.setCookedOn(dbdish.get(DISH.COOKED_ON));
            result.setPfcc(new PfccDto(dbdish.get(DISH.PROTEIN), dbdish.get(DISH.FAT), dbdish.get(DISH.CARBOHYDRATES), dbdish.get(DISH.CALORIES)));
            result.setName(dbdish.get(DISH.NAME));
            result.setFoodId(dbdish.get(DISH.FOOD_ID));
            result.setRecipeWeight(dbdish.get(DISH.RECIPE_WEIGHT));
            result.setCookedWeight(dbdish.get(DISH.COOKED_WEIGHT));
            result.setDeleted(dbdish.get(DISH.DELETED, Boolean.class));
            return result;
        });

        profileDto.setDishes(dishesList);

        LocalDateTime startOfTheWeek = LocalDateTime.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).withHour(0).withMinute(0).withSecond(0).withNano(0);

        List<MealDto> mealList = ctx.select(MEAL.asterisk()).from(MEAL).join(USERS).on(MEAL.OWNER_ID.eq(USERS.ID))
                .where(USERS.EMAIL.eq(email)
                        .and(MEAL.EATEN_ON.greaterOrEqual(startOfTheWeek))
                ).fetch(m -> {
            MealDto result = new MealDto();
            result.setId(m.get(MEAL.ID));
            result.setEatenOn(m.get(MEAL.EATEN_ON));
            result.setWeight(m.get(MEAL.WEIGHT));
            result.setPfcc(new PfccDto(m.get(MEAL.PROTEIN), m.get(MEAL.FAT), m.get(MEAL.CARBOHYDRATES), m.get(MEAL.CALORIES)));
            result.setFoodId(m.get(MEAL.FOOD_ID));
            result.setDishId(m.get(MEAL.DISH_ID));
            return result;
        });

        profileDto.setMeals(mealList);

        return profileDto;
    }
}
