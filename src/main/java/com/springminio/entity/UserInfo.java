package com.springminio.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("t_user_info")
public class UserInfo {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String nick;
    private String password;
    private Integer sex;
    private String phone;
    private String email;
    private String address;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}