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

import javax.annotation.Resource;

import static com.holland.wechatminiapp.constants.WechatConstants.WX_MA_KEY_PREFIX;

@Slf4j
@Configuration
public class Beans {

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private MiniappMapper       miniappMapper;

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

}
