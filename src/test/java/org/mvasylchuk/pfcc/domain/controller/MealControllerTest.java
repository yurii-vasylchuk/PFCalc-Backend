package org.mvasylchuk.pfcc.domain.controller;

import org.flywaydb.test.annotation.FlywayTest;
import org.junit.jupiter.api.Test;
import org.mvasylchuk.pfcc.IntegrationTest;
import org.mvasylchuk.pfcc.common.dto.BaseResponse;
import org.mvasylchuk.pfcc.common.dto.Page;
import org.mvasylchuk.pfcc.domain.dto.MealOptionDto;
import org.mvasylchuk.pfcc.util.WithTestUser;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mvasylchuk.pfcc.domain.dto.MealOptionType.*;

@IntegrationTest
class MealControllerTest {

    @Autowired
    private MealController mealController;

    @Test
    @FlywayTest(locationsForMigrate = "migration/MealControllerTest")
    @WithTestUser(id = 1, email = "test1@mail.com", roles = "USER")
    void getOptions_whenPageSizeIsSmall() {
        BaseResponse<Page<MealOptionDto>> options = mealController.getOptions(null, 0, 2);

        Page<MealOptionDto> page = options.getData();

        assertEquals(0, page.getPage());
        assertEquals(2, page.getPageSize());
        assertEquals(4, page.getTotalPages());
        assertEquals(7, page.getTotalElements());
    }

    @Test
    @FlywayTest(locationsForMigrate = "migration/MealControllerTest")
    @WithTestUser(id = 1, email = "test1@mail.com", roles = "USER")
    void getOptions() {
        BaseResponse<Page<MealOptionDto>> rsp = mealController.getOptions(null, 0, 10);

        assertNotNull(rsp);
        assertTrue(rsp.isSuccess());

        Page<MealOptionDto> page = rsp.getData();

        assertEquals(0, page.getPage());
        assertEquals(10, page.getPageSize());
        assertEquals(1, page.getTotalPages());
        assertEquals(7, page.getTotalElements());

        assertThat(page.getData())
                .hasSize(7)
                .anyMatch(o -> Objects.equals(o.getFoodId(), 1L) && INGREDIENT.equals(o.getType()))
                .anyMatch(o -> Objects.equals(o.getFoodId(), 2L) && INGREDIENT.equals(o.getType()))
                .anyMatch(o -> Objects.equals(o.getFoodId(), 3L) && RECIPE.equals(o.getType()))
                .anyMatch(o -> Objects.equals(o.getFoodId(), 5L) && INGREDIENT.equals(o.getType()))
                .anyMatch(o -> Objects.equals(o.getFoodId(), 8L) && RECIPE.equals(o.getType()))
                .anyMatch(o -> Objects.equals(o.getFoodId(), 3L) &&
                        Objects.equals(o.getDishId(), 1L) &&
                        DISH.equals(o.getType()))
                .anyMatch(o -> Objects.equals(o.getFoodId(), 8L) &&
                        Objects.equals(o.getDishId(), 2L) &&
                        DISH.equals(o.getType()));
    }
}
