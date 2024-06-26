/*
 * This file is generated by jOOQ.
 */
package org.mvasylchuk.pfcc.jooq;


import org.jooq.Index;
import org.jooq.OrderField;
import org.jooq.impl.DSL;
import org.jooq.impl.Internal;
import org.mvasylchuk.pfcc.jooq.tables.Dish;
import org.mvasylchuk.pfcc.jooq.tables.Food;
import org.mvasylchuk.pfcc.jooq.tables.Ingredients;
import org.mvasylchuk.pfcc.jooq.tables.Meal;
import org.mvasylchuk.pfcc.jooq.tables.Measurement;
import org.mvasylchuk.pfcc.jooq.tables.Reports;
import org.mvasylchuk.pfcc.jooq.tables.SecurityTokens;


/**
 * A class modelling indexes of tables in pfcc.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Indexes {

    // -------------------------------------------------------------------------
    // INDEX definitions
    // -------------------------------------------------------------------------

    public static final Index MEAL_DISH_ID = Internal.createIndex(DSL.name("dish_id"), Meal.MEAL, new OrderField[] { Meal.MEAL.DISH_ID }, false);
    public static final Index DISH_FOOD_ID = Internal.createIndex(DSL.name("food_id"), Dish.DISH, new OrderField[] { Dish.DISH.FOOD_ID }, false);
    public static final Index MEAL_FOOD_ID = Internal.createIndex(DSL.name("food_id"), Meal.MEAL, new OrderField[] { Meal.MEAL.FOOD_ID }, false);
    public static final Index MEASUREMENT_FOOD_ID = Internal.createIndex(DSL.name("food_id"), Measurement.MEASUREMENT, new OrderField[] { Measurement.MEASUREMENT.FOOD_ID }, false);
    public static final Index INGREDIENTS_INGREDIENT_ID = Internal.createIndex(DSL.name("ingredient_id"), Ingredients.INGREDIENTS, new OrderField[] { Ingredients.INGREDIENTS.INGREDIENT_ID }, false);
    public static final Index DISH_OWNER_ID = Internal.createIndex(DSL.name("owner_id"), Dish.DISH, new OrderField[] { Dish.DISH.OWNER_ID }, false);
    public static final Index FOOD_OWNER_ID = Internal.createIndex(DSL.name("owner_id"), Food.FOOD, new OrderField[] { Food.FOOD.OWNER_ID }, false);
    public static final Index MEAL_OWNER_ID = Internal.createIndex(DSL.name("owner_id"), Meal.MEAL, new OrderField[] { Meal.MEAL.OWNER_ID }, false);
    public static final Index REPORTS_USER_ID = Internal.createIndex(DSL.name("user_id"), Reports.REPORTS, new OrderField[] { Reports.REPORTS.USER_ID }, false);
    public static final Index SECURITY_TOKENS_USER_ID = Internal.createIndex(DSL.name("user_id"), SecurityTokens.SECURITY_TOKENS, new OrderField[] { SecurityTokens.SECURITY_TOKENS.USER_ID }, false);
}
