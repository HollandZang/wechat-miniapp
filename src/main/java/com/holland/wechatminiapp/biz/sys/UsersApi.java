package com.holland.wechatminiapp.biz.sys;

import com.holland.wechatminiapp.kit.PageSelector;
import com.holland.wechatminiapp.kit.Res;
import com.holland.wechatminiapp.plugin.validator.groups.Insert;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/sys/users")
public class UsersApi {

    @Resource
    private JdbcUserDetailsManager jdbcUserDetailsManager;
    @Resource
    private PasswordEncoder        encoder;
    @Resource
    private UsersMapper            usersMapper;

    @PostMapping("")
    public Res<?> create(@Validated(Insert.class) @RequestBody UsersDTO usersDTO) {
        jdbcUserDetailsManager.createUser(User
                .withUsername(usersDTO.username)
                .password(encoder.encode(usersDTO.password))
                .roles(usersDTO.roles.toArray(String[]::new))
                .build());
        return Res.success();
    }

    @GetMapping("")
    public Res<?> list(@Validated PageSelector pageSelector) {
        pageSelector.startMyPage();
        List<UsersDTO> userDetailsList = usersMapper.list();
        return Res.success(userDetailsList);
    }
}
