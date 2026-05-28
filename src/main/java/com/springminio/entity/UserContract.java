package com.springminio.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("t_user_contract")
public class UserContract {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer uid;
    private String bucket;
    private String object;
    private String contractName;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}