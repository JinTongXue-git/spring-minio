package com.springminio.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.springminio.entity.UserContract;
import com.springminio.service.UserContractService;
import com.springminio.mapper.UserContractMapper;
import org.springframework.stereotype.Service;

/**
* @author MechrevoUser1
* @description 针对表【t_user_contract(用户合同表)】的数据库操作Service实现
* @createDate 2026-05-28 15:11:52
*/
@Service
public class UserContractServiceImpl extends ServiceImpl<UserContractMapper, UserContract>
    implements UserContractService{

}




