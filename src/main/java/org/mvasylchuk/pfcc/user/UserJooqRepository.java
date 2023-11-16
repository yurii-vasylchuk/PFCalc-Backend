package org.mvasylchuk.pfcc.user;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.mvasylchuk.pfcc.common.dto.PfccDto;
import org.mvasylchuk.pfcc.user.dto.ProfileDto;
import org.springframework.stereotype.Component;

import static org.mvasylchuk.pfcc.jooq.tables.Users.USERS;

@Component
@RequiredArgsConstructor
public class UserJooqRepository {
    private final DSLContext ctx;

    public ProfileDto getProfileByUserEmail(String email) {
        return ctx.select(USERS.EMAIL,
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
    }
}
