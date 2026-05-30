package com.springminio.miniotest;

import io.minio.*;
import io.minio.http.Method;
import io.minio.messages.Bucket;
import io.minio.messages.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * MinIO API 演示测试类
 * 项目类型：Spring Boot 项目
 * MinIO 服务端版本：RELEASE.2025-04-22T22-12-26Z
 * Java SDK 版本：io.minio:minio:8.5.10
 * 
 * 测试说明：
 * - 所有 API 演示代码使用 JUnit 5 的 @Test 注解，可直接运行
 * - 每个方法都是独立测试，可以单独运行验证
 * - 统一使用 BUCKET_NAME 和 OBJECT_NAME 常量，避免硬编码
 */
@SpringBootTest
public class MinioTest {

    // 注入 MinioClient Bean，由 MinioConfig 配置类提供
    @Autowired
    private MinioClient minioClient;

    // 统一变量定义，避免硬编码，方便统一修改测试目标
    private static final String BUCKET_NAME = "test-bucket";
    private static final String OBJECT_NAME = "banner.txt";

    // ============================================
    // 第一部分：Bucket（桶）操作 API 演示
    // ============================================

    /**
     * 1. 创建桶
     * 
     * <p>功能说明：在 MinIO 服务器上创建一个新桶（Bucket）</p>
     * 
     * <p>API 方法：makeBucket(MakeBucketArgs args)</p>
     * <ul>
     *   <li>建造者模式：MakeBucketArgs.builder().bucket("桶名").build()</li>
     *   <li>可选参数：region（区域）、objectLock（对象锁定）</li>
     * </ul>
     * 
     * <p>演示流程：</p>
     * <ol>
     *   <li>先使用 bucketExists() 检查桶是否已存在</li>
     *   <li>如果不存在，则调用 makeBucket() 创建</li>
     *   <li>如果已存在，则输出提示信息</li>
     * </ol>
     * 
     * <p>注意事项：</p>
     * <ul>
     *   <li>桶名必须是全局唯一的（在整个 MinIO 集群中）</li>
     *   <li>桶名只能包含小写字母、数字、中划线和点号</li>
     *   <li>桶名长度必须在 3-63 个字符之间</li>
     *   <li>重复创建会抛出 BucketExists 异常</li>
     * </ul>
     * 
     * @throws Exception MinIO 操作异常（网络错误、权限不足等）
     */
    @Test
    public void testCreateBucket() {
        try {
            System.out.println("\n========== 测试：创建桶 ==========");
            System.out.println("目标桶名: " + BUCKET_NAME);
            
            // 第一步：检查桶是否已存在
            // bucketExists() 返回布尔值，true 表示桶存在，false 表示不存在
            boolean exists = minioClient.bucketExists(
                    BucketExistsArgs.builder()
                            .bucket(BUCKET_NAME)  // 指定要检查的桶名
                            .build()
            );

            if (!exists) {
                // 第二步：桶不存在，创建新桶
                // makeBucket() 方法：根据 MakeBucketArgs 参数创建桶
                minioClient.makeBucket(
                        MakeBucketArgs.builder()
                                .bucket(BUCKET_NAME)  // 必填：指定要创建的桶名
                                // .region("us-east-1")  // 可选：指定区域，默认使用 MinIO 配置的区域
                                // .objectLock(true)     // 可选：启用对象锁定功能
                                .build()
                );
                System.out.println("✅ 桶创建成功: " + BUCKET_NAME);
            } else {
                System.out.println("⚠️  桶已存在，无需创建: " + BUCKET_NAME);
            }
        } catch (Exception e) {
            System.err.println("❌ 创建桶失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 2. 检查桶是否存在
     * 
     * <p>功能说明：判断指定的桶是否已经存在于 MinIO 服务器中</p>
     * 
     * <p>API 方法：bucketExists(BucketExistsArgs args)</p>
     * <ul>
     *   <li>返回类型：boolean（true=存在，false=不存在）</li>
     *   <li>建造者模式：BucketExistsArgs.builder().bucket("桶名").build()</li>
     * </ul>
     * 
     * <p>使用场景：</p>
     * <ul>
     *   <li>创建桶前先检查，避免重复创建异常</li>
     *   <li>上传文件前验证桶是否存在</li>
     *   <li>删除桶前确认桶存在</li>
     * </ul>
     * 
     * @throws Exception MinIO 操作异常（网络错误、认证失败等）
     */
    @Test
    public void testBucketExists() {
        try {
            System.out.println("\n========== 测试：检查桶是否存在 ==========");
            System.out.println("检查桶名: " + BUCKET_NAME);
            
            // 调用 bucketExists() 方法，传入桶名参数
            // 返回值：boolean 类型，true 表示桶存在，false 表示不存在
            boolean exists = minioClient.bucketExists(
                    BucketExistsArgs.builder()
                            .bucket(BUCKET_NAME)  // 指定要检查的桶名
                            .build()
            );
            
            // 输出检查结果
            System.out.println("✅ 检查结果: 桶 [" + BUCKET_NAME + "] " + (exists ? "存在" : "不存在"));
        } catch (Exception e) {
            System.err.println("❌ 检查桶是否存在失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 3. 列出所有桶
     * 
     * <p>功能说明：获取 MinIO 服务器上的所有桶列表</p>
     * 
     * <p>API 方法：listBuckets()</p>
     * <ul>
     *   <li>返回类型：Iterable&lt;Bucket&gt;（可迭代的桶集合）</li>
     *   <li>不需要参数，直接调用即可</li>
     *   <li>返回的 Bucket 对象包含：name（桶名）、creationDate（创建时间）、region（区域）</li>
     * </ul>
     * 
     * <p>使用场景：</p>
     * <ul>
     *   <li>查看当前 MinIO 服务中有多少个桶</li>
     *   <li>遍历所有桶进行批量操作</li>
     *   <li>监控和统计桶的数量</li>
     * </ul>
     * 
     * @throws Exception MinIO 操作异常（网络错误、认证失败等）
     */
    @Test
    public void testListBuckets() {
        try {
            System.out.println("\n========== 测试：列出所有桶 ==========");
            System.out.println("开始获取桶列表...");
            
            // 调用 listBuckets() 方法，返回 List<Bucket> 列表
            // 每个 Bucket 对象包含桶的信息
            List<Bucket> buckets = minioClient.listBuckets();
            
            // 遍历所有桶并输出信息
            int count = 0;
            for (Bucket bucket : buckets) {
                count++;
                System.out.println("桶 #" + count + ":");
                // 直接打印 bucket 对象，查看其内容
                System.out.println("  - 桶信息: " + bucket.toString());
            }
            
            if (count == 0) {
                System.out.println("⚠️  当前 MinIO 服务中没有任何桶");
            } else {
                System.out.println("\n✅ 总共找到 " + count + " 个桶");
            }
        } catch (Exception e) {
            System.err.println("❌ 列出桶失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 4. 设置桶策略（公开读权限）
     * 
     * <p>功能说明：为桶设置访问策略（Policy），控制谁可以访问桶内的对象</p>
     * 
     * <p>API 方法：setBucketPolicy(SetBucketPolicyArgs args)</p>
     * <ul>
     *   <li>建造者模式：SetBucketPolicyArgs.builder().bucket("桶名").config(policyJson).build()</li>
     *   <li>注意：使用 .config() 而不是 .policy()</li>
     *   <li>策略格式：JSON 字符串，符合 AWS S3 策略语法</li>
     * </ul>
     * 
     * <p>策略 JSON 结构说明：</p>
     * <ul>
     *   <li>Version: 策略版本，固定为 "2012-10-17"</li>
     *   <li>Statement: 策略声明数组，每个声明包含：</li>
     *   <ul>
     *     <li>Effect: 效果，"Allow"（允许）或 "Deny"（拒绝）</li>
     *     <li>Principal: 主体，"*" 表示所有用户</li>
     *     <li>Action: 允许的操作，如 ["s3:GetObject"] 表示读取对象</li>
     *     <li>Resource: 资源路径，如 "arn:aws:s3:::桶名/*"</li>
     *   </ul>
     * </ul>
     * 
     * <p>常用策略示例：</p>
     * <ul>
     *   <li>公开读：允许所有人读取桶内对象</li>
     *   <li>公开读写：允许所有人读取和写入（谨慎使用）</li>
     *   <li>私有：拒绝所有未授权访问（默认）</li>
     * </ul>
     * 
     * @throws Exception MinIO 操作异常（策略格式错误、权限不足等）
     */
    @Test
    public void testSetBucketPolicy() {
        try {
            System.out.println("\n========== 测试：设置桶策略 ==========");
            System.out.println("目标桶名: " + BUCKET_NAME);
            
            // 定义公开读权限的桶策略 JSON
            // 这个策略允许所有人（Principal: "*"）读取（Action: "s3:GetObject"）桶内的所有对象
            String policyJson = "{" +
                    "\"Version\": \"2012-10-17\"," +
                    "\"Statement\": [" +
                    "  {" +
                    "    \"Effect\": \"Allow\"," +        // 允许操作
                    "    \"Principal\": \"*\"," +         // 所有用户（公开访问）
                    "    \"Action\": [\"s3:GetObject\"]," +  // 允许的操作：读取对象
                    "    \"Resource\": [\"arn:aws:s3:::" + BUCKET_NAME + "/*\"]" +  // 资源路径：桶内所有对象
                    "  }" +
                    "]}"
            ;
            
            System.out.println("设置策略: 公开读权限");
            
            // 调用 setBucketPolicy() 方法设置桶策略
            // 注意：使用 .config() 方法传入策略 JSON 字符串
            minioClient.setBucketPolicy(
                    SetBucketPolicyArgs.builder()
                            .bucket(BUCKET_NAME)  // 指定要设置策略的桶名
                            .config(policyJson)   // 策略 JSON 配置（不是 .policy()）
                            .build()
            );
            
            System.out.println("✅ 桶策略设置成功: " + BUCKET_NAME);
            System.out.println("现在可以通过公开 URL 访问桶内对象");
        } catch (Exception e) {
            System.err.println("❌ 设置桶策略失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 5. 删除桶
     * 
     * <p>功能说明：删除 MinIO 服务器上的指定桶</p>
     * 
     * <p>API 方法：removeBucket(RemoveBucketArgs args)</p>
     * <ul>
     *   <li>建造者模式：RemoveBucketArgs.builder().bucket("桶名").build()</li>
     *   <li>前提条件：桶必须为空（没有任何对象）</li>
     * </ul>
     * 
     * <p>注意事项：</p>
     * <ul>
     *   <li>桶内必须没有任何对象，否则会抛出异常</li>
     *   <li>删除前建议先调用 listObjects() 确认桶为空</li>
     *   <li>可以使用 removeObject() 先删除所有对象，再删除桶</li>
     *   <li>删除操作不可逆，请谨慎使用</li>
     * </ul>
     * 
     * @throws Exception MinIO 操作异常（桶非空、桶不存在等）
     */
    @Test
    public void testRemoveBucket() {
        try {
            System.out.println("\n========== 测试：删除桶 ==========");
            System.out.println("目标桶名: " + BUCKET_NAME);
            System.out.println("️  注意：桶必须为空才能删除");
            
            // 调用 removeBucket() 方法删除桶
            // 如果桶内有对象，会抛出异常："The bucket you tried to delete is not empty"
            minioClient.removeBucket(
                    RemoveBucketArgs.builder()
                            .bucket(BUCKET_NAME)  // 指定要删除的桶名
                            .build()
            );
            
            System.out.println("✅ 桶删除成功: " + BUCKET_NAME);
        } catch (Exception e) {
            System.err.println("❌ 删除桶失败");
            System.err.println("可能原因：");
            System.err.println("  1. 桶内还有对象，需要先删除所有对象");
            System.err.println("  2. 桶不存在");
            System.err.println("错误信息: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ============================================
    // 第二部分：Object（对象）操作 API 演示
    // ============================================

    /**
     * 1. 上传文件（流式上传）
     * 
     * <p>功能说明：将本地文件通过流式方式上传到 MinIO 桶中</p>
     * 
     * <p>API 方法：putObject(PutObjectArgs args)</p>
     * <ul>
     *   <li>建造者模式：PutObjectArgs.builder().bucket("桶名").object("对象名").stream(...).build()</li>
     *   <li>支持上传任意类型的文件（文本、图片、视频、压缩包等）</li>
     * </ul>
     * 
     * <p>关键参数说明：</p>
     * <ul>
     *   <li><b>bucket</b>: 目标桶名，文件将存储在这个桶中</li>
     *   <li><b>object</b>: 对象名（文件名），在桶中的唯一标识</li>
     *   <li><b>stream</b>: 文件流，包含三个参数：</li>
     *   <ul>
     *     <li>inputStream: 输入流，文件的内容</li>
     *     <li>size: 文件大小（字节），用于 MinIO 分片上传计算</li>
     *     <li>partSize: 分片大小，-1 表示让 MinIO 自动计算</li>
     *   </ul>
     *   <li><b>contentType</b>: 文件的 MIME 类型，决定浏览器如何解析文件</li>
     *   <li>常用类型：text/plain、image/png、application/pdf、application/octet-stream</li>
     * </ul>
     * 
     * <p>注意事项：</p>
     * <ul>
     *   <li>上传后必须关闭 inputStream，避免资源泄漏</li>
     *   <li>如果对象已存在，会被覆盖（建议上传前检查或使用 UUID 命名）</li>
     *   <li>大文件会自动分片上传，无需手动处理</li>
     * </ul>
     * 
     * @throws Exception MinIO 操作异常（网络错误、权限不足、文件不存在等）
     */
    @Test
    public void testUploadObject() {
        try {
            System.out.println("\n========== 测试：上传文件 ==========");
            System.out.println("目标桶名: " + BUCKET_NAME);
            System.out.println("对象名: " + OBJECT_NAME);
            
            // 第一步：从 classpath 读取文件（Spring Boot 项目的 resources 目录）
            // getResourceAsStream() 返回 InputStream，用于读取文件内容
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(OBJECT_NAME);
            
            // 检查文件是否存在
            if (inputStream == null) {
                System.err.println("❌ 文件不存在: " + OBJECT_NAME);
                System.err.println("请确保文件在 src/main/resources 或 src/test/resources 目录下");
                return;
            }

            // 第二步：获取文件大小（字节数）
            // available() 返回可以无阻塞读取的字节数
            // 注意：对于某些流，available() 可能返回 0，建议实际项目中使用 Files.size()
            long size = inputStream.available();
            System.out.println("文件大小: " + size + " 字节");

            // 第三步：上传文件到 MinIO
            // putObject() 方法：根据 PutObjectArgs 参数上传文件流
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(BUCKET_NAME)              // 必填：目标桶名
                            .object(OBJECT_NAME)              // 必填：对象名（文件在桶中的名称）
                            .stream(inputStream, size, -1)    // 必填：文件流 + 大小 + 分片大小（-1自动计算）
                            .contentType("text/plain")        // 可选：文件 MIME 类型，影响浏览器解析方式
                            // .contentType("image/png")      // 如果是图片，用这个
                            // .contentType("application/pdf") // 如果是PDF，用这个
                            .build()
            );

            System.out.println("✅ 文件上传成功: " + OBJECT_NAME);
            System.out.println("访问路径: " + BUCKET_NAME + "/" + OBJECT_NAME);
            
            // 第四步：关闭输入流，释放资源
            // 重要：必须关闭流，否则会导致资源泄漏
            inputStream.close();
        } catch (Exception e) {
            System.err.println("❌ 文件上传失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 2. 下载文件（获取对象流）
     * 
     * <p>功能说明：从 MinIO 桶中下载指定对象，获取输入流进行读取或保存</p>
     * 
     * <p>API 方法：getObject(GetObjectArgs args)</p>
     * <ul>
     *   <li>返回类型：InputStream（输入流），可以直接读取文件内容或保存到本地</li>
     *   <li>建造者模式：GetObjectArgs.builder().bucket("桶名").object("对象名").build()</li>
     * </ul>
     * 
     * <p>使用方式：</p>
     * <ul>
     *   <li>读取文本文件：使用 BufferedReader 或 Scanner 逐行读取</li>
     *   <li>读取二进制文件：使用字节数组读取，保存到本地文件</li>
     *   <li>返回给前端：直接将 InputStream 写入 HttpServletResponse</li>
     * </ul>
     * 
     * <p>注意事项：</p>
     * <ul>
     *   <li>必须关闭 InputStream，避免资源泄漏</li>
     *   <li>建议使用 try-with-resources 语法自动关闭流</li>
     *   <li>对于大文件，建议分块读取，避免内存溢出</li>
     * </ul>
     * 
     * @throws Exception MinIO 操作异常（对象不存在、网络错误等）
     */
    @Test
    public void testDownloadObject() {
        try {
            System.out.println("\n========== 测试：下载文件 ==========");
            System.out.println("目标桶名: " + BUCKET_NAME);
            System.out.println("对象名: " + OBJECT_NAME);
            
            // 第一步：调用 getObject() 方法获取对象流
            // 返回 InputStream，可以像读取本地文件一样读取 MinIO 中的对象
            // 底层会自动处理网络连接、分片下载等细节
            InputStream inputStream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(BUCKET_NAME)    // 必填：桶名
                            .object(OBJECT_NAME)    // 必填：对象名
                            // .offset(0)            // 可选：从指定字节位置开始读取（用于断点续传）
                            // .length(1024)         // 可选：读取指定长度的字节数
                            .build()
            );
            
            System.out.println("✅ 成功获取对象流");
            System.out.println("\n========== 文件内容 ==========");
            
            // 第二步：读取流内容（示例：读取文本文件）
            // 使用字节数组逐块读取，适用于任意类型的文件
            byte[] buffer = new byte[1024];  // 缓冲区大小 1KB
            int bytesRead;                    // 实际读取的字节数
            StringBuilder content = new StringBuilder();
            
            // 循环读取直到文件末尾（read() 返回 -1 表示结束）
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                // 将读取的字节转换为字符串并追加
                content.append(new String(buffer, 0, bytesRead));
            }
            
            // 输出文件内容
            System.out.println(content.toString());
            System.out.println("\n========== 下载完成 ==========");
            
            // 第三步：关闭输入流，释放资源
            // 重要：必须关闭流，否则会导致连接泄漏
            inputStream.close();
        } catch (Exception e) {
            System.err.println("❌ 文件下载失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 3. 获取预签名 URL（临时访问链接）
     * 
     * <p>功能说明：生成一个临时的文件访问链接，无需认证即可访问私有桶中的对象</p>
     * 
     * <p>API 方法：getPresignedObjectUrl(GetPresignedObjectUrlArgs args)</p>
     * <ul>
     *   <li>返回类型：String（URL 地址字符串）</li>
     *   <li>建造者模式：GetPresignedObjectUrlArgs.builder().bucket().object().method().expiry().build()</li>
     * </ul>
     * 
     * <p>关键参数说明：</p>
     * <ul>
     *   <li><b>method</b>: HTTP 请求方法</li>
     *   <ul>
     *     <li>Method.GET: 下载/查看文件（最常用）</li>
     *     <li>Method.PUT: 上传文件</li>
     *     <li>Method.DELETE: 删除文件</li>
     *     <li>Method.POST: 表单上传</li>
     *   </ul>
     *   <li><b>expiry</b>: URL 有效期（过期后链接失效）</li>
     *   <ul>
     *     <li>可以设置秒、分钟、小时、天</li>
     *     <li>示例：expiry(7, TimeUnit.DAYS) 表示 7 天有效</li>
     *     <li>建议：公开分享设置较短时间，内部使用可设置较长时间</li>
     *   </ul>
     * </ul>
     * 
     * <p>使用场景：</p>
     * <ul>
     *   <li>临时分享文件给朋友（设置 1 天或 7 天有效期）</li>
     *   <li>前端直接上传文件（生成 PUT 方法的预签名 URL）</li>
     *   <li>下载大文件时提供直接下载链接</li>
     * </ul>
     * 
     * <p>注意事项：</p>
     * <ul>
     *   <li>URL 过期后将无法访问，需要重新生成</li>
     *   <li>即使桶是私有的，预签名 URL 也能临时访问</li>
     *   <li>URL 中包含签名参数，不可篡改</li>
     * </ul>
     * 
     * @throws Exception MinIO 操作异常（对象不存在、参数错误等）
     */
    @Test
    public void testGetPresignedUrl() {
        try {
            System.out.println("\n========== 测试：生成预签名 URL ==========");
            System.out.println("目标桶名: " + BUCKET_NAME);
            System.out.println("对象名: " + OBJECT_NAME);
            System.out.println("有效期限: 7 天");
            System.out.println("请求方法: GET（下载）");
            
            // 调用 getPresignedObjectUrl() 方法生成临时访问链接
            // 返回的 URL 包含签名参数，有效期内可直接访问
            String presignedUrl = minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .bucket(BUCKET_NAME)              // 必填：桶名
                            .object(OBJECT_NAME)              // 必填：对象名
                            .method(Method.GET)               // 必填：HTTP 方法（GET=下载，PUT=上传，DELETE=删除）
                            .expiry(7, TimeUnit.DAYS)         // 必填：URL 有效期（7天）
                            // .expiry(1, TimeUnit.HOURS)     // 也可以设置为 1 小时
                            // .expiry(30, TimeUnit.MINUTES)  // 或 30 分钟
                            .build()
            );
            
            System.out.println("\n✅ 预签名 URL 生成成功:");
            System.out.println(presignedUrl);
            System.out.println("\n 说明：");
            System.out.println("  - 复制上面的 URL 到浏览器即可直接访问文件");
            System.out.println("  - 7 天后此链接将失效，需要重新生成");
        } catch (Exception e) {
            System.err.println("❌ 生成预签名 URL 失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 4. 检查对象元数据（Stat Object）
     * 
     * <p>功能说明：获取指定对象的元数据信息（大小、类型、修改时间等），不下载文件内容</p>
     * 
     * <p>API 方法：statObject(StatObjectArgs args)</p>
     * <ul>
     *   <li>返回类型：StatObjectResponse（对象元数据响应）</li>
     *   <li>建造者模式：StatObjectArgs.builder().bucket("桶名").object("对象名").build()</li>
     * </ul>
     * 
     * <p>返回的元数据信息：</p>
     * <ul>
     *   <li><b>object()</b>: 对象名称（String）</li>
     *   <li><b>size()</b>: 文件大小（long，单位：字节）</li>
     *   <li><b>contentType()</b>: 文件 MIME 类型（String），如 "text/plain"</li>
     *   <li><b>lastModified()</b>: 最后修改时间（ZonedDateTime）</li>
     *   <li><b>etag()</b>: 文件的 MD5 校验和（String），用于验证文件完整性</li>
     * </ul>
     * 
     * <p>使用场景：</p>
     * <ul>
     *   <li>检查对象是否存在（不存在会抛出异常）</li>
     *   <li>获取文件大小，用于进度条显示</li>
     *   <li>验证文件是否被修改过（通过 etag 或 lastModified）</li>
     *   <li>获取文件类型，决定前端如何展示</li>
     * </ul>
     * 
     * @throws Exception MinIO 操作异常（对象不存在、网络错误等）
     */
    @Test
    public void testStatObject() {
        try {
            System.out.println("\n========== 测试：检查对象元数据 ==========");
            System.out.println("目标桶名: " + BUCKET_NAME);
            System.out.println("对象名: " + OBJECT_NAME);
            
            // 调用 statObject() 方法获取对象元数据
            // 返回 StatObjectResponse 对象，包含文件的各种元信息
            StatObjectResponse stat = minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(BUCKET_NAME)    // 必填：桶名
                            .object(OBJECT_NAME)    // 必填：对象名
                            .build()
            );
            
            System.out.println("\n✅ 对象元数据获取成功:");
            System.out.println("========== 元数据详情 ==========");
            System.out.println("对象名: " + stat.object());                      // 对象名称
            System.out.println("文件大小: " + stat.size() + " 字节");            // 文件大小（字节）
            System.out.println("文件类型: " + stat.contentType());               // MIME 类型
            System.out.println("最后修改: " + stat.lastModified());              // 最后修改时间
            System.out.println("ETag: " + stat.etag());                          // MD5 校验和
            
            // 文件大小格式化（可选）
            long size = stat.size();
            if (size < 1024) {
                System.out.println("大小（格式化）: " + size + " B");
            } else if (size < 1024 * 1024) {
                System.out.println("大小（格式化）: " + String.format("%.2f KB", size / 1024.0));
            } else {
                System.out.println("大小（格式化）: " + String.format("%.2f MB", size / (1024.0 * 1024)));
            }
        } catch (Exception e) {
            System.err.println("❌ 获取对象元数据失败: " + e.getMessage());
            System.err.println("可能原因：对象不存在、桶不存在、权限不足");
            e.printStackTrace();
        }
    }

    /**
     * 5. 列出桶内所有对象
     * 
     * <p>功能说明：获取指定桶内的所有对象列表（类似 ls 命令）</p>
     * 
     * <p>API 方法：listObjects(ListObjectsArgs args)</p>
     * <ul>
     *   <li>返回类型：Iterable&lt;Result&lt;Item&gt;&gt;（可迭代的结果集合）</li>
     *   <li>建造者模式：ListObjectsArgs.builder().bucket("桶名").recursive(true).build()</li>
     * </ul>
     * 
     * <p>关键参数说明：</p>
     * <ul>
     *   <li><b>bucket</b>: 桶名，指定要列出对象的桶</li>
     *   <li><b>recursive</b>: 是否递归列出（true=列出所有子目录，false=只列出根目录）</li>
     *   <li><b>prefix</b>: 可选，前缀过滤（如 "images/" 只列出 images 目录下的文件）</li>
     *   <li><b>startAfter</b>: 可选，从指定对象之后开始列出（用于分页）</li>
     *   <li><b>maxKeys</b>: 可选，最大返回数量（默认 1000）</li>
     * </ul>
     * 
     * <p>返回的 Item 对象信息：</p>
     * <ul>
     *   <li>objectName(): 对象完整路径（String）</li>
     *   <li>size(): 对象大小（long，单位：字节）</li>
     *   <li>lastModified(): 最后修改时间（ZonedDateTime）</li>
     *   <li>etag(): MD5 校验和（String）</li>
     *   <li>isDir(): 是否为目录（boolean）</li>
     * </ul>
     * 
     * <p>注意事项：</p>
     * <ul>
     *   <li>返回的是 Iterable&lt;Result&lt;Item&gt;&gt;，需要先 .get() 获取 Item</li>
     *   <li>Result 对象可能包含异常，需要检查 result.get() 是否成功</li>
     * </ul>
     * 
     * @throws Exception MinIO 操作异常（桶不存在、网络错误等）
     */
    @Test
    public void testListObjects() {
        try {
            System.out.println("\n========== 测试：列出桶内所有对象 ==========");
            System.out.println("目标桶名: " + BUCKET_NAME);
            System.out.println("递归列出: true（包含所有子目录）");
            
            // 调用 listObjects() 方法列出桶内所有对象
            // 返回 Iterable<Result<Item>>，需要迭代获取每个 Item
            Iterable<Result<Item>> results = minioClient.listObjects(
                    ListObjectsArgs.builder()
                            .bucket(BUCKET_NAME)    // 必填：桶名
                            .recursive(true)        // 必填：true=递归列出所有子目录，false=只列出根目录
                            // .prefix("images/")   // 可选：前缀过滤，只列出特定目录
                            // .maxKeys(100)        // 可选：最大返回数量
                            .build()
            );
            
            // 遍历结果集
            int count = 0;
            for (Result<Item> result : results) {
                try {
                    // result.get() 获取 Item 对象
                    // 如果获取失败会抛出异常，需要 try-catch
                    Item item = result.get();
                    
                    count++;
                    System.out.println("\n对象 #" + count + ":");
                    System.out.println("  - 对象名: " + item.objectName());     // 对象完整路径
                    System.out.println("  - 大小: " + item.size() + " 字节");   // 文件大小
                    System.out.println("  - 修改时间: " + item.lastModified()); // 修改时间
                    
                    // 判断是否为目录（MinIO 是对象存储，目录是虚拟的）
                    if (item.isDir()) {
                        System.out.println("  - 类型: 目录");
                    } else {
                        System.out.println("  - 类型: 文件");
                    }
                } catch (Exception e) {
                    System.err.println("读取对象信息失败: " + e.getMessage());
                }
            }
            
            if (count == 0) {
                System.out.println("\n⚠️  桶内没有任何对象");
            } else {
                System.out.println("\n✅ 总共找到 " + count + " 个对象");
            }
        } catch (Exception e) {
            System.err.println("❌ 列出对象失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 6. 删除对象
     * 
     * <p>功能说明：从 MinIO 桶中删除指定对象</p>
     * 
     * <p>API 方法：removeObject(RemoveObjectArgs args)</p>
     * <ul>
     *   <li>建造者模式：RemoveObjectArgs.builder().bucket("桶名").object("对象名").build()</li>
     * </ul>
     * 
     * <p>注意事项：</p>
     * <ul>
     *   <li>删除操作不可逆，删除后无法恢复</li>
     *   <li>删除不存在的对象不会抛出异常（静默失败）</li>
     *   <li>建议删除前先用 statObject() 确认对象存在</li>
     *   <li>可以批量删除：removeObjects(List&lt;DeleteObject&gt;)</li>
     * </ul>
     * 
     * @throws Exception MinIO 操作异常（桶不存在、权限不足等）
     */
    @Test
    public void testRemoveObject() {
        try {
            System.out.println("\n========== 测试：删除对象 ==========");
            System.out.println("目标桶名: " + BUCKET_NAME);
            System.out.println("对象名: " + OBJECT_NAME);
            System.out.println("⚠️  警告：删除操作不可逆！");
            
            // 调用 removeObject() 方法删除对象
            // 如果对象不存在，不会抛出异常（静默失败）
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(BUCKET_NAME)    // 必填：桶名
                            .object(OBJECT_NAME)    // 必填：对象名
                            .build()
            );
            
            System.out.println("\n✅ 对象删除成功: " + OBJECT_NAME);
            System.out.println("💡 提示：如果对象本来就不存在，也会显示删除成功");
        } catch (Exception e) {
            System.err.println("❌ 删除对象失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
