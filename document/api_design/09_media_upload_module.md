# DOCUMENTATION THIẾT KẾ API - MODULE 09: MEDIA & FILE UPLOAD SERVICE

> **Domain:** Media & Document Storage Service  
> **Phiên bản API:** v1  
> **Base URL:** `/api/v1/media`  
> **Định dạng dữ liệu:** `multipart/form-data` / `application/json`

---

## 1. API Tải Lên Tệp Đơn (Upload Single File - Image / Video)

- **Domain:** Media Service
- **Function:** Upload 1 tệp ảnh hoặc video minh chứng
- **Description:** Cho phép ứng dụng Khách hàng, Tài xế hoặc Admin tải 1 tệp Ảnh (JPG, PNG, WEBP) hoặc Video ngắn (MP4 <10s) lên máy chủ lưu trữ (Cloud Storage / S3 / Local Storage). Trả về đường dẫn CDN/URL công khai để gắn vào các API nghiệp vụ.
- **URL/API:** `/api/v1/media/upload`
- **Method:** `POST`
- **Authorization:** `Bearer Token` (`CUSTOMER`, `DRIVER`, `OPERATOR`, `ADMIN`)
- **Header:**
  ```http
  Authorization: Bearer <access_token>
  Content-Type: multipart/form-data
  ```
- **RequestParam:** `None`
- **RequestBody:**
  - `file` (binary, required): Tệp tin cần tải lên.
  - `folder` (string, optional, default: 'general'): Thư mục lưu trữ (`incidents`, `warranty`, `bank_proofs`, `products`).
- **Response:**
  - **Success (201 Created):**
    ```json
    {
      "code": 201,
      "message": "Tải tệp lên máy chủ thành công!",
      "data": {
        "file_id": "MED-20260806-001",
        "original_name": "broken_tire_photo.jpg",
        "file_url": "https://storage.gaspro.vn/incidents/2026/08/broken_tire_photo_98765.jpg",
        "file_type": "IMAGE",
        "mime_type": "image/jpeg",
        "file_size_bytes": 1542000,
        "created_at": "2026-08-06T13:50:00Z"
      },
      "paging": null
    }
    ```
  - **Error - Invalid File Format (400 Bad Request):**
    ```json
    {
      "code": 400,
      "message": "Định dạng tệp không được hỗ trợ. Hệ thống chỉ chấp nhận tệp Ảnh (JPG, PNG, WEBP) hoặc Video MP4.",
      "data": {
        "field_errors": [
          {
            "field": "file",
            "error_code": "UNSUPPORTED_MEDIA_TYPE",
            "rejected_value": "application/pdf"
          }
        ]
      }
    }
    ```
  - **Error - File Size Exceeded (400 Bad Request):**
    ```json
    {
      "code": 400,
      "message": "Dung lượng tệp vượt quá giới hạn cho phép (Tối đa 10MB cho Ảnh và 50MB cho Video <10 giây).",
      "data": {
        "field_errors": [
          {
            "field": "file",
            "error_code": "FILE_SIZE_LIMIT_EXCEEDED",
            "rejected_value": "75MB"
          }
        ]
      }
    }
    ```
- **Note:** 
  - API xử lý kiểm tra MIME type thực tế của tệp (Magic Bytes) để tránh lỗ hổng tải file độc hại (.php, .exe).
  - Tự động nén ảnh (Image Optimization) trước khi lưu trữ để tiết kiệm dung lượng.
- **Reference:** BRD v8 (SD-003, CD-003, RC-002, WR-001).

---

## 2. API Tải Lên Nhiều Tệp Cùng Lúc (Upload Multiple Files)

- **Domain:** Media Service
- **Function:** Upload nhiều tệp ảnh/video trong 1 request
- **Description:** Cho phép tải tối đa 5 tệp cùng lúc để phục vụ các luồng gửi nhiều ảnh minh chứng bảo hành hoặc sự cố.
- **URL/API:** `/api/v1/media/upload-multiple`
- **Method:** `POST`
- **Authorization:** `Bearer Token` (`CUSTOMER`, `DRIVER`, `OPERATOR`, `ADMIN`)
- **Header:**
  ```http
  Authorization: Bearer <access_token>
  Content-Type: multipart/form-data
  ```
- **RequestParam:** `None`
- **RequestBody:**
  - `files` (array of binary, required): Danh sách các tệp tin.
  - `folder` (string, optional): Thư mục lưu trữ.
- **Response:**
  - **Success (201 Created):**
    ```json
    {
      "code": 201,
      "message": "Tải lên 2 tệp thành công!",
      "data": [
        {
          "file_id": "MED-20260806-002",
          "file_url": "https://storage.gaspro.vn/warranty/2026/08/bep_goc_trai.jpg",
          "file_type": "IMAGE"
        },
        {
          "file_id": "MED-20260806-003",
          "file_url": "https://storage.gaspro.vn/warranty/2026/08/bep_video_danh_lua.mp4",
          "file_type": "VIDEO"
        }
      ],
      "paging": null
    }
    ```
- **Reference:** `warranty_tickets`, `transit_incidents`.
