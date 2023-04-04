package com.holland.wechatminiapp.kit;

import com.holland.wechatminiapp.plugin.mybaties.MyPageHelper;
import lombok.AllArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
public class PageSelector {
    @Min(1)
    @NotNull(message = "page不能为空")
    public final Integer page;
    @Min(1)
    @NotNull(message = "size不能为空")
    public final Integer size;

    public void startMyPage() {
        MyPageHelper.startPage(page, size);
    }
}
