package com.atguigu.lease.web.app.service.impl;

import com.aliyun.dypnsapi20170525.Client;
import com.aliyun.dypnsapi20170525.models.CheckSmsVerifyCodeRequest;
import com.aliyun.dypnsapi20170525.models.CheckSmsVerifyCodeResponse;
import com.atguigu.lease.common.constant.RedisConstant;
import com.atguigu.lease.common.exception.LeaseException;
import com.atguigu.lease.common.result.ResultCodeEnum;
import com.atguigu.lease.common.utils.JwtUtil;
import com.atguigu.lease.common.utils.VerifyCodeUtil;
import com.atguigu.lease.model.entity.UserInfo;
import com.atguigu.lease.model.enums.BaseStatus;
import com.atguigu.lease.web.app.service.LoginService;
import com.atguigu.lease.web.app.service.SmsService;
import com.atguigu.lease.web.app.service.UserInfoService;
import com.atguigu.lease.web.app.vo.user.LoginVo;
import com.atguigu.lease.web.app.vo.user.UserInfoVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private SmsService smsService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private UserInfoService userInfoService;

    @Override
    public void getSMSCode(String phone) {
        //1. 检查手机号码是否为空
        if (!StringUtils.hasText(phone)) {
            throw new LeaseException(ResultCodeEnum.APP_LOGIN_PHONE_EMPTY);
        }

        //2. 检查Redis中是否已经存在该手机号码的key
        String key = RedisConstant.APP_LOGIN_PREFIX + phone;
        boolean hasKey = Boolean.TRUE.equals(redisTemplate.hasKey(key));
        if (hasKey) {
            //若存在，则检查其存在的时间
            Long expire = redisTemplate.getExpire(key, TimeUnit.SECONDS);
            if (RedisConstant.APP_LOGIN_CODE_TTL_SEC - expire < RedisConstant.APP_LOGIN_CODE_RESEND_TIME_SEC) {
                //若存在时间不足一分钟，响应发送过于频繁
                throw new LeaseException(ResultCodeEnum.APP_SEND_SMS_TOO_OFTEN);
            }
        }

        // 3. 调用号码认证服务发送短信 (无需自己生成验证码)
        smsService.sendCode(phone);

        // 4. Redis 仅记录该手机号已发送过验证码（用于频率控制），值可以随便填
        redisTemplate.opsForValue().set(key, "SENT", RedisConstant.APP_LOGIN_CODE_TTL_SEC, TimeUnit.SECONDS);
    }

    @Override
    public String login(LoginVo loginVo) {
        //1.判断手机号码和验证码是否为空
        /*if (!StringUtils.hasText(loginVo.getPhone())) {
            throw new LeaseException(ResultCodeEnum.APP_LOGIN_PHONE_EMPTY);
        }

        boolean isVaild = smsService.verifyCode(loginVo.getPhone(), loginVo.getCode());
        if (!isVaild) {
            throw new LeaseException(ResultCodeEnum.APP_LOGIN_CODE_ERROR);
        }*/

        //3.判断用户是否存在,不存在则注册（创建用户）
        LambdaQueryWrapper<UserInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserInfo::getPhone, loginVo.getPhone());
        UserInfo userInfo = userInfoService.getOne(queryWrapper);
        if (userInfo == null) {
            userInfo = new UserInfo();
            userInfo.setPhone(loginVo.getPhone());
            userInfo.setStatus(BaseStatus.ENABLE);
            userInfo.setNickname("用户-"+loginVo.getPhone().substring(6));
            userInfoService.save(userInfo);
        }

        //4.判断用户是否被禁
        if (userInfo.getStatus().equals(BaseStatus.DISABLE)) {
            throw new LeaseException(ResultCodeEnum.APP_ACCOUNT_DISABLED_ERROR);
        }

        //5.创建并返回TOKEN
        return JwtUtil.createToken(userInfo.getId(), loginVo.getPhone());
    }

    @Override
    public UserInfoVo getUserInfoById(Long userId) {
        UserInfo userInfo = userInfoService.getById(userId);
        return new UserInfoVo(userInfo.getNickname(), userInfo.getAvatarUrl());
    }


}
