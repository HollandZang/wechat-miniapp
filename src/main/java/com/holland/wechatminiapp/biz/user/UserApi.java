package com.holland.wechatminiapp.biz.user;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import com.holland.wechatminiapp.kit.Res;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.constraints.NotBlank;

@Slf4j
@Validated
@RestController
@RequestMapping("/user/{appid}")
public class UserApi {

    @Resource
    private WxMaService wxMaService;

    @PostMapping("/login")
    public Res<WxMaJscode2SessionResult> login(
            @PathVariable String appid,
            @NotBlank(message = "code不能为空") String code
    ) throws WxErrorException {
        WxMaJscode2SessionResult sessionInfo = wxMaService.getUserService().getSessionInfo(code);
        return Res.success(sessionInfo);
    }
}
