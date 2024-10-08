/*
 * This file is generated by jOOQ.
 */
package org.mvasylchuk.pfcc.jooq.tables.records;


import java.time.LocalDateTime;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record7;
import org.jooq.Row7;
import org.jooq.impl.UpdatableRecordImpl;
import org.mvasylchuk.pfcc.jooq.tables.Reports;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class ReportsRecord extends UpdatableRecordImpl<ReportsRecord> implements Record7<Long, Long, String, String, String, String, LocalDateTime> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>public.reports.id</code>.
     */
    public void setId(Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>public.reports.id</code>.
     */
    public Long getId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>public.reports.user_id</code>.
     */
    public void setUserId(Long value) {
        set(1, value);
    }

    /**
     * Getter for <code>public.reports.user_id</code>.
     */
    public Long getUserId() {
        return (Long) get(1);
    }

    /**
     * Setter for <code>public.reports.name</code>.
     */
    public void setName(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>public.reports.name</code>.
     */
    public String getName() {
        return (String) get(2);
    }

    /**
     * Setter for <code>public.reports.file_path</code>.
     */
    public void setFilePath(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>public.reports.file_path</code>.
     */
    public String getFilePath() {
        return (String) get(3);
    }

    /**
     * Setter for <code>public.reports.status</code>.
     */
    public void setStatus(String value) {
        set(4, value);
    }

    /**
     * Getter for <code>public.reports.status</code>.
     */
    public String getStatus() {
        return (String) get(4);
    }

    /**
     * Setter for <code>public.reports.type</code>.
     */
    public void setType(String value) {
        set(5, value);
    }

    /**
     * Getter for <code>public.reports.type</code>.
     */
    public String getType() {
        return (String) get(5);
    }

    /**
     * Setter for <code>public.reports.created_at</code>.
     */
    public void setCreatedAt(LocalDateTime value) {
        set(6, value);
    }

    /**
     * Getter for <code>public.reports.created_at</code>.
     */
    public LocalDateTime getCreatedAt() {
        return (LocalDateTime) get(6);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<Long> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record7 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row7<Long, Long, String, String, String, String, LocalDateTime> fieldsRow() {
        return (Row7) super.fieldsRow();
    }

    @Override
    public Row7<Long, Long, String, String, String, String, LocalDateTime> valuesRow() {
        return (Row7) super.valuesRow();
    }

    @Override
    public Field<Long> field1() {
        return Reports.REPORTS.ID;
    }

    @Override
    public Field<Long> field2() {
        return Reports.REPORTS.USER_ID;
    }

    @Override
    public Field<String> field3() {
        return Reports.REPORTS.NAME;
    }

    @Override
    public Field<String> field4() {
        return Reports.REPORTS.FILE_PATH;
    }

    @Override
    public Field<String> field5() {
        return Reports.REPORTS.STATUS;
    }

    @Override
    public Field<String> field6() {
        return Reports.REPORTS.TYPE;
    }

    @Override
    public Field<LocalDateTime> field7() {
        return Reports.REPORTS.CREATED_AT;
    }

    @Override
    public Long component1() {
        return getId();
    }

    @Override
    public Long component2() {
        return getUserId();
    }

    @Override
    public String component3() {
        return getName();
    }

    @Override
    public String component4() {
        return getFilePath();
    }

    @Override
    public String component5() {
        return getStatus();
    }

    @Override
    public String component6() {
        return getType();
    }

    @Override
    public LocalDateTime component7() {
        return getCreatedAt();
    }

    @Override
    public Long value1() {
        return getId();
    }

    @Override
    public Long value2() {
        return getUserId();
    }

    @Override
    public String value3() {
        return getName();
    }

    @Override
    public String value4() {
        return getFilePath();
    }

    @Override
    public String value5() {
        return getStatus();
    }

    @Override
    public String value6() {
        return getType();
    }

    @Override
    public LocalDateTime value7() {
        return getCreatedAt();
    }

    @Override
    public ReportsRecord value1(Long value) {
        setId(value);
        return this;
    }

    @Override
    public ReportsRecord value2(Long value) {
        setUserId(value);
        return this;
    }

    @Override
    public ReportsRecord value3(String value) {
        setName(value);
        return this;
    }

    @Override
    public ReportsRecord value4(String value) {
        setFilePath(value);
        return this;
    }

    @Override
    public ReportsRecord value5(String value) {
        setStatus(value);
        return this;
    }

    @Override
    public ReportsRecord value6(String value) {
        setType(value);
        return this;
    }

    @Override
    public ReportsRecord value7(LocalDateTime value) {
        setCreatedAt(value);
        return this;
    }

    @Override
    public ReportsRecord values(Long value1, Long value2, String value3, String value4, String value5, String value6, LocalDateTime value7) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        value7(value7);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached ReportsRecord
     */
    public ReportsRecord() {
        super(Reports.REPORTS);
    }

    /**
     * Create a detached, initialised ReportsRecord
     */
    public ReportsRecord(Long id, Long userId, String name, String filePath, String status, String type, LocalDateTime createdAt) {
        super(Reports.REPORTS);

        setId(id);
        setUserId(userId);
        setName(name);
        setFilePath(filePath);
        setStatus(status);
        setType(type);
        setCreatedAt(createdAt);
        resetChangedOnNotNull();
    }
}
