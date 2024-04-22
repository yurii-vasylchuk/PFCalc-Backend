package org.mvasylchuk.pfcc.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Locale;

@Getter
@RequiredArgsConstructor
public enum Language {
    UA(new Locale("uk", "UA")),
    EN(new Locale("en", "US"));

    private final Locale locale;
}
