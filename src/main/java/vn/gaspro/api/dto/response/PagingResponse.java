package vn.gaspro.api.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO đại diện cho thông tin phân trang (Paging Metadata)
 * Trả về trong khung JSON response chuẩn của hệ thống
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagingResponse {

    private int page;         // Trang hiện tại (1-indexed)
    private int limit;        // Số lượng phần tử trên 1 trang
    private long totalItems;  // Tổng số lượng phần tử trong DB
    private int totalPages;   // Tổng số trang (Tính bằng Math.ceil(totalItems / limit))
}