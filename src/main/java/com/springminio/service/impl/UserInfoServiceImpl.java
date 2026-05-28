package com.springminio.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.springminio.entity.UserInfo;
import com.springminio.service.UserInfoService;
import com.springminio.mapper.UserInfoMapper;
import org.springframework.stereotype.Service;

/**
* @author MechrevoUser1
* @description 针对表【t_user_info(用户信息表)】的数据库操作Service实现
* @createDate 2026-05-28 15:11:14
*/
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo>
    implements UserInfoService{

}




