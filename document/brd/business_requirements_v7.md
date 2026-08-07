# TÀI LIỆU YÊU CẦU NGHIỆP VỤ (BUSINESS REQUIREMENTS DOCUMENT - BRD)
## DỰ ÁN: HỆ THỐNG QUẢN LÝ BÁN GAS & THIẾT BỊ HỘ KINH DOANH
**Vai trò phân tích:** Senior Business Analyst (dựa trên quy chuẩn [business-analyst.md](file:///d:/6rd_semester/gas_management/rule_agent/business-analyst.md))  
**Phiên bản:** 7.0 (Aligned Scope & Finalized Operational Workflows)  
**Ngày lập:** 02-08-2026

---

## 1. TỔNG QUAN DỰ ÁN & MỤC TIÊU CHIẾN LƯỢC
Hệ thống được xây dựng nhằm chuyển đổi số mô hình vận hành của một đại lý gas lớn (nhập hàng trực tiếp từ nhà sản xuất và phân phối tới hộ gia đình B2C, nhà hàng/xí nghiệp B2B).
* **Mục tiêu cốt lõi:**
  * Minh bạch hóa dòng tiền thực tế thông qua việc đối chiếu tự động đơn hàng của tài xế cuối ngày.
  * Tối ưu hóa vận hành giao hàng bằng thuật toán "giật đơn" tự động (phương án C) kết hợp tự làm mới thủ công (Pull-to-refresh).
  * Quản lý chặt chẽ công nợ (khách hàng lẻ/sỉ và công nợ với nhà sản xuất).
  * Kiểm soát vòng đời vỏ bình gas và tiền đặt cọc vỏ bình của khách hàng.
  * Xây dựng giao diện đặt hàng tiện lợi cho khách hàng cuối (tương tự Shopee).
  * Xây dựng luồng sửa chữa bảo hành chặt chẽ thông qua xác nhận đa bên (Tài xế -> Operator -> Khách hàng).

---

## 2. PHÂN QUYỀN VAI TRÒ TRÊN HỆ THỐNG (STAKEHOLDERS & ROLES)

Hệ thống phân chia thành 4 nhóm đối tượng người dùng chính với giao diện và quyền truy cập độc lập rõ ràng (chủ ra chủ, nhân viên ra nhân viên):

| Vai trò | Giao diện | Quyền hạn & Chức năng chính |
| :--- | :--- | :--- |
| **Admin (Chủ đại lý)** | **Web Admin Portal (Desktop)** | Chỉ dành riêng cho Chủ đại lý. Toàn quyền cấu hình các tham số hệ thống (thời gian phạt, hạn mức nợ, ngưỡng cảnh báo kho). Xem báo cáo doanh thu tài chính, dòng tiền thực thu/thực chi, giá vốn nhập hàng, quản lý nhân viên, quản lý nợ với nhà sản xuất. |
| **Nhân viên tổng đài (Operator)** | **Web Operator Portal (Desktop)** | Đóng vai trò trung gian điều phối giữa Khách hàng và Tài xế. Tiếp nhận cuộc gọi Hotline lên đơn hộ khách; duyệt đơn hủy khi khách yêu cầu; duyệt đề xuất báo giá linh kiện bảo hành của tài xế; can thiệp gán đơn thủ công cho tài xế đang trực tuyến khi có khiếu nại trôi đơn quá lâu. Không xem được báo cáo tài chính, doanh thu tổng và giá vốn nhập hàng của đại lý. |
| **Tài xế giao hàng** | **App Mobile Driver** | Xem danh sách chợ đơn, bấm giật đơn (hoặc tải lại thủ công danh sách đơn); nhận đơn đổi trả/bảo hành; thối tiền lẻ cho khách; trực tiếp sửa chữa bảo hành tại nhà khách; gửi đề xuất thay thế linh kiện; quyết toán ca cuối ngày. |
| **Khách hàng cuối** | **Web/App Customer (B2C)** | Đăng ký tài khoản, xem danh mục, đặt hàng trực tuyến; theo dõi trạng thái đơn hàng tĩnh (Đang giao, Hoàn thành, Đã hủy - không có Live Tracking định vị tài xế); duyệt đề xuất và chi phí linh kiện bảo hành trên App của mình. |

---

## 3. QUY TRÌNH NGHIỆP VỤ CHI TIẾT & CÁC TRƯỜNG HỢP NGOẠI LỆ (EDGE CASES)

### Quy trình 3.1: Đặt hàng & Phân công Giao nhận (Smart Dispatching)
* **Quy trình đặt đơn:** 
  * Khách hàng tự đặt trên App B2C.
  * Hoặc khách hàng gọi điện trực tiếp vào Hotline, nhân viên tổng đài nhập thông tin đặt hàng hộ khách trên Web Operator Portal.
* **Tải chợ đơn trên App Driver:** Loại bỏ cơ chế Websocket real-time phức tạp. Tài xế thực hiện vuốt kéo màn hình để tải lại (Pull-to-refresh) hoặc nhấn nút "Tải lại" trên App Driver để cập nhật danh sách đơn mới.
* **Ngoại lệ 3.1.1: Can thiệp đơn trôi & Xử lý từ chối gán đơn:**
  * Nhân viên tổng đài chỉ can thiệp gán đơn thủ công trên Web Operator Portal khi đơn trôi quá lâu (ví dụ >15 phút) hoặc khách hàng gọi điện phàn nàn. Hệ thống chặn không cho gán đơn cho tài xế đang ở trạng thái Ngoại tuyến (Offline).
  * Khi nhân viên gán đơn thủ công, nếu tài xế C bấm **Từ chối**:
    1. Tài xế C sẽ tự động bị hệ thống **khóa App không cho nhận đơn trong N phút** (N là tham số cấu hình được trong Web Admin).
    2. Đơn hàng đó tự động được trả ngược lại Chợ đơn để tài xế khác nhận.
* **Ngoại lệ 3.1.2: Sự cố dọc đường của Tài xế:**
  * Tài xế A báo cáo sự cố (hỏng xe, tai nạn) kèm ảnh chụp minh chứng -> Đơn hàng giải phóng về Chợ đơn.
  * Tài xế B giật đơn đó, chạy đến kho lấy bình gas mới giao cho khách. Tài xế A mang bình gas cũ về kho sau khi sửa xe xong để đối soát.

---

### Quy trình 3.2: Quản lý Công nợ Khách hàng (Customer Debt Management)
* **Điều kiện tự động nợ:** Đặt gas trên **1 năm** AND mua lũy kế trên **10 bình**. Áp dụng cứng cho tất cả mọi khách hàng, không có cơ chế duyệt nợ đặc biệt cho VIP (ai đủ điều kiện mới được nợ).
* **Cơ chế chống spam đơn ảo đặt/hủy liên tục:** 
  * Hủy đơn quá **3 lần trong vòng 24 giờ** -> Tài khoản tự động bị khóa tính năng COD và Ghi nợ, bắt buộc phải thanh toán online khi đặt đơn mới.
* **Ngoại lệ 3.2.1: Chuyển khoản VietQR bị lỗi / Ngân hàng bảo trì khi tài xế nộp tiền ngay:**
  * Nếu tài khoản ngân hàng của đại lý hoặc tài xế bị lỗi không thể chuyển khoản COD giữa ca:
    1. Hệ thống tự động khóa App tài xế do giữ tiền mặt vượt hạn mức cho phép.
    2. Tài xế chụp ảnh màn hình thông báo lỗi/bảo trì của ngân hàng, gửi báo cáo sự cố nộp tiền ngay về hệ thống.
    3. Admin duyệt minh chứng trên Web Admin Portal, phê duyệt mở khóa tạm thời cho tài xế hoạt động tiếp và gia hạn thời gian nộp tiền trên hệ thống (chờ ngân hàng hoạt động trở lại).
* **Ngoại lệ 3.2.2: Luồng dự phòng khi quét VietQR thanh toán lỗi:**
  * *Luồng chính:* Khách hàng/Tài xế quét VietQR chuyển vào tài khoản ngân hàng chính của đại lý.
  * *Dự phòng 1:* Nếu TK chính lỗi, hệ thống tự động đổi mã QR hiển thị tài khoản ngân hàng phụ của đại lý.
  * *Dự phòng 2:* Nếu cả hai TK đại lý đều lỗi, hiển thị mã QR tài khoản ngân hàng cá nhân của Tài xế đi giao. Khách chuyển khoản cho tài xế, cuối ngày tài xế quyết toán và chuyển trả lại đại lý.

---

### Quy trình 3.3: Quản lý Vỏ bình Gas & Tiền cọc (Cylinder & Deposit)
* **Quản lý vỏ chéo hãng:** Đại lý thu hồi vỏ hãng PG Gas, Totalgaz... đổi chéo miễn phí. Hệ thống theo dõi tồn kho vỏ rỗng theo hãng độc lập (`PG_RONG`, `TOTAL_RONG`, `PMG_RONG`).
* **Đối lưu vỏ với NSX:** 
  * Đại lý chấp nhận thu hồi mọi loại vỏ từ khách (kể cả cũ nát, trầy xước) và đối lưu 1-1 trực tiếp với xe của nhà sản xuất. 
  * Hệ thống chỉ ghi nhận tồn kho vỏ rỗng theo số lượng và thương hiệu, không phân loại trạng thái chất lượng vỏ (Tốt/Nát) để tinh gọn nghiệp vụ.

---

### Quy trình 3.4: Bàn giao & Quyết toán Cuối ca của Tài xế (Reconciliation)
* **Quy định Tiền lẻ đầu ca:** Tài xế mang theo tối thiểu **500.000đ tiền mặt cá nhân** để thối tiền lẻ và hoàn cọc vỏ cho khách.
* **Đối soát cuối ca:** Thủ kho đếm tiền mặt, vỏ rỗng, bình gas dư đem về để xác nhận đóng ca.
  * Nếu tài xế làm mất vỏ: Admin tạo lệnh phạt đền vỏ (Ví dụ: 500.000đ/vỏ) trừ trực tiếp vào lương. Hệ thống ghi tăng hao hụt vỏ rỗng ảo (Ví dụ: `PG_LOSS` +1) và trừ tồn kho vỏ thực tế.

---

### Quy trình 3.5: Quy trình Bảo hành & Sửa chữa (Warranty & Repairs)
* **Khách hàng gửi yêu cầu:** Khách hàng không cần biết lỗi kỹ thuật cụ thể. Khách hàng chỉ nhập mô tả chung về tình trạng bếp (Ví dụ: "Bếp lửa đỏ", "Không đánh lửa") trên App B2C để gửi ticket.
* **Luồng sửa chữa bảo hành tại chỗ:**
  1. Tài xế có kỹ thuật giật đơn bảo hành, đến nhà khách kiểm tra lỗi thực tế.
  2. Tài xế xác định cần thay linh kiện X, Y -> Chọn linh kiện từ hòm đồ di động trên App Driver, tạo đề xuất báo giá sửa chữa và bấm gửi.
  3. **Nhân viên tổng đài (Operator)** kiểm tra và duyệt đề xuất này trên Web Operator Portal.
  4. Hệ thống tự động gửi thông báo chi tiết chi phí đề xuất về App của **Khách hàng**.
  5. **Khách hàng** bấm **Xác nhận đồng ý** trên App của mình -> App Tài xế nhận tín hiệu đồng ý -> Tài xế mới được phép tiến hành thay thế và sửa chữa thực tế.
* **Quản lý Bếp mượn dùng tạm:**
  * Nếu bếp hỏng nặng cần mang về đại lý sửa dài ngày: Tài xế bàn giao bếp dùng tạm cho khách.
  * App tài xế chỉ hiển thị danh sách các bếp dùng tạm đang ở trạng thái **Rảnh (Available)** để chọn bàn giao cho khách (Không hiển thị bếp đang bận).
  * Khách hàng tự bấm xác nhận đã nhận bếp dùng tạm trên App B2C của mình.

---

## 4. BẢNG THAM SỐ CẤU HÌNH HỆ THỐNG (SYSTEM CONFIGURATION SETTINGS)
Để tránh việc cố định cứng (hardcoding) các chỉ số vận hành, toàn quyền cấu hình các tham số sau sẽ thuộc về Admin trên giao diện Web Admin Portal:

| Tên Tham Số | Giá Trị Mặc Định | Đơn Vị | Mô Tả |
| :--- | :--- | :--- | :--- |
| `D_RATE` | 5,000 | VND / km | Đơn giá khoảng cách tính lương tài xế. |
| `P_RATE_PCT` | 2.0 | % | Phần trăm hoa hồng chia sẻ trên giá trị đơn gas. |
| `T_LOCK_OUT` | 30 | Phút | Thời gian khóa App tài xế khi từ chối đơn gán thủ công. |
| `T_ORDER_TIMEOUT` | 15 | Phút | Thời gian đơn trôi tối đa trên chợ đơn trước khi Operator can thiệp. |
| `DEFAULT_LIMIT_LE` | 1,000,000 | VND | Hạn mức nợ tối đa dành cho Khách lẻ. |
| `DEFAULT_LIMIT_SI` | 10,000,000 | VND | Hạn mức nợ tối đa dành cho Khách sỉ. |
| `CYLINDER_LOSS_FINE` | 500,000 | VND | Tiền phạt đền 1 vỏ bình gas làm mất của tài xế. |

---

## 5. KẾT LUẬN & CÁC BƯỚC TIẾP THEO (NEXT STEPS)
Tài liệu BRD phiên bản v7 đã hoàn thiện và chuẩn hóa 100% scope hoạt động thực tế của đại lý gas, loại bỏ các nghiệp vụ phức tạp không cần thiết và phân quyền rõ ràng hai giao diện Web Admin (Chủ) và Web Operator (Nhân viên).

**Các bước tiếp theo:**
1. Chủ đại lý phê duyệt tài liệu BRD v7.
2. BA tiến hành cập nhật danh sách User Stories tương ứng lên phiên bản v4.
3. BA tiến hành xây dựng sơ đồ cơ sở dữ liệu (Database Schema).
