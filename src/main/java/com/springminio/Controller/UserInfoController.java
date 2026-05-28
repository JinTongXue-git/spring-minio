package com.springminio.Controller;

import com.springminio.entity.UserInfo;
import com.springminio.result.R;
import com.springminio.service.UserInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// 允许跨域请求，解决前端跨域问题，如：http://localhost:8080 访问 http://localhost:9090
@CrossOrigin
@RequiredArgsConstructor
@RestController
public class UserInfoController {

    private final UserInfoService userInfoService;

    /**
     * 查询所有用户信息
     *
     * @return 包含用户信息列表的通用响应对象
     */
    @RequestMapping(path = "api/users" , method = RequestMethod.GET)
    public R getUserInfos(){

        List<UserInfo> list = userInfoService.list();

        return R.ok(list);
    }


}
