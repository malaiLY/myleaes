package com.atguigu.lease.web.app.service;

public interface SmsService {

    // void sendCode(String phone, String code);

    void sendCode(String phone);

    boolean verifyCode(String phone, String code);
}
