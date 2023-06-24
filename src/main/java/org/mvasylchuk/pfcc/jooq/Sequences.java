/*
 * This file is generated by jOOQ.
 */
package org.mvasylchuk.pfcc.jooq;


import org.jooq.Sequence;
import org.jooq.impl.Internal;
import org.jooq.impl.SQLDataType;


/**
 * Convenience access to all sequences in pfcc.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Sequences {

    /**
     * The sequence <code>pfcc.user_id_seq</code>
     */
    public static final Sequence<Long> USER_ID_SEQ = Internal.createSequence("user_id_seq", Pfcc.PFCC, SQLDataType.BIGINT, null, 50L, null, null, false, null);
}