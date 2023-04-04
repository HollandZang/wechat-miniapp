package com.holland.wechatminiapp.biz.sys;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UsersMapper {

    List<UsersDTO> list();
}
