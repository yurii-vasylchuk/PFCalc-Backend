/*
 * This file is generated by jOOQ.
 */
package org.mvasylchuk.pfcc.jooq.tables;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Function12;
import org.jooq.Index;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Records;
import org.jooq.Row12;
import org.jooq.Schema;
import org.jooq.SelectField;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;
import org.mvasylchuk.pfcc.jooq.Indexes;
import org.mvasylchuk.pfcc.jooq.Keys;
import org.mvasylchuk.pfcc.jooq.Pfcc;
import org.mvasylchuk.pfcc.jooq.tables.records.DishRecord;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Dish extends TableImpl<DishRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>pfcc.dish</code>
     */
    public static final Dish DISH = new Dish();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<DishRecord> getRecordType() {
        return DishRecord.class;
    }

    /**
     * The column <code>pfcc.dish.id</code>.
     */
    public final TableField<DishRecord, Long> ID = createField(DSL.name("id"), SQLDataType.BIGINT.nullable(false).defaultValue(DSL.field(DSL.raw("nextval(`pfcc`.`dish_id_seq`)"), SQLDataType.BIGINT)), this, "");

    /**
     * The column <code>pfcc.dish.name</code>.
     */
    public final TableField<DishRecord, String> NAME = createField(DSL.name("name"), SQLDataType.VARCHAR(255).nullable(false), this, "");

    /**
     * The column <code>pfcc.dish.food_id</code>.
     */
    public final TableField<DishRecord, Long> FOOD_ID = createField(DSL.name("food_id"), SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>pfcc.dish.recipe_weight</code>.
     */
    public final TableField<DishRecord, BigDecimal> RECIPE_WEIGHT = createField(DSL.name("recipe_weight"), SQLDataType.DECIMAL(9, 4).nullable(false), this, "");

    /**
     * The column <code>pfcc.dish.cooked_weight</code>.
     */
    public final TableField<DishRecord, BigDecimal> COOKED_WEIGHT = createField(DSL.name("cooked_weight"), SQLDataType.DECIMAL(9, 4).nullable(false), this, "");

    /**
     * The column <code>pfcc.dish.protein</code>.
     */
    public final TableField<DishRecord, BigDecimal> PROTEIN = createField(DSL.name("protein"), SQLDataType.DECIMAL(9, 4).nullable(false), this, "");

    /**
     * The column <code>pfcc.dish.fat</code>.
     */
    public final TableField<DishRecord, BigDecimal> FAT = createField(DSL.name("fat"), SQLDataType.DECIMAL(9, 4).nullable(false), this, "");

    /**
     * The column <code>pfcc.dish.carbohydrates</code>.
     */
    public final TableField<DishRecord, BigDecimal> CARBOHYDRATES = createField(DSL.name("carbohydrates"), SQLDataType.DECIMAL(9, 4).nullable(false), this, "");

    /**
     * The column <code>pfcc.dish.calories</code>.
     */
    public final TableField<DishRecord, BigDecimal> CALORIES = createField(DSL.name("calories"), SQLDataType.DECIMAL(9, 4).nullable(false), this, "");

    /**
     * The column <code>pfcc.dish.cooked_on</code>.
     */
    public final TableField<DishRecord, LocalDateTime> COOKED_ON = createField(DSL.name("cooked_on"), SQLDataType.LOCALDATETIME(0).defaultValue(DSL.field(DSL.raw("current_timestamp()"), SQLDataType.LOCALDATETIME)), this, "");

    /**
     * The column <code>pfcc.dish.deleted</code>.
     */
    public final TableField<DishRecord, Byte> DELETED = createField(DSL.name("deleted"), SQLDataType.TINYINT.nullable(false).defaultValue(DSL.field(DSL.raw("0"), SQLDataType.TINYINT)), this, "");

    /**
     * The column <code>pfcc.dish.owner_id</code>.
     */
    public final TableField<DishRecord, Long> OWNER_ID = createField(DSL.name("owner_id"), SQLDataType.BIGINT.defaultValue(DSL.field(DSL.raw("NULL"), SQLDataType.BIGINT)), this, "");

    private Dish(Name alias, Table<DishRecord> aliased) {
        this(alias, aliased, null);
    }

    private Dish(Name alias, Table<DishRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>pfcc.dish</code> table reference
     */
    public Dish(String alias) {
        this(DSL.name(alias), DISH);
    }

    /**
     * Create an aliased <code>pfcc.dish</code> table reference
     */
    public Dish(Name alias) {
        this(alias, DISH);
    }

    /**
     * Create a <code>pfcc.dish</code> table reference
     */
    public Dish() {
        this(DSL.name("dish"), null);
    }

    public <O extends Record> Dish(Table<O> child, ForeignKey<O, DishRecord> key) {
        super(child, key, DISH);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : Pfcc.PFCC;
    }

    @Override
    public List<Index> getIndexes() {
        return Arrays.asList(Indexes.DISH_FOOD_ID, Indexes.DISH_OWNER_ID);
    }

    @Override
    public UniqueKey<DishRecord> getPrimaryKey() {
        return Keys.KEY_DISH_PRIMARY;
    }

    @Override
    public List<ForeignKey<DishRecord, ?>> getReferences() {
        return Arrays.asList(Keys.DISH_IBFK_1, Keys.DISH_IBFK_2);
    }

    private transient Food _food;
    private transient Users _users;

    /**
     * Get the implicit join path to the <code>pfcc.food</code> table.
     */
    public Food food() {
        if (_food == null)
            _food = new Food(this, Keys.DISH_IBFK_1);

        return _food;
    }

    /**
     * Get the implicit join path to the <code>pfcc.users</code> table.
     */
    public Users users() {
        if (_users == null)
            _users = new Users(this, Keys.DISH_IBFK_2);

        return _users;
    }

    @Override
    public Dish as(String alias) {
        return new Dish(DSL.name(alias), this);
    }

    @Override
    public Dish as(Name alias) {
        return new Dish(alias, this);
    }

    @Override
    public Dish as(Table<?> alias) {
        return new Dish(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public Dish rename(String name) {
        return new Dish(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Dish rename(Name name) {
        return new Dish(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public Dish rename(Table<?> name) {
        return new Dish(name.getQualifiedName(), null);
    }

    // -------------------------------------------------------------------------
    // Row12 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row12<Long, String, Long, BigDecimal, BigDecimal, BigDecimal, BigDecimal, BigDecimal, BigDecimal, LocalDateTime, Byte, Long> fieldsRow() {
        return (Row12) super.fieldsRow();
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Function)}.
     */
    public <U> SelectField<U> mapping(Function12<? super Long, ? super String, ? super Long, ? super BigDecimal, ? super BigDecimal, ? super BigDecimal, ? super BigDecimal, ? super BigDecimal, ? super BigDecimal, ? super LocalDateTime, ? super Byte, ? super Long, ? extends U> from) {
        return convertFrom(Records.mapping(from));
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Class,
     * Function)}.
     */
    public <U> SelectField<U> mapping(Class<U> toType, Function12<? super Long, ? super String, ? super Long, ? super BigDecimal, ? super BigDecimal, ? super BigDecimal, ? super BigDecimal, ? super BigDecimal, ? super BigDecimal, ? super LocalDateTime, ? super Byte, ? super Long, ? extends U> from) {
        return convertFrom(toType, Records.mapping(from));
    }
}
