package org.mvasylchuk.pfcc.user;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.mvasylchuk.pfcc.jooq.tables.Users;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserJooqRepository {
    private final DSLContext ctx;

    //TODO: Example, delete it in future
    public String getEmailById(Long id) {
        return ctx.select(Users.USERS.EMAIL)
                .from(Users.USERS)
                .where(Users.USERS.ID.eq(id))
                .fetchOneInto(String.class);
    }
}
