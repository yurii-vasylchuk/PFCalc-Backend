package org.mvasylchuk.pfcc.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.mvasylchuk.pfcc.common.dto.BaseResponse;
import org.mvasylchuk.pfcc.user.dto.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/register")
    public BaseResponse<AccessTokenDto> register(@RequestBody @Valid RegisterRequestDto request) {
        return BaseResponse.success(userService.register(request));
    }

    @PostMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public BaseResponse<Void> completeProfile(@RequestBody CompleteProfileRequestDto request) {
        userService.completeProfile(request);
        return BaseResponse.success(null);
    }

    @PostMapping("/login")
    public BaseResponse<AccessTokenDto> login(@RequestBody LoginRequestDto request) {
        return BaseResponse.success(userService.login(request));
    }
    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public BaseResponse<ProfileDto> getProfile(){
        return BaseResponse.success(userService.getUserProfile());
    }

    @PostMapping("/verify")
    public BaseResponse<AccessTokenDto> verifyAccount(@RequestBody VerifyAccountRequestDto request) {
        return BaseResponse.success(userService.verifyAccount(request));
    }
}
