package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.mapper.UserMapper;
import com.sky.service.UserService;
import com.sky.properties.WeChatProperties;
import com.sky.utils.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    // 微信服务接口地址
    private static final String WECHAT_LOGIN_URL = "https://api.weixin.qq.com/sns/jscode2session";

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private WeChatProperties weChatProperties;

    /**
     * 微信登录
     * @param userLoginDTO
     */
    @Override
    public User wxLogin(UserLoginDTO userLoginDTO) {
        // 1.获取微信登录凭证code
        String openid = getOpenId(userLoginDTO);

        // 判断openid是否为空，为空则说明登录失败
        if (openid == null || openid.isEmpty()) {
            throw new RuntimeException("微信登录失败，无法获取openid");
        }

        // 根据openid查询数据库中用户信息
        User user = userMapper.getByOpenid(openid);

        // 如果用户不存在，则注册新用户
        if (user == null) {
            user = User.builder()
                    .openid(openid)
                    .createTime(LocalDateTime.now())
                    .build();
            userMapper.insert(user);
        }

        // 6.返回用户信息
        return user;
    }

    /**
     * 获取微信用户openid
     * @param userLoginDTO
     * @return
     */
    private String getOpenId(UserLoginDTO userLoginDTO) {
        // 1.封装HttpClint请求参数
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("appid", weChatProperties.getAppid());
        paramMap.put("secret", weChatProperties.getSecret());
        paramMap.put("js_code", userLoginDTO.getCode());
        paramMap.put("grant_type", "authorization_code");

        // 2.发送请求到微信服务器获取openid
        String json = HttpClientUtil.doGet(WECHAT_LOGIN_URL, paramMap);
        JSONObject jsonObject = JSON.parseObject(json);
        String openid = jsonObject.getString("openid");
        return openid;
    }
}