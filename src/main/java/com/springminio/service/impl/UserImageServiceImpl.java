package com.springminio.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.springminio.entity.UserImage;
import com.springminio.service.UserImageService;
import com.springminio.mapper.UserImageMapper;
import org.springframework.stereotype.Service;

/**
* @author MechrevoUser1
* @description 针对表【t_user_image(用户图片表)】的数据库操作Service实现
* @createDate 2026-05-28 15:10:37
*/
@Service
public class UserImageServiceImpl extends ServiceImpl<UserImageMapper, UserImage>
    implements UserImageService{

}




