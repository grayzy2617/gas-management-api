# DOCUMENTATION THIẾT KẾ API - MODULE 10: DASHBOARD EXECUTIVE, CẢNH BÁO VỎ & BÁO CÁO BI

> **Domain:** Business Intelligence & Executive Dashboard  
> **Phiên bản API:** v1  
> **Base URL:** `/api/v1/admin/dashboard`  
> **Định dạng dữ liệu:** `application/json`

---

## 1. API Lấy Dữ Liệu Dashboard Tổng Quan (Get Executive Dashboard Overview)

- **Domain:** Business Intelligence
- **Function:** Lấy 4 Stat Cards, Cảnh báo đỏ vỏ an toàn, và danh sách khách quá hạn nợ (BI-001, CI-003, CD-004)
- **Description:** Trả về dữ liệu thời gian thực cho màn hình Dashboard Admin (`admin_dashboard.html`) gồm: Doanh thu hôm nay, Số đơn hàng, Số tài xế Online, Tổng công nợ khách hàng, Cảnh báo đỏ tồn kho vỏ rỗng dưới mức an toàn (`safety_threshold <= 10`), và danh sách 5 khách hàng nợ quá hạn 30 ngày.
- **URL/API:** `/api/v1/admin/dashboard/overview`
- **Method:** `GET`
- **Authorization:** `Bearer Token` (`ADMIN`)
- **Header:**
  ```http
  Authorization: Bearer <access_token>
  ```
- **RequestParam:** `None`
- **RequestBody:** `None`
- **Response:**
  - **Success (200 OK):**
    ```json
    {
      "code": 200,
      "message": "Lấy dữ liệu Dashboard tổng quan thành công",
      "data": {
        "stats": {
          "today_revenue": 15800000,
          "today_orders_count": 42,
          "online_drivers_count": 6,
          "total_drivers_count": 8,
          "total_customer_debt": 12500000
        },
        "safety_alerts": [
          {
            "product_id": 1,
            "product_name": "Bình gas Petrolimex 12kg",
            "brand_name": "Petrolimex",
            "current_empty_stock": 9,
            "safety_threshold": 10,
            "status": "ALERT_RED",
            "message": "Vỏ bình PG rỗng còn 9 (Mức an toàn: 10) — Cần nhập thêm vỏ từ NSX!"
          }
        ],
        "overdue_debt_reminders": [
          {
            "customer_id": 45,
            "customer_name": "Nhà hàng Biển Đông",
            "phone": "0912345678",
            "current_debt": 8500000,
            "overdue_days": 35,
            "debt_status": "OVERDUE_LOCKED"
          }
        ]
      },
      "paging": null
    }
    ```
- **Reference:** BRD v8 (BI-001, CI-003, CD-004), UI Prototype `admin_dashboard.html`.

---

## 2. API Biểu Đồ Dòng Tiền Thu Chi 7 Ngày (Get Cashflow Line Chart)

- **Domain:** Business Intelligence
- **Function:** Lấy dữ liệu Chart.js Line chart Thực Thu vs Thực Chi 7 ngày (BI-001)
- **Description:** Trả về chuỗi dữ liệu tổng thu (từ bán gas/thiết bị/thu nợ) và tổng chi (từ nhập kho NSX/lương tài xế) trong 7 ngày gần nhất để vẽ biểu đồ đường dòng tiền.
- **URL/API:** `/api/v1/admin/dashboard/cashflow-chart`
- **Method:** `GET`
- **Authorization:** `Bearer Token` (`ADMIN`)
- **Header:**
  ```http
  Authorization: Bearer <access_token>
  ```
- **RequestParam:**
  - `days` (integer, optional, default: 7): Số ngày thống kê (7 ngày, 30 ngày).
- **RequestBody:** `None`
- **Response:**
  - **Success (200 OK):**
    ```json
    {
      "code": 200,
      "message": "Lấy dữ liệu biểu đồ dòng tiền thành công",
      "data": {
        "labels": ["29/07", "30/07", "31/07", "01/08", "02/08", "03/08", "04/08"],
        "revenue_series": [12000000, 15000000, 11000000, 18000000, 14000000, 16000000, 15800000],
        "expense_series": [8000000, 9000000, 7000000, 12000000, 10000000, 11000000, 9500000]
      },
      "paging": null
    }
    ```
- **Reference:** BRD v8 (BI-001), `orders`, `supplier_payments`, `driver_shifts`.

---

## 3. API Biểu Đồ Top 5 Sản Phẩm Bán Chạy (Get Top Best Selling Chart)

- **Domain:** Business Intelligence
- **Function:** Lấy dữ liệu Chart.js Bar chart Top 5 sản phẩm (BI-002)
- **Description:** Trả về top 5 mặt hàng bán chạy nhất. Cho phép chuyển đổi tiêu chí lọc `by_type = 'QUANTITY'` (Theo số lượng bình) hoặc `by_type = 'REVENUE'` (Theo doanh thu tiền).
- **URL/API:** `/api/v1/admin/dashboard/top-products-chart`
- **Method:** `GET`
- **Authorization:** `Bearer Token` (`ADMIN`)
- **Header:**
  ```http
  Authorization: Bearer <access_token>
  ```
- **RequestParam:**
  - `by_type` (string, optional, default: 'QUANTITY'): Tiêu chí xếp hạng (`QUANTITY` / `REVENUE`).
- **RequestBody:** `None`
- **Response:**
  - **Success (200 OK):**
    ```json
    {
      "code": 200,
      "message": "Lấy biểu đồ sản phẩm bán chạy thành công",
      "data": {
        "by_type": "QUANTITY",
        "labels": ["Petrolimex 12kg", "Petrolimex 50kg", "Van gas Namilux", "Dây gas Petrolimex 1.5m", "Bếp Rinnai RV-365"],
        "values": [145, 67, 45, 38, 23]
      },
      "paging": null
    }
    ```
- **Reference:** BRD v8 (BI-002), `order_items`.

---

## 4. API Gửi Thông Báo Nhắc Nợ Khách Hàng Quá Hạn (Send Debt Reminder Notification)

- **Domain:** Customer Debt
- **Function:** Gửi tin nhắn SMS / Zalo ZNS / Notification nhắc nợ (CD-004)
- **Description:** Cho phép Admin bấm nút "Gửi nhắc nợ" trên bảng nhắc nợ quá hạn. Hệ thống tự động gửi tin nhắn nhắc nợ tới SĐT khách hàng và ghi log lịch sử nhắc nợ.
- **URL/API:** `/api/v1/admin/customers/{id}/send-debt-reminder`
- **Method:** `POST`
- **Authorization:** `Bearer Token` (`ADMIN`)
- **Header:**
  ```http
  Authorization: Bearer <access_token>
  ```
- **RequestParam:** `None`
- **RequestBody:** `None`
- **Response:**
  - **Success (200 OK):**
    ```json
    {
      "code": 200,
      "message": "Đã gửi tin nhắn nhắc nợ thành công tới khách hàng [Nhà hàng Biển Đông — 0912345678]!",
      "data": {
        "customer_id": 45,
        "customer_name": "Nhà hàng Biển Đông",
        "current_debt": 8500000,
        "sent_at": "2026-08-06T13:51:00Z"
      },
      "paging": null
    }
    ```
- **Reference:** BRD v8 (CD-004), `customers`.

---

## 5. API Xuất Báo Cáo Hiệu Suất Tài Xế Bằng Tệp Excel (Export Driver Performance Excel)

- **Domain:** Driver Performance & Reporting
- **Function:** Xuất file Excel báo cáo đơn hàng và lương tài xế (BI-003)
- **Description:** Xuất file `.xlsx` chứa tổng số đơn hoàn thành, tỷ lệ giao thành công, lương tích lũy và tiền phạt vỏ bình của tất cả tài xế trong tháng.
- **URL/API:** `/api/v1/admin/drivers/export-excel`
- **Method:** `GET`
- **Authorization:** `Bearer Token` (`ADMIN`)
- **Header:**
  ```http
  Authorization: Bearer <access_token>
  ```
- **RequestParam:**
  - `month` (string, optional, default: '2026-08'): Tháng xuất báo cáo.
- **RequestBody:** `None`
- **Response:**
  - **Success (200 OK - File Binary Download):**
    - Content-Type: `application/vnd.openxmlformats-officedocument.spreadsheetml.sheet`
    - Content-Disposition: `attachment; filename="Driver_Performance_2026_08.xlsx"`
- **Reference:** BRD v8 (BI-003), `driver_profiles`, `driver_shifts`.
