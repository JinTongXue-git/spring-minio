package com.springminio.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 通用响应结果封装类，用于统一接口返回格式
 *
 * <p>包含状态码、提示消息和响应数据，提供 ok 和 error 静态工厂方法快速构建实例</p>
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class R {

    private int code;

    private String message;

    private Object data;

    public static R ok(){
        return new R(200 , "成功" , null);
    }

    public static R ok(Object data){
        return new R(200 , "成功" , data);
    }

    public static R ok(String message){
        return new R(200 , message , null);
    }

    public static R ok(String message , Object data){
        return new R(200 , message , data);
    }

    public static R ok(int code , String message , Object data){
        return new R(code , message , data);
    }

    public static R error(){
        return new R(500 , "失败" , null);
    }

    public static R error(String message){
        return new R(500 , message , null);
    }


}
