package com.atguigu.lease.common.exception;

import com.atguigu.lease.common.result.Result;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Result error(Exception e){
        e.printStackTrace();
        return Result.fail();
    }

    @ExceptionHandler(LeaseException.class)
    @ResponseBody
    public Result error(LeaseException e){
        e.printStackTrace();
        return Result.fail(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseBody
    public Result error(MaxUploadSizeExceededException e){
        e.printStackTrace();
        return Result.fail(202, "上传文件不能超过10MB");
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseBody
    public Result error(HttpMessageNotReadableException e){
        e.printStackTrace();
        return Result.fail(202, "请求参数格式错误，日期时间请使用 yyyy-MM-dd HH:mm:ss");
    }
}
