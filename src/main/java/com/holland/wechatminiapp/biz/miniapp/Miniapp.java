package com.holland.wechatminiapp.biz.miniapp;

import com.holland.wechatminiapp.plugin.validator.groups.Insert;
import lombok.Builder;
import lombok.ToString;
import lombok.With;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

@With
@Builder
@ToString
public class Miniapp {
    @NotNull(message = "appid不能为空")
    @Length(min = 18, max = 18, message = "请输入正确的appid")
    public String appid;
    @NotNull(groups = Insert.class, message = "secret不能为空")
    @Length(min = 32, max = 32, message = "请输入正确的secret")
    public String secret;
    @Max(value = 20, message = "长度不能超过20")
    public String name;
}
