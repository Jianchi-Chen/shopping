package io.cjc.backend.controller;

import io.cjc.backend.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@RestController
@RequestMapping("/user/upload")
@RequiredArgsConstructor
public class UploadController {

    @Value("${file.upload.dir:uploads}")
    private String uploadDir;

    @Value("${file.upload.base-url:http://localhost:8080/api/uploads}")
    private String baseUrl;

    @PostMapping(value = "/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<UploadResponse> uploadAvatar(@RequestParam("file") MultipartFile file) {
        // 验证文件
        if (file.isEmpty()) {
            throw new RuntimeException("文件不能为空");
        }

        // 验证大小（1MB）
        if (file.getSize() > 1024 * 1024) {
            throw new RuntimeException("文件大小不能超过1MB");
        }

        // 验证格式
        String contentType = file.getContentType();
        if (contentType == null || (!contentType.equals("image/jpeg") && !contentType.equals("image/png"))) {
            throw new RuntimeException("文件格式必须为jpg或png");
        }

        try {
            // 确保上传目录存在
            File uploadDirFile = new File(uploadDir);
            if (!uploadDirFile.exists()) {
                uploadDirFile.mkdirs();
            }

            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".")
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : ".jpg";
            String filename = "avatar_" + UUID.randomUUID() + extension;

            // 保存文件
            Path filePath = Paths.get(uploadDir, filename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // 返回URL
            String url = baseUrl + "/" + filename;
            return ApiResponse.success(new UploadResponse(url));

        } catch (IOException e) {
            throw new RuntimeException("文件上传失败: " + e.getMessage());
        }
    }

    public static class UploadResponse {
        private String url;

        public UploadResponse(String url) {
            this.url = url;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
