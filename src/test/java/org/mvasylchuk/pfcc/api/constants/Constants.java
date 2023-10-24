package org.mvasylchuk.pfcc.api.constants;

import lombok.Getter;
import org.mvasylchuk.pfcc.user.Language;

import java.math.BigDecimal;

public interface Constants {
    @Getter
    enum TestUser {
        alpha("alpha", "alpha@test.com", "password", Language.UA, true, true, "130", "50", "205", "2500", "USER");

        private final String name;
        private final String email;
        private final String password;
        private final Language preferredLanguage;
        private final Boolean profileConfigured;
        private final Boolean emailConfirmed;
        private final BigDecimal proteinAim;
        private final BigDecimal fatAim;
        private final BigDecimal carbohydratesAim;
        private final BigDecimal caloriesAim;
        private final String roles;

        TestUser(String name, String email, String password, Language preferredLanguage, Boolean profileConfigured, Boolean emailConfirmed, String proteinAim, String fatAim, String carbohydratesAim, String caloriesAim, String roles) {
            this.name = name;
            this.email = email;
            this.password = password;
            this.preferredLanguage = preferredLanguage;
            this.profileConfigured = profileConfigured;
            this.emailConfirmed = emailConfirmed;
            this.proteinAim = proteinAim != null ? new BigDecimal(proteinAim) : null;
            this.fatAim = fatAim != null ? new BigDecimal(fatAim) : null;
            this.carbohydratesAim = carbohydratesAim != null ? new BigDecimal(carbohydratesAim) : null;
            this.caloriesAim = caloriesAim != null ? new BigDecimal(caloriesAim) : null;
            this.roles = roles;
        }
    }

    interface Db {
        byte TRUE = 1;
        byte FALSE = 0;
    }
}
