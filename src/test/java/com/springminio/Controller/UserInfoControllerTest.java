package com.springminio.Controller;

import com.springminio.entity.UserInfo;
import com.springminio.service.UserInfoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class UserInfoControllerTest {

    @Autowired
    private UserInfoService userInfoService;


    /**
     * 测试查询所有用户信息
     *
     */
    @Test
    public void testGetUserInfos (){

        List<UserInfo> list = userInfoService.list();

        System.out.println(list.toString());
    }

}
