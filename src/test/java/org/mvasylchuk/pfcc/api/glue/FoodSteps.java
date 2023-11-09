package org.mvasylchuk.pfcc.api.glue;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.mvasylchuk.pfcc.api.ApiTestContext;
import org.mvasylchuk.pfcc.api.constants.Constants.TestUser;
import org.mvasylchuk.pfcc.domain.entity.FoodType;
import org.mvasylchuk.pfcc.jooq.tables.records.FoodRecord;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mvasylchuk.pfcc.api.constants.Constants.Db.dbBool;
import static org.mvasylchuk.pfcc.jooq.Tables.FOOD;
import static org.mvasylchuk.pfcc.jooq.Tables.USERS;

@RequiredArgsConstructor
public class FoodSteps {
    private final DSLContext db;
    private final ApiTestContext ctx;
    private final ObjectMapper mapper;

    @Then("the food is saved in db and contain next fields:")
    public void theFoodIsSavedInDbAndContainNextFields(Map<String, String> fields) throws Exception {
        long foodId = getFoodIdFromResponse();

        FoodRecord food = db.selectFrom(FOOD)
                            .where(FOOD.ID.eq(foodId))
                            .fetchOne();

        assertNotNull(food);

        fields.forEach((field, valueStr) -> {
            switch (field) {
                case "name":
                    assertEquals(valueStr, food.getName());
                    break;
                case "description":
                    assertEquals(valueStr, food.getDescription());
                    break;
                case "pfcc_protein":
                    assertThat(food.getProtein()).isEqualByComparingTo(valueStr);
                    break;
                case "pfcc_fat":
                    assertThat(food.getFat()).isEqualByComparingTo(valueStr);
                    break;
                case "pfcc_carbohydrates":
                    assertThat(food.getCarbohydrates()).isEqualByComparingTo(valueStr);
                    break;
                case "pfcc_calories":
                    assertThat(food.getCalories()).isEqualByComparingTo(valueStr);
                    break;
                case "is_hidden":
                    assertEquals(dbBool(valueStr), food.getIsHidden());
                    break;
                case "is_deleted":
                    assertEquals(dbBool(valueStr), food.getDeleted());
                    break;
                case "type":
                    assertDoesNotThrow(() -> FoodType.valueOf(valueStr));
                    assertEquals(valueStr, food.getType());
                    break;
                case "id":
                    Long id = assertDoesNotThrow(() -> Long.valueOf(valueStr));
                    assertEquals(id, food.getId());
                    break;
                case "owner_id":
                    Long ownerId = assertDoesNotThrow(() -> Long.valueOf(valueStr));
                    assertEquals(ownerId, food.getOwnerId());
                    break;
                default:
                    throw new IllegalArgumentException("Unknown field %s".formatted(field));
            }
        });
    }

    @And("the food is owned by user '{}'")
    public void theFoodIsOwnedByUserAlpha(TestUser user) throws Exception {
        long foodId = getFoodIdFromResponse();

        Long foodOwnerId = db.select(FOOD.OWNER_ID)
                             .from(FOOD)
                             .where(FOOD.ID.eq(foodId))
                             .fetchOneInto(Long.class);

        Long userId = db.select(USERS.ID)
                        .from(USERS)
                        .where(USERS.EMAIL.eq(user.getEmail()))
                        .fetchOneInto(Long.class);


        assertEquals(userId, foodOwnerId);
    }

    private long getFoodIdFromResponse() throws Exception {
        return mapper.readValue(
                ctx.getPerformedCalls().andReturn().getResponse().getContentAsString(),
                JsonNode.class
        ).at("/data/id").asLong();
    }
}
