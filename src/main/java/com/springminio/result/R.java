package com.springminio.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
