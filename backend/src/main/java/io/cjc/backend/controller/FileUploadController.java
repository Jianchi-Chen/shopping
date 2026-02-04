package io.cjc.backend.controller;

import io.cjc.backend.common.ApiResponse;
import io.cjc.backend.service.FileUploadService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/upload")
@RequiredArgsConstructor
public class FileUploadController {
    
    private final FileUploadService fileUploadService;

    @PostMapping
    public ApiResponse<UploadResponse> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("文件不能为空");
        }

        // 检查文件大小（最大 5MB）
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new RuntimeException("文件大小不能超过 5MB");
        }

        // 检查文件类型
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new RuntimeException("只允许上传图片文件");
        }

        String url = fileUploadService.uploadFile(file);
        
        UploadResponse response = new UploadResponse();
        response.setUrl(url);
        response.setFilename(file.getOriginalFilename());
        response.setSize(file.getSize());
        response.setMimeType(contentType);
        
        return ApiResponse.success(response);
    }

    @Data
    public static class UploadResponse {
        private String url;
        private String filename;
        private Long size;
        private String mimeType;
    }
}
