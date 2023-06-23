package com.holland.wechatminiapp.biz.wechat_user;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaPhoneNumberInfo;
import cn.binarywang.wx.miniapp.bean.WxMaUniformMessage;
import com.holland.wechatminiapp.kit.Res;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.NotBlank;

@Slf4j
@Validated
@RestController
@RequestMapping("/wechat/user/{appid}")
public class WechatUserApi {

    @Resource
    private WxMaService wxMaService;

    @PostMapping("/login")
    public Res<WxMaJscode2SessionResult> login(
            @PathVariable(value = "appid") String ignoredAppid,
            @NotBlank(message = "code不能为空") String code
    ) throws WxErrorException {
        WxMaJscode2SessionResult sessionInfo = wxMaService.getUserService().getSessionInfo(code);
        // db 初始化用户信息
        return Res.success(sessionInfo);
    }

    @GetMapping("/phone")
    public Res<WxMaPhoneNumberInfo> phone(
            @PathVariable(value = "appid") String ignoredAppid,
            @NotBlank(message = "code不能为空") String code
    ) throws WxErrorException {
        WxMaPhoneNumberInfo phoneNoInfo = wxMaService.getUserService().getPhoneNoInfo(code);
        // db 更新用户信息 sendUniformMessage
        return Res.success(phoneNoInfo);
    }
}
