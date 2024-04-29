package org.mvasylchuk.pfcc.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.mvasylchuk.pfcc.common.dto.BaseResponse;
import org.mvasylchuk.pfcc.platform.configuration.model.PfccAppConfigurationProperties;
import org.mvasylchuk.pfcc.user.dto.AuthTokenResponseDto;
import org.mvasylchuk.pfcc.user.dto.AuthTokensDto;
import org.mvasylchuk.pfcc.user.dto.LoginRequestDto;
import org.mvasylchuk.pfcc.user.dto.ProfileDto;
import org.mvasylchuk.pfcc.user.dto.RefreshAuthTokenRequestDto;
import org.mvasylchuk.pfcc.user.dto.RegisterRequestDto;
import org.mvasylchuk.pfcc.user.dto.SaveProfileRequestDto;
import org.mvasylchuk.pfcc.user.dto.VerifyAccountRequestDto;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    private final UserService userService;
    private final PfccAppConfigurationProperties conf;

    @PostMapping("/register")
    public ResponseEntity<BaseResponse<AuthTokenResponseDto>> register(@RequestBody @Valid RegisterRequestDto request) throws URISyntaxException {
        return buildAuthTokenResponse(userService.register(request));
    }

    @PostMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public BaseResponse<Void> saveProfile(@RequestBody SaveProfileRequestDto request) {
        userService.saveProfile(request);
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
        return BaseResponse.success(userService.getCurrentUserProfile());
    }

    @PostMapping("/verify")
    public ResponseEntity<BaseResponse<AuthTokenResponseDto>> verifyAccount(@RequestBody VerifyAccountRequestDto request) throws URISyntaxException {
        return buildAuthTokenResponse(userService.verifyAccount(request));
    }

    @NotNull
    private ResponseEntity<BaseResponse<AuthTokenResponseDto>> buildAuthTokenResponse(AuthTokensDto token) throws URISyntaxException {
        URI uri = new URI(conf.auth.issuer);
        ResponseCookie accessToken = ResponseCookie.from("access-token", token.getAccessToken())
                                                   .httpOnly(true)
                                                   .secure(uri.getScheme().equals("https"))
                                                   .path("/api")
                                                   .maxAge(conf.auth.authTokenExpiration)
                                                   .domain(uri.getHost())
                                                   .sameSite(conf.auth.sameSite.attributeValue())
                                                   .build();

        return ResponseEntity.ok()
                             .header(SET_COOKIE, accessToken + "; Partitioned;")
                             .body(BaseResponse.success(new AuthTokenResponseDto(token.getRefreshToken())));
    }
}
