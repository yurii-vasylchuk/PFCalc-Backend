/*
 * This file is generated by jOOQ.
 */
package org.mvasylchuk.pfcc.jooq;


import java.util.Arrays;
import java.util.List;

import org.jooq.Catalog;
import org.jooq.Sequence;
import org.jooq.Table;
import org.jooq.impl.SchemaImpl;
import org.mvasylchuk.pfcc.jooq.tables.Dish;
import org.mvasylchuk.pfcc.jooq.tables.DishIngredients;
import org.mvasylchuk.pfcc.jooq.tables.Food;
import org.mvasylchuk.pfcc.jooq.tables.Ingredients;
import org.mvasylchuk.pfcc.jooq.tables.Meal;
import org.mvasylchuk.pfcc.jooq.tables.Measurement;
import org.mvasylchuk.pfcc.jooq.tables.Reports;
import org.mvasylchuk.pfcc.jooq.tables.SecurityTokens;
import org.mvasylchuk.pfcc.jooq.tables.Users;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Pfcc extends SchemaImpl {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>pfcc</code>
     */
    public static final Pfcc PFCC = new Pfcc();

    /**
     * The table <code>pfcc.dish</code>.
     */
    public final Dish DISH = Dish.DISH;

    /**
     * The table <code>pfcc.dish_ingredients</code>.
     */
    public final DishIngredients DISH_INGREDIENTS = DishIngredients.DISH_INGREDIENTS;

    /**
     * The table <code>pfcc.food</code>.
     */
    public final Food FOOD = Food.FOOD;

    /**
     * The table <code>pfcc.ingredients</code>.
     */
    public final Ingredients INGREDIENTS = Ingredients.INGREDIENTS;

    /**
     * The table <code>pfcc.meal</code>.
     */
    public final Meal MEAL = Meal.MEAL;

    /**
     * The table <code>pfcc.measurement</code>.
     */
    public final Measurement MEASUREMENT = Measurement.MEASUREMENT;

    /**
     * The table <code>pfcc.reports</code>.
     */
    public final Reports REPORTS = Reports.REPORTS;

    /**
     * The table <code>pfcc.security_tokens</code>.
     */
    public final SecurityTokens SECURITY_TOKENS = SecurityTokens.SECURITY_TOKENS;

    /**
     * The table <code>pfcc.users</code>.
     */
    public final Users USERS = Users.USERS;

    /**
     * No further instances allowed
     */
    private Pfcc() {
        super("pfcc", null);
    }


    @Override
    public Catalog getCatalog() {
        return DefaultCatalog.DEFAULT_CATALOG;
    }

    @Override
    public final List<Sequence<?>> getSequences() {
        return Arrays.asList(
            Sequences.DISH_ID_SEQ,
            Sequences.FOOD_ID_SEQ,
            Sequences.MEAL_ID_SEQ,
            Sequences.MEASUREMENT_ID_SEQ,
            Sequences.REPORT_ID_SEQ,
            Sequences.SECURITY_TOKEN_ID_SEQ,
            Sequences.USER_ID_SEQ
        );
    }

    @Override
    public final List<Table<?>> getTables() {
        return Arrays.asList(
            Dish.DISH,
            DishIngredients.DISH_INGREDIENTS,
            Food.FOOD,
            Ingredients.INGREDIENTS,
            Meal.MEAL,
            Measurement.MEASUREMENT,
            Reports.REPORTS,
            SecurityTokens.SECURITY_TOKENS,
            Users.USERS
        );
    }
}
