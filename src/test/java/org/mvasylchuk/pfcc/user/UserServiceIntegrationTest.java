package org.mvasylchuk.pfcc.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mvasylchuk.pfcc.IntegrationTest;
import org.mvasylchuk.pfcc.user.dto.RegisterRequestDto;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@IntegrationTest
class UserServiceIntegrationTest {
    @Autowired
    private UserService underTest;
    @Autowired
    private UserRepository userRepository;

    @Test
    void register() {

        underTest.register(new RegisterRequestDto("email", "password"));
        List<UserEntity> users = userRepository.findAll();
        Assertions.assertEquals(users.size(),1);
        Assertions.assertEquals(users.get(0).getEmail(),"email");
    }
}
