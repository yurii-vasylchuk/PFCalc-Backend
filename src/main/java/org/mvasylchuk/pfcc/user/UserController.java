package org.mvasylchuk.pfcc.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.mvasylchuk.pfcc.common.dto.BaseResponse;
import org.mvasylchuk.pfcc.platform.configuration.model.PfccAppConfigurationProperties;
import org.mvasylchuk.pfcc.user.dto.*;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.TimeZone;

import static org.springframework.http.HttpHeaders.SET_COOKIE;

@Slf4j
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private static final Duration LOCAL_OFFSET = Duration.ofMillis(TimeZone.getDefault().getRawOffset());

    private final UserService userService;
    private final PfccAppConfigurationProperties conf;

    @PostMapping("/register")
    public ResponseEntity<BaseResponse<AuthTokenResponseDto>> register(@RequestBody @Valid RegisterRequestDto request) throws URISyntaxException {
        return buildAuthTokenResponse(userService.register(request));
    }

    @PostMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public BaseResponse<Void> completeProfile(@RequestBody CompleteProfileRequestDto request) {
        userService.completeProfile(request);
        return BaseResponse.success(null);
    }

    @PostMapping("/login")
    public ResponseEntity<BaseResponse<AuthTokenResponseDto>> login(@RequestBody LoginRequestDto request) throws URISyntaxException {
        return buildAuthTokenResponse(userService.login(request));
    }

    @PostMapping("/refresh-auth-token")
    public ResponseEntity<BaseResponse<AuthTokenResponseDto>> refreshAuthToken(@RequestBody @Valid RefreshAuthTokenRequestDto request) throws URISyntaxException {
        return buildAuthTokenResponse(userService.refreshAuth(request));
    }

    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public BaseResponse<ProfileDto> getProfile() {
        return BaseResponse.success(userService.getUserProfile());
    }

    @PostMapping("/verify")
    public ResponseEntity<BaseResponse<AuthTokenResponseDto>> verifyAccount(@RequestBody VerifyAccountRequestDto request) throws URISyntaxException {
        return buildAuthTokenResponse(userService.verifyAccount(request));
    }

    @NotNull
    private ResponseEntity<BaseResponse<AuthTokenResponseDto>> buildAuthTokenResponse(AuthTokensDto token) throws URISyntaxException {
        URI uri = new URI(conf.jwt.issuer);
        log.info(uri.getScheme());
        ResponseCookie accessToken = ResponseCookie.from("access-token", token.getAccessToken())
                                                   .httpOnly(true)
                                                   .secure(uri.getScheme().equals("https"))
                                                   .path("/")
                                                   .maxAge(conf.jwt.expiration.plus(LOCAL_OFFSET))
                                                   .domain(uri.getHost())
                                                   .build();

        return ResponseEntity.ok()
                             .header(SET_COOKIE, accessToken.toString())
                             .body(BaseResponse.success(new AuthTokenResponseDto(token.getRefreshToken())));
    }
}
