package vn.gaspro.api.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Class Wrapper chuẩn hóa định dạng JSON Response trả về cho Frontend
 * @param <T> Kiểu dữ liệu thực tế của trường data (Object, List, String, Integer...)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // Ẩn các trường null trong JSON xuất ra (ví dụ: paging = null)
public class ApiResponse<T> {

    private int code;             // Mã trạng thái/lỗi Business Custom Code (VD: 200, 201, 1001, 4004...)
    private String message;       // Thông báo ngắn gọn bằng tiếng Việt
    private T data;               // Dữ liệu thực tế trả về, có thể null nếu API bị lỗi
    private PagingResponse paging;// Thông tin phân trang, null nếu API không phân trang

    // --- CÁC HELPER METHOD TIỆN ÍCH GIÚP CODE SERVICE NgẮN GỌN HƠN ---

    /**
     * Helper tạo Response thành công không phân trang (HTTP 200 OK)
     */
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .code(200)
                .message("Thành công")
                .data(data)
                .paging(null)
                .build();
    }

//    /**
//     * Helper tạo Response thành công có thông báo tùy chỉnh
//     */
//    public static <T> ApiResponse<T> success(String message, T data) {
//        return ApiResponse.<T>builder()
//                .code(200)
//                .message(message)
//                .data(data)
//                .paging(null)
//                .build();
//    }

    /**
     * Helper tạo Response thành công cho các API lấy danh sách có phân trang
     */
    public static <T> ApiResponse<T> success(T data, PagingResponse paging) {
        return ApiResponse.<T>builder()
                .code(200)
                .message("Lấy danh sách thành công")
                .data(data)
                .paging(paging)
                .build();
    }

    /**
     * Helper tạo Response báo lỗi (Dùng cho GlobalExceptionHandler)
     */
    public static <T> ApiResponse<T> error(int code, String message) {
        return ApiResponse.<T>builder()
                .code(code)
                .message(message)
                .data(null)
                .paging(null)
                .build();
    }
}