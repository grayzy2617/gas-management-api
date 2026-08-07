# TÀI LIỆU YÊU CẦU NGHIỆP VỤ (BUSINESS REQUIREMENTS DOCUMENT - BRD)
## DỰ ÁN: HỆ THỐNG QUẢN LÝ BÁN GAS & THIẾT BỊ HỘ KINH DOANH
**Vai trò phân tích:** Senior Business Analyst (dựa trên quy chuẩn [business-analyst.md](file:///d:/6rd_semester/gas_management/rule_agent/business-analyst.md))  
**Phiên bản:** 8.0 (Reconciled VietQR Fallback, Warranty Media & Supplier Cylinder Rules)  
**Ngày lập:** 02-08-2026

---

## 1. TỔNG QUAN DỰ ÁN & MỤC TIÊU CHIẾN LƯỢC
Hệ thống được xây dựng nhằm chuyển đổi số mô hình vận hành của một đại lý gas lớn (nhập hàng trực tiếp từ nhà sản xuất và phân phối tới hộ gia đình B2C, nhà hàng/xí nghiệp B2B).
* **Mục tiêu cốt lõi:**
  * Minh bạch hóa dòng tiền thực tế thông qua việc đối chiếu tự động đơn hàng của tài xế cuối ngày.
  * Tối ưu hóa vận hành giao hàng bằng thuật toán "giật đơn" tự động (phương án C) kết hợp tự làm mới thủ công (Pull-to-refresh).
  * Quản lý chặt chẽ công nợ (khách hàng lẻ/sỉ và công nợ với nhà sản xuất).
  * Kiểm soát rủi ro dòng tiền bằng việc loại bỏ thanh toán qua tài khoản cá nhân tài xế, thay thế bằng luồng "Nợ tạm thời" kiểm soát chặt chẽ.
  * Tăng hiệu quả chẩn đoán lỗi bảo hành bằng cách bắt buộc tải ảnh/video minh chứng từ khách hàng.
  * Quản lý chính xác đối lưu vỏ bình gas với từng Nhà sản xuất theo thương hiệu.

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
* **Tải chợ đơn trên App Driver:** Tài xế thực hiện vuốt kéo màn hình để tải lại (Pull-to-refresh) hoặc nhấn nút "Tải lại" trên App Driver để cập nhật danh sách đơn mới.
* **Ngoại lệ 3.1.1: Can thiệp đơn trôi & Xử lý từ chối gán đơn:**
  * Nhân viên tổng đài chỉ can thiệp gán đơn thủ công trên Web Operator Portal khi đơn trôi quá lâu (ví dụ >15 phút) hoặc khách hàng gọi điện phàn nàn. Hệ thống chặn không cho gán đơn cho tài xế đang ở trạng thái Ngoại tuyến (Offline).
  * Khi nhân viên gán đơn thủ công, nếu tài xế C bấm **Từ chối**:
    1. Tài xế C sẽ tự động bị hệ thống **khóa App không cho nhận đơn trong T_LOCK_OUT phút** (cấu hình được trong Web Admin).
    2. Đơn hàng đó tự động được trả ngược lại Chợ đơn để tài xế khác nhận.
* **Ngoại lệ 3.1.2: Sự cố dọc đường của Tài xế:**
  * Tài xế A báo cáo sự cố (hỏng xe, tai nạn) kèm ảnh chụp minh chứng -> Đơn hàng giải phóng về Chợ đơn.
  * Tài xế B giật đơn đó, chạy đến kho lấy bình gas mới giao cho khách. Tài xế A mang bình gas cũ về kho sau khi sửa xe xong để đối soát.

---

### Quy trình 3.2: Quản lý Công nợ Khách hàng (Customer Debt Management)

#### A. CHÍNH SÁCH BÁN HÀNG GHI NỢ THÔNG THƯỜNG (CD-001)
* **Điều kiện tự động cấp nợ (Áp dụng cho cả Khách sỉ B2B và Khách lẻ B2C):**
  * **Khách sỉ (B2B - Quán ăn / Nhà hàng):** Đủ điều kiện mua nợ khi thâm niên giao dịch **> 1 năm** AND số bình gas mua tích lũy **> 20 bình**. Hạn mức nợ mặc định: **10,000,000đ** (`DEFAULT_LIMIT_SI`).
  * **Khách lẻ (B2C - Hộ gia đình):** Đủ điều kiện mua nợ khi thâm niên giao dịch **> 1 năm** AND số bình gas mua tích lũy **> 10 bình**. Hạn mức nợ mặc định: **1,000,000đ** (`DEFAULT_LIMIT_LE`).
* Hệ thống tự động kiểm tra lịch sử giao dịch và bật tùy chọn "Ghi nợ" khi thanh toán. Khách hàng chưa đủ điều kiện thâm niên hoặc sản lượng bắt buộc thanh toán COD hoặc VietQR online. Áp dụng chuẩn tự động 100%, không duyệt cảm tính.

#### B. CƠ CHẾ CHỐNG SPAM ĐƠN ẢO ĐẶT/HỦY LIÊN TỤC
* Hủy đơn quá **3 lần trong vòng 24 giờ** -> Tài khoản tự động bị khóa tính năng COD và Ghi nợ, bắt buộc phải thanh toán online khi đặt đơn mới.

#### C. LUỒNG DỰ PHÒNG KHI CỔNG THANH TOÁN VIETQR LỖI / NGÂN HÀNG BẢO TRÌ (CD-003)
Để bảo mật dòng tiền của đại lý, **tuyệt đối không hiển thị mã QR tài khoản cá nhân của tài xế** dưới mọi hình thức. Giao dịch sẽ đi qua 3 cấp dự phòng:
1. *Cấp 1 (Luồng chính):* Khách quét VietQR chuyển vào tài khoản ngân hàng chính của đại lý.
2. *Cấp 2 (Dự phòng 1):* Nếu TK chính bảo trì, hệ thống tự động hiển thị mã QR tài khoản ngân hàng phụ của đại lý.
3. *Cấp 3 (Dự phòng 2 - Nợ tạm thời - Pending Payment):* Nếu cả hai TK đại lý đều lỗi sập hệ thống ngân hàng diện rộng và khách hàng không có sẵn tiền mặt (COD):
   * **Bản chất Nợ tạm thời:** Là luồng xử lý sự cố bất khả kháng, áp dụng cho **mọi khách hàng** (kể cả khách lẻ mới) để tài xế hoàn thành ca giao hàng nhanh chóng, không phải đứng đợi tại nhà khách.
   * **Workflow thực hiện:**
     * Tài xế bấm nút "Báo cáo lỗi ngân hàng - Xin cho khách nợ tạm" trên App Driver.
     * Tài xế chụp ảnh màn hình điện thoại của khách hiển thị thông báo lỗi chuyển khoản ngân hàng làm bằng chứng để gửi lên hệ thống.
     * Hệ thống tự động chuyển đơn hàng sang trạng thái: **"Đã giao - Chờ thanh toán" (Pending Payment)**, giải phóng tài xế tiếp tục đi giao đơn khác.
     * **Thu hồi nợ:** Khi ngân hàng ổn định lại, khách hàng truy cập App B2C vào mục "Đơn hàng chưa thanh toán" để thực hiện thanh toán lại bằng mã QR mới, hoặc hệ thống gửi Zalo/SMS tự động chứa link thanh toán nhắc nợ.
     * **Kiểm soát rủi ro:** Nếu sau **24 giờ** kể từ khi nhận nợ tạm thời khách hàng vẫn chưa hoàn thành thanh toán, hệ thống sẽ **tự động khóa tài khoản** của khách hàng, chặn không cho đặt đơn mới.

---

### Quy trình 3.3: Quản lý Vỏ bình Gas & Tiền cọc (Cylinder & Deposit)
* **Quản lý vỏ chéo hãng:** Đại lý thu hồi vỏ hãng PG Gas, Totalgaz... đổi chéo miễn phí. Hệ thống theo dõi tồn kho vỏ rỗng theo hãng độc lập (`PG_RONG`, `TOTAL_RONG`, `PMG_RONG`).
* **Đối lưu vỏ với Nhà sản xuất (NSX):** 
  * Đại lý và NSX làm việc theo quy tắc thương hiệu khớp 100%: **Chỉ cho phép tạo phiếu đối lưu vỏ rỗng với NSX khi Mã hãng vỏ rỗng xuất ra khớp 100% với Mã hãng bình gas đầy nhập vào** (Ví dụ: Xuất 50 vỏ PG_RONG để nhập 50 bình PG Gas đầy). 
  * Vỏ của hãng nào thì chỉ đối lưu với hãng đó (Ví dụ: Vỏ Totalgaz phải chờ xe của hãng Totalgaz đến đổi riêng). Không cho phép dùng vỏ hãng này đối lưu cấn trừ công nợ với hãng khác.

---

### Quy trình 3.4: Bàn giao & Quyết toán Cuối ca của Tài xế (Reconciliation)
* **Quy định Tiền lẻ đầu ca:** Tài xế mang theo tối thiểu **500.000đ tiền mặt cá nhân** để thối tiền lẻ và hoàn cọc vỏ cho khách.
* **Quyết toán nộp tiền ngay giữa ca (RC-002):**
  * Tài xế nộp tiền COD tích lũy qua VietQR của đại lý. Nếu ngân hàng bảo trì làm giao dịch thất bại dẫn đến tài xế bị khóa app do giữ quá hạn mức tiền mặt:
    * Tài xế chụp ảnh màn hình bảo trì gửi yêu cầu mở khóa.
    * Admin duyệt trên Web Admin Portal để mở khóa tạm thời và gia hạn thời gian nộp tiền trên hệ thống.
* **Đối soát cuối ca:** Thủ kho đếm tiền mặt, vỏ rỗng, bình gas dư đem về để xác nhận đóng ca.
  * Nếu tài xế làm mất vỏ: Admin tạo lệnh phạt đền vỏ (Ví dụ: 500.000đ/vỏ) trừ trực tiếp vào lương. Hệ thống ghi tăng hao hụt vỏ rỗng ảo (Ví dụ: `PG_LOSS` +1) và trừ tồn kho vỏ thực tế.

---

### Quy trình 3.5: Quy trình Bảo hành & Sửa chữa (Warranty & Repairs)
* **Khách hàng gửi yêu cầu:** Khách hàng báo hỏng bếp bắt buộc phải **nhập mô tả lỗi bằng chữ** và **tải lên tối thiểu 1 hình ảnh hoặc video ngắn (dưới 10 giây)** tình trạng thực tế của thiết bị. Điều này giúp nhân viên tổng đài và thợ kỹ thuật "bắt bệnh" từ xa để chuẩn bị đúng chủng loại linh kiện (IC, van điều áp, dây dẫn) trong hòm đồ di động trước khi xuất phát, giảm thiểu thời gian đi lại.
* **Luồng sửa chữa bảo hành tại chỗ:**
  1. Tài xế có kỹ thuật giật đơn bảo hành, đến nhà khách kiểm tra lỗi thực tế.
  2. Tài xế xác định cần thay linh kiện X, Y -> Chọn linh kiện từ hòm đồ di động trên App Driver, tạo đề xuất báo giá sửa chữa và bấm gửi.
  3. **Nhân viên tổng đài (Operator)** kiểm tra và duyệt đề xuất này trên Web Operator Portal.
  4. Hệ thống tự động gửi thông báo chi tiết chi phí đề xuất về App của **Khách hàng**.
  5. **Khách hàng** bấm **Xác nhận đồng ý** trên App B2C của mình -> App Tài xế nhận tín hiệu đồng ý -> Tài xế mới được phép tiến hành thay thế và sửa chữa thực tế.
* **Quản lý Bếp mượn dùng tạm:**
  * Nếu bếp hỏng nặng cần mang về đại lý sửa dài ngày: Tài xế bàn giao bếp dùng tạm cho khách.
  * App tài xế chỉ hiển thị danh sách các bếp dùng tạm đang ở trạng thái **Rảnh (Available)** để chọn bàn giao cho khách (Không hiển thị bếp đang bận).
  * Khách hàng tự bấm xác nhận đã nhận bếp dùng tạm trên App B2C của mình.

---

## 4. BẢNG THAM SỐ CẤU HÌNH HỆ THỐNG (SYSTEM CONFIGURATION SETTINGS)
Toàn quyền cấu hình các tham số sau thuộc về Admin trên giao diện Web Admin Portal:

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
Tài liệu BRD phiên bản v8 đã cập nhật chính xác luồng Nợ tạm thời (Pending Payment) thay thế QR tài xế, luồng bảo hành bắt buộc hình ảnh/video và quy chế đối lưu vỏ cùng thương hiệu với NSX.

**Các bước tiếp theo:**
1. Chủ đại lý phê duyệt tài liệu BRD v8.
2. BA tiến hành cập nhật danh sách User Stories tương ứng lên phiên bản v5.
3. BA tiến hành xây dựng sơ đồ cơ sở dữ liệu (Database Schema).
