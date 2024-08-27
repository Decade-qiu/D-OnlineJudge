package com.decade.doj.user;

import com.decade.doj.user.domain.po.User;
import com.decade.doj.user.mapper.UserMapper;
import com.decade.doj.user.service.IUserService;
import com.decade.doj.user.utils.AESTool;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.SQLOutput;

@SpringBootTest
public class UserTest {

    @Autowired
    private IUserService userService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AESTool aesTool;

    @Test
    public void testAes() {
        String pods = "123";
        System.out.println(aesTool.encode(pods, aesTool.fnv1aHash(pods)));
        System.out.println(aesTool.match(pods, "1okxVZ0I1b0jrfTp7wyGpg=="));
    }

    @Test
    public void testSaveUser() {
        User user = userService.getById(1);
        System.out.println(user);
    }

}
