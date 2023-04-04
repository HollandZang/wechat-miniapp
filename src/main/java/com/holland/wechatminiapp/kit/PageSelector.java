package com.holland.wechatminiapp.kit;

import com.holland.wechatminiapp.plugin.mybaties.MyPageHelper;
import lombok.AllArgsConstructor;

import javax.validation.constraints.Min;

@AllArgsConstructor
public class PageSelector {
    @Min(1)
    public final int page = 1;
    @Min(1)
    public final int size = 10;

    public void startMyPage() {
        MyPageHelper.startPage(page, size);
    }
}
