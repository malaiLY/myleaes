package com.atguigu.lease.web.app.service.impl;

import com.aliyun.dypnsapi20170525.Client;
import com.aliyun.dypnsapi20170525.models.CheckSmsVerifyCodeRequest;
import com.aliyun.dypnsapi20170525.models.CheckSmsVerifyCodeResponse;
import com.aliyun.dypnsapi20170525.models.SendSmsVerifyCodeRequest;
import com.aliyun.dypnsapi20170525.models.SendSmsVerifyCodeResponse;
import com.aliyun.tea.TeaException;
import com.aliyun.teautil.models.RuntimeOptions;
import com.atguigu.lease.common.exception.LeaseException;
import com.atguigu.lease.common.sms.AliyunSMSProperties;
import com.atguigu.lease.model.enums.ResultCodeEnum;
import com.atguigu.lease.web.app.service.SmsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SmsServiceImpl implements SmsService {

    @Autowired
    private Client smsClient;

    @Autowired
    private AliyunSMSProperties properties;

    /*@Override
    public void sendCode(String phone, String code) {
        SendSmsVerifyCodeRequest request = new SendSmsVerifyCodeRequest()
                .setSignName(properties.getSignName())
                .setTemplateCode(properties.getTemplateCode())
                .setPhoneNumber(phone)
                .setTemplateParam("{\"code\":\"" + code + "\",\"min\":\"5\"}");

        RuntimeOptions runtime = new RuntimeOptions();

        try {
            SendSmsVerifyCodeResponse response = smsClient.sendSmsVerifyCodeWithOptions(request, runtime);
            String resultCode = response.getBody().getCode();
            if (!"OK".equals(resultCode)) {
                throw new RuntimeException("短信验证码发送失败：" + response.getBody().getMessage());
            }
        } catch (TeaException e) {
            throw new RuntimeException("短信验证码发送失败：" + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("短信验证码发送异常", e);
        }
    }*/

    /*@Override
    public void sendCode(String phone, String code) { // 注意：不需要传入 code
        SendSmsVerifyCodeRequest request = new SendSmsVerifyCodeRequest()
                .setSignName(properties.getSignName())
                .setTemplateCode(properties.getTemplateCode())
                .setPhoneNumber(phone)
                // 关键点：使用 ##code## 占位符，由阿里云自动生成并替换
                .setTemplateParam("{\"code\":\"##code##\"}");

        try {
            SendSmsVerifyCodeResponse response = smsClient.sendSmsVerifyCode(request);
            if (!"OK".equals(response.getBody().getCode())) {
                throw new LeaseException(ResultCodeStatus.SYSTEM_ERROR, response.getBody().getMessage());
            }
        } catch (Exception e) {
            throw new RuntimeException("号码认证发送异常", e);
        }
    }*/

    @Override
    public void sendCode(String phone) {
        // 1. 构造请求参数，注意根据你的模板变量名来写 JSON
        // 如果你的模板只有 ${code}，那就只传 code；如果有 ${min}，则加上 min
        String param = String.format("{\"code\":\"##code##\",\"min\":\"%s\"}", properties.getExpireMin());

        SendSmsVerifyCodeRequest request = new SendSmsVerifyCodeRequest()
                .setSignName(properties.getSignName())
                .setTemplateCode(properties.getTemplateCode())
                .setPhoneNumber(phone)
                .setTemplateParam(param);

        // 2. 设置运行参数（可选）
        RuntimeOptions runtime = new RuntimeOptions();

        try {
            // 3. 调用 API
            SendSmsVerifyCodeResponse response = smsClient.sendSmsVerifyCodeWithOptions(request, runtime);

            // 4. 校验响应状态
            if (!"OK".equals(response.getBody().getCode())) {
                log.error("短信发送失败，阿里云返回码：{}，消息：{}",
                        response.getBody().getCode(), response.getBody().getMessage());
                throw new LeaseException(ResultCodeEnum.APP_SEND_SMS_ERROR.toString());
            }
        } catch (TeaException error) {
            // 打印阿里云提供的诊断地址
            log.error("阿里云SDK异常消息：{}", error.getMessage());
            log.error("诊断建议：{}", error.getData().get("Recommend"));
            throw new LeaseException(ResultCodeEnum.APP_SEND_SMS_ERROR.toString());
        } catch (Exception e) {
            log.error("系统异常：", e);
            throw new LeaseException(ResultCodeEnum.APP_SEND_SMS_ERROR.toString());
        }
    }

    // 发送验证码到阿里云验证
    public boolean verifyCode(String phone, String code) {
        CheckSmsVerifyCodeRequest request = new CheckSmsVerifyCodeRequest()
                .setPhoneNumber(phone)
                .setVerifyCode(code);
        try {
            CheckSmsVerifyCodeResponse response = smsClient.checkSmsVerifyCode(request);
            return "OK".equals(response.getBody().getCode());
        } catch (Exception e) {
            return false;
        }
    }
}