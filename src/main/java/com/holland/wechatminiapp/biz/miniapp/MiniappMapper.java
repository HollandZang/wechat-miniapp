package com.holland.wechatminiapp.biz.miniapp;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface MiniappMapper {
    List<Miniapp> findAll();

    boolean existsByAppid(String id);

    Optional<Miniapp> findByAppid(String appid);

    int save(Miniapp miniapp);

    int updateSelective(Miniapp miniapp);
}
