package com.holland.wechatminiapp.biz.sys;

import com.holland.wechatminiapp.plugin.validator.groups.Insert;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@With
@Builder
@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsersDTO {
    @NotNull(message = "username不能为空")
    @Length(max = 50, message = "username不能超过50")
    public String       username;
    @NotNull(groups = Insert.class, message = "password不能为空")
    @Length(max = 50, message = "password不能超过50")
    public String       password;
    public Boolean      enabled;
    @NotNull(groups = Insert.class, message = "roles不能为空")
    @Size(groups = Insert.class, min = 1, message = "roles不能为空")
    public List<String> roles;
}
