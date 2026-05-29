package com.springminio.Controller;

import com.springminio.entity.UserInfo;
import com.springminio.result.R;
import com.springminio.service.UserInfoService;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

// 允许跨域请求，解决前端跨域问题，如：http://localhost:8080 访问 http://localhost:9090
@CrossOrigin
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class UserInfoController {

    private final UserInfoService userInfoService;
    //这是构造函数注入
    private final MinioClient minioClient;

    /**
     * 查询所有用户信息
     *
     * @return 包含用户信息列表的通用响应对象
     */
    @RequestMapping(path = "/users" , method = RequestMethod.GET)
    public R getUserInfos(){

        List<UserInfo> list = userInfoService.list();

        return R.ok(list);
    }

    @RequestMapping(path = "/user/image" , method = RequestMethod.POST)
    public R uploadImage(MultipartFile file) throws Exception {


        String uuid = UUID.randomUUID().toString();
        minioClient.putObject(
                PutObjectArgs.builder()
                        // 【bucket】桶名，相当于 MinIO 里的"文件夹根目录"
                        // 比如 "user-avatar" 存放头像，"user-contract" 存放合同
                        .bucket("images-bucket")

                        // 【object】文件名，文件在 MinIO 里保存的名字
                        // 最终路径会是：bucket/object，比如 images-bucket/xxx.png
                        // 一般用 UUID + 后缀，保证不重名
                        .object(uuid + ".png")

                        // 【filename】本地文件路径（本地磁盘上的文件）
                        // 但这里你用 MultipartFile 就不需要这个参数了
                        // .filename() 和 .stream() 二选一，传一个就行
                        // .filename(file.getOriginalFilename())

                        // 【stream】文件流，用于上传
                        // 第一个参数：InputStream，文件的内容
                        // 第二个参数：文件大小，单位字节
                        // 第三个参数：-1 表示让 MinIO 自己计算分片大小
                        .stream(file.getInputStream(), file.getSize(), -1)

                        // 【contentType】文件的 MIME 类型，比如 "image/png"、"application/pdf"
                        // 决定了浏览器访问这个文件时怎么解析它
                        .contentType(file.getContentType())

                        .build()

        );
        return R.ok("images上传成功");
    }

    @RequestMapping(path = "/user/contract" , method = RequestMethod.POST)
    public R uploadContract(MultipartFile file) throws Exception {

        String uuid = UUID.randomUUID().toString();
        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket("contract-bucket")
                        .object(uuid + ".pdf")
                        .stream(file.getInputStream(), file.getSize(), -1)
                        .contentType(file.getContentType())
                        .build()

        );
        return R.ok("contract上传成功");

    }

}
