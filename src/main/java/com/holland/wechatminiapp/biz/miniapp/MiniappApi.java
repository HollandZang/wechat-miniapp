package com.holland.wechatminiapp.biz.miniapp;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.config.impl.WxMaRedisBetterConfigImpl;
import com.holland.wechatminiapp.constants.SpringConstants;
import com.holland.wechatminiapp.plugin.validator.groups.Insert;
import com.holland.wechatminiapp.plugin.validator.groups.Update;
import com.holland.wechatminiapp.kit.PageSelector;
import com.holland.wechatminiapp.kit.Res;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.redis.RedisTemplateWxRedisOps;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static com.holland.wechatminiapp.constants.WechatConstants.WX_MA_KEY_PREFIX;

@Slf4j
@Validated
@RestController
@RequestMapping("/wechat/miniapps")
public class MiniappApi {

    @Resource
    private HttpServletRequest      request;
    @Resource
    private RedisTemplateWxRedisOps redisTemplateWxRedisOps;
    @Resource
    private WxMaService             wxMaService;
    @Resource
    private MiniappMapper           miniappMapper;

    @GetMapping("")
    public Res<List<Miniapp>> list(@Validated PageSelector pageSelector) {
        pageSelector.startMyPage();
        List<Miniapp> miniapps = miniappMapper.findAll();
        return Res.success(miniapps);
    }

    @GetMapping("/{appid}")
    public Res<Miniapp> info(@PathVariable String appid) {
        return miniappMapper.findByAppid(appid)
                .map(Res::success)
                .orElse(Res.notFound(appid));
    }

    @PostMapping("")
    public Res<?> create(@Validated(Insert.class) @RequestBody Miniapp miniapp) {
        boolean exists = miniappMapper.existsByAppid(miniapp.appid);
        if (exists) {
            return Res.exists(miniapp.appid);
        }

        int row = miniappMapper.save(miniapp);

        log.info("{} 加载小程序: {}", request.getAttribute(SpringConstants.KEY_REQ_UID), miniapp);
        WxMaRedisBetterConfigImpl config = new WxMaRedisBetterConfigImpl(redisTemplateWxRedisOps, WX_MA_KEY_PREFIX + miniapp.appid);
        config.setAppid(miniapp.appid);
        config.setSecret(miniapp.secret);
        wxMaService.addConfig(miniapp.appid, config);

        return Res.success(row);
    }

    @PutMapping("")
    public Res<?> update(@Validated(Update.class) @RequestBody Miniapp miniapp) {
        int row = miniappMapper.updateSelective(miniapp);
        return Res.success(row);
    }
}
