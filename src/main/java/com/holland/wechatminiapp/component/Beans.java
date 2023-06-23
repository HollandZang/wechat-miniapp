package com.holland.wechatminiapp.component;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceHttpClientImpl;
import cn.binarywang.wx.miniapp.config.impl.WxMaRedisBetterConfigImpl;
import com.holland.wechatminiapp.biz.miniapp.MiniappMapper;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.redis.RedisTemplateWxRedisOps;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.annotation.Resource;
import javax.sql.DataSource;

import static com.holland.wechatminiapp.constants.WechatConstants.WX_MA_KEY_PREFIX;

@Slf4j
@Configuration
public class Beans {

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private MiniappMapper       miniappMapper;
    @Resource
    private DataSource          dataSource;

    @Bean
    public RedisTemplateWxRedisOps redisTemplateWxRedisOps() {
        return new RedisTemplateWxRedisOps(stringRedisTemplate);
    }

    @Bean
    public WxMaService wxMaService(RedisTemplateWxRedisOps redisTemplateWxRedisOps) {
        WxMaServiceHttpClientImpl client = new WxMaServiceHttpClientImpl();

        miniappMapper.findAll().forEach(miniapp -> {
            log.info("加载小程序: {}", miniapp);
            WxMaRedisBetterConfigImpl config = new WxMaRedisBetterConfigImpl(redisTemplateWxRedisOps, WX_MA_KEY_PREFIX + miniapp.appid);
            config.setAppid(miniapp.appid);
            config.setSecret(miniapp.secret);
            client.addConfig(miniapp.appid, config);
        });

        return client;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .authorizeRequests()
//                .mvcMatchers("/actuator/**").hasRole("ADMIN")
//                .mvcMatchers("/sys/**").hasRole("ADMIN")
                .anyRequest().permitAll()
                .and().formLogin()
                .and().cors()
                .and().csrf().disable()
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(8);
    }

    @Bean
    public JdbcUserDetailsManager jdbcUserDetailsManager() {
        JdbcUserDetailsManager manager = new JdbcUserDetailsManager();
        manager.setDataSource(dataSource);
        return manager;
    }

}
