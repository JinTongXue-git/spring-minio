package com.springminio.Controller;

import com.springminio.entity.UserInfo;
import com.springminio.result.R;
import com.springminio.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserInfoController {

    @Autowired
    private UserInfoService userInfoService;

    /**
     * 查询所有用户信息
     *
     * @return 包含用户信息列表的通用响应对象
     */
    @RequestMapping(path = "api/users" , method = RequestMethod.GET)
    public R getUserInfos(){

        List<UserInfo> list = userInfoService.list();

        System.out.println(list.toString());

        return R.ok(list);
    }


}
