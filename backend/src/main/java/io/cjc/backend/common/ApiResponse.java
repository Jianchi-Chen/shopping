package io.cjc.backend.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private int code;
    private T result;
    private String message;
    private String type;

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, data, "Success", "success");
    }

    public static <T> ApiResponse<T> error(int code, String message) {
        return new ApiResponse<>(code, null, message, "error");
    }

    public static <T> ApiResponse<T> error(String message) {
        return error(400, message);
    }
}
