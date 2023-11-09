package org.mvasylchuk.pfcc.user;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.mvasylchuk.pfcc.common.dto.PfccDto;
import org.mvasylchuk.pfcc.domain.dto.DishDto;
import org.mvasylchuk.pfcc.domain.dto.CommandMealDto;
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


        ProfileDto profileDto = ctx.select(USERS.EMAIL,
                                           USERS.NAME,
                                           USERS.PROFILE_CONFIGURED,
                                           USERS.CALORIES_AIM,
                                           USERS.CARBOHYDRATES_AIM,
                                           USERS.FAT_AIM,
                                           USERS.PROTEIN_AIM,
                                           USERS.PREFERRED_LANGUAGE)
                                   .from(USERS)
                                   .where(USERS.EMAIL.eq(email))
                                   .fetchOne((dbUser) -> {
                                       ProfileDto result = new ProfileDto();
                                       result.setProfileConfigured(dbUser.get(USERS.PROFILE_CONFIGURED, Boolean.class));
                                       result.setAims(new PfccDto(dbUser.get(USERS.PROTEIN_AIM), dbUser.get(USERS.FAT_AIM), dbUser.get(USERS.CARBOHYDRATES_AIM), dbUser.get(USERS.CALORIES_AIM)));
                                       result.setPreferredLanguage(Language.valueOf(dbUser.get(USERS.PREFERRED_LANGUAGE)));
                                       result.setEmail(dbUser.get(USERS.EMAIL));
                                       result.setName(dbUser.get(USERS.NAME));
                                       return result;
                                   });
        if (profileDto == null) {
            return null;
        }

        List<DishDto> dishesList = ctx.select(DISH.asterisk())
                                      .from(DISH)
                                      .join(USERS).on(DISH.OWNER_ID.eq(USERS.ID))
                                      .where(USERS.EMAIL.eq(email)
                                                        .and(DISH.DELETED.isFalse()))
                                      .fetch((dbdish) -> {
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

        LocalDateTime startOfTheWeek = LocalDateTime.now()
                                                    .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                                                    .withHour(0)
                                                    .withMinute(0)
                                                    .withSecond(0)
                                                    .withNano(0);

        List<CommandMealDto> mealList = ctx.select(MEAL.asterisk()).from(MEAL).join(USERS).on(MEAL.OWNER_ID.eq(USERS.ID))
                                    .where(USERS.EMAIL.eq(email)
                                                      .and(MEAL.EATEN_ON.greaterOrEqual(startOfTheWeek))
                                    ).fetch(m -> {
                    CommandMealDto result = new CommandMealDto();
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
