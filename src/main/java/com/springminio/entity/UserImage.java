package com.springminio.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("t_user_image")
public class UserImage {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer uid;        // 逻辑关联 user_info.id
    private String bucket;
    private String object;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}