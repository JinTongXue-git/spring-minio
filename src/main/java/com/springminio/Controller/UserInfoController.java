package com.springminio.Controller;

import com.springminio.entity.UserInfo;
import com.springminio.entity.UserImage;
import com.springminio.entity.UserContract;
import com.springminio.result.R;
import com.springminio.service.UserInfoService;
import com.springminio.service.UserImageService;
import com.springminio.service.UserContractService;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
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
    private final UserImageService userImageService;
    private final UserContractService userContractService;
    @Value("${minio.endpoint:http://192.168.100.129:9000}")
    private String endpoint;

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


    @RequestMapping(path = "/user/{id}" , method = RequestMethod.GET)
    public R getUserInfo(@PathVariable(name = "id")  Integer id){

        UserInfo userInfo = userInfoService.getById(id);

        return R.ok( "单个用户信息返回成功" , userInfo);
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





    /**
     * 根据用户ID查询头像URL
     *
     * @param id 用户ID
     * @return 包含头像URL的响应对象，未查到返回失败
     */
    @RequestMapping(path = "/user/image/{id}", method = RequestMethod.GET)
    public R getUserImage(@PathVariable(name = "id") Integer id) {
        // 查询用户的头像记录
        UserImage userImage = userImageService.lambdaQuery()
                .eq(UserImage::getUid, id)
                .one();

        if (userImage == null) {
            // 未查到数据，返回失败
            return R.error("未找到该用户的头像信息");
        }

        // 构建MinIO的完整访问URL
        String url = endpoint + "/" + userImage.getBucket() + "/" + userImage.getObject();

        return R.ok(url);
    }

    /**
     * 根据用户ID查询合同URL
     *
     * @param id 用户ID
     * @return 包含合同URL的响应对象，未查到返回失败
     */
    @RequestMapping(path = "/user/contract/{id}", method = RequestMethod.GET)
    public R getUserContract(@PathVariable(name = "id") Integer id) {
        // 查询用户的合同记录
        UserContract userContract = userContractService.lambdaQuery()
                .eq(UserContract::getUid, id)
                .one();

        if (userContract == null) {
            // 未查到数据，返回失败
            return R.error("未找到该用户的合同信息");
        }

        // 构建MinIO的完整访问URL
        String url = endpoint + "/" + userContract.getBucket() + "/" + userContract.getObject();

        return R.ok(url);
    }

}
