package org.mvasylchuk.pfcc.api;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.ResultActions;

@Component
@Getter
@Setter
public class ApiTestContext {
    private String request;
    private ResultActions performedCalls;
    private String verifyEmailToken;
    private String authToken;
}
