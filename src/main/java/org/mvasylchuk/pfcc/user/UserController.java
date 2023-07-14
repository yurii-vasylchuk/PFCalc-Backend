package org.mvasylchuk.pfcc.user;

import lombok.RequiredArgsConstructor;
import org.mvasylchuk.pfcc.platform.dto.BaseResponse;
import org.mvasylchuk.pfcc.user.dto.AccessTokenDto;
import org.mvasylchuk.pfcc.user.dto.LoginRequestDto;
import org.mvasylchuk.pfcc.user.dto.RegisterRequestDto;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/register")
    public BaseResponse<AccessTokenDto> register(@RequestBody RegisterRequestDto request) {
        return BaseResponse.success(userService.register(request));
    }

    @PostMapping("/login")
    public BaseResponse<AccessTokenDto> login(@RequestBody LoginRequestDto request) {
        return BaseResponse.success(userService.login(request));
    }
}
