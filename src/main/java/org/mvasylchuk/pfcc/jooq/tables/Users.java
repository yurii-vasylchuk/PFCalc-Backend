/*
 * This file is generated by jOOQ.
 */
package org.mvasylchuk.pfcc.jooq.tables;


import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Function12;
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
import org.mvasylchuk.pfcc.jooq.Keys;
import org.mvasylchuk.pfcc.jooq.Pfcc;
import org.mvasylchuk.pfcc.jooq.tables.records.UsersRecord;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Users extends TableImpl<UsersRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>pfcc.users</code>
     */
    public static final Users USERS = new Users();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<UsersRecord> getRecordType() {
        return UsersRecord.class;
    }

    /**
     * The column <code>pfcc.users.id</code>.
     */
    public final TableField<UsersRecord, Long> ID = createField(DSL.name("id"), SQLDataType.BIGINT.nullable(false).defaultValue(DSL.field(DSL.raw("nextval(`pfcc`.`user_id_seq`)"), SQLDataType.BIGINT)), this, "");

    /**
     * The column <code>pfcc.users.email</code>.
     */
    public final TableField<UsersRecord, String> EMAIL = createField(DSL.name("email"), SQLDataType.VARCHAR(255).nullable(false), this, "");

    /**
     * The column <code>pfcc.users.preferred_language</code>.
     */
    public final TableField<UsersRecord, String> PREFERRED_LANGUAGE = createField(DSL.name("preferred_language"), SQLDataType.VARCHAR(3).nullable(false), this, "");

    /**
     * The column <code>pfcc.users.protein_aim</code>.
     */
    public final TableField<UsersRecord, BigDecimal> PROTEIN_AIM = createField(DSL.name("protein_aim"), SQLDataType.DECIMAL(9, 4).defaultValue(DSL.field(DSL.raw("NULL"), SQLDataType.DECIMAL)), this, "");

    /**
     * The column <code>pfcc.users.fat_aim</code>.
     */
    public final TableField<UsersRecord, BigDecimal> FAT_AIM = createField(DSL.name("fat_aim"), SQLDataType.DECIMAL(9, 4).defaultValue(DSL.field(DSL.raw("NULL"), SQLDataType.DECIMAL)), this, "");

    /**
     * The column <code>pfcc.users.carbohydrates_aim</code>.
     */
    public final TableField<UsersRecord, BigDecimal> CARBOHYDRATES_AIM = createField(DSL.name("carbohydrates_aim"), SQLDataType.DECIMAL(9, 4).defaultValue(DSL.field(DSL.raw("NULL"), SQLDataType.DECIMAL)), this, "");

    /**
     * The column <code>pfcc.users.calories_aim</code>.
     */
    public final TableField<UsersRecord, BigDecimal> CALORIES_AIM = createField(DSL.name("calories_aim"), SQLDataType.DECIMAL(9, 4).defaultValue(DSL.field(DSL.raw("NULL"), SQLDataType.DECIMAL)), this, "");

    /**
     * The column <code>pfcc.users.profile_configured</code>.
     */
    public final TableField<UsersRecord, Byte> PROFILE_CONFIGURED = createField(DSL.name("profile_configured"), SQLDataType.TINYINT.defaultValue(DSL.field(DSL.raw("NULL"), SQLDataType.TINYINT)), this, "");

    /**
     * The column <code>pfcc.users.email_confirmed</code>.
     */
    public final TableField<UsersRecord, Byte> EMAIL_CONFIRMED = createField(DSL.name("email_confirmed"), SQLDataType.TINYINT.defaultValue(DSL.field(DSL.raw("NULL"), SQLDataType.TINYINT)), this, "");

    /**
     * The column <code>pfcc.users.password</code>.
     */
    public final TableField<UsersRecord, String> PASSWORD = createField(DSL.name("password"), SQLDataType.VARCHAR(255).nullable(false), this, "");

    /**
     * The column <code>pfcc.users.roles</code>.
     */
    public final TableField<UsersRecord, String> ROLES = createField(DSL.name("roles"), SQLDataType.VARCHAR(255).nullable(false), this, "");

    /**
     * The column <code>pfcc.users.name</code>.
     */
    public final TableField<UsersRecord, String> NAME = createField(DSL.name("name"), SQLDataType.VARCHAR(255).nullable(false), this, "");

    private Users(Name alias, Table<UsersRecord> aliased) {
        this(alias, aliased, null);
    }

    private Users(Name alias, Table<UsersRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>pfcc.users</code> table reference
     */
    public Users(String alias) {
        this(DSL.name(alias), USERS);
    }

    /**
     * Create an aliased <code>pfcc.users</code> table reference
     */
    public Users(Name alias) {
        this(alias, USERS);
    }

    /**
     * Create a <code>pfcc.users</code> table reference
     */
    public Users() {
        this(DSL.name("users"), null);
    }

    public <O extends Record> Users(Table<O> child, ForeignKey<O, UsersRecord> key) {
        super(child, key, USERS);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : Pfcc.PFCC;
    }

    @Override
    public UniqueKey<UsersRecord> getPrimaryKey() {
        return Keys.KEY_USERS_PRIMARY;
    }

    @Override
    public List<UniqueKey<UsersRecord>> getUniqueKeys() {
        return Arrays.asList(Keys.KEY_USERS_EMAIL);
    }

    @Override
    public Users as(String alias) {
        return new Users(DSL.name(alias), this);
    }

    @Override
    public Users as(Name alias) {
        return new Users(alias, this);
    }

    @Override
    public Users as(Table<?> alias) {
        return new Users(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public Users rename(String name) {
        return new Users(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Users rename(Name name) {
        return new Users(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public Users rename(Table<?> name) {
        return new Users(name.getQualifiedName(), null);
    }

    // -------------------------------------------------------------------------
    // Row12 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row12<Long, String, String, BigDecimal, BigDecimal, BigDecimal, BigDecimal, Byte, Byte, String, String, String> fieldsRow() {
        return (Row12) super.fieldsRow();
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Function)}.
     */
    public <U> SelectField<U> mapping(Function12<? super Long, ? super String, ? super String, ? super BigDecimal, ? super BigDecimal, ? super BigDecimal, ? super BigDecimal, ? super Byte, ? super Byte, ? super String, ? super String, ? super String, ? extends U> from) {
        return convertFrom(Records.mapping(from));
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Class,
     * Function)}.
     */
    public <U> SelectField<U> mapping(Class<U> toType, Function12<? super Long, ? super String, ? super String, ? super BigDecimal, ? super BigDecimal, ? super BigDecimal, ? super BigDecimal, ? super Byte, ? super Byte, ? super String, ? super String, ? super String, ? extends U> from) {
        return convertFrom(toType, Records.mapping(from));
    }
}
