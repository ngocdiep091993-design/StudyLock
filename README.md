# StudyLock 1.3.0 — Học để mở khóa (Android)

Dự án Android Studio Java, giao diện tiếng Việt, Android 8.0 trở lên. Chạy ngoại tuyến, không quảng cáo, không máy chủ, không quyền Internet. Đây là nguyên mẫu mã nguồn, chưa biên dịch APK hoặc kiểm thử trên thiết bị Android. Không sử dụng làm cơ chế bảo vệ duy nhất trước khi kiểm tra thực tế.

## Chức năng
- Giữ danh sách khóa YouTube, YouTube Kids, TikTok, Facebook, trình duyệt, cửa hàng ứng dụng và ứng dụng được phụ huynh chọn. Không khóa launcher, ứng dụng gọi điện mặc định và các gói hệ thống thiết yếu.
- Lịch riêng từng ngày, nhiều khung giờ, hỗ trợ qua nửa đêm và khung chồng nhau. Hạn mức chung thiết bị từ 0–1440 phút/ngày, mặc định 90 phút. Thời gian theo lịch và thưởng cùng tính vào hạn mức. Hết hạn mức thì không có bài mở khóa. Hạn mức mới lúc 00:00 địa phương.
- Tối đa 8 hồ sơ học tập; phụ huynh tạo, đổi tên, chuyển hồ sơ sau khi nhập PIN. Mỗi hồ sơ lưu riêng lớp, học kỳ, môn, chế độ nhập Toán, độ khó và số câu đúng/sai. Lịch, hạn mức, danh sách khóa, thời gian nghỉ, số câu/mức thưởng là cấu hình chung trên điện thoại. Không nhận diện tự động trẻ đang sử dụng. Chuyển hồ sơ không đặt lại thời gian giải trí đã dùng; hủy thưởng/nghỉ hiện tại. Hồ sơ 1 kế thừa dữ liệu học tập phiên bản trước.
- Bài tập gốc: 90 câu Tiếng Anh (5 câu/nhóm lớp–học kỳ–độ khó), 36 mẫu sinh số liệu Toán (6 mẫu/khối lớp–học kỳ); Toán có lựa chọn tự nhập số nguyên, số thập phân, phân số. Phân loại học kỳ là gợi ý ôn tập, không phải mục lục chính thức của từng bộ sách. Có thể chọn cả năm. Môn ngẫu nhiên hoặc một môn duy nhất. Toán tăng độ lớn số theo mức; Tiếng Anh tăng độ phức tạp câu.
- Ba mức cơ bản/trung bình/nâng cao. Nếu bật tự thích ứng, mỗi môn tăng một mức sau 3 câu đúng liên tiếp, giảm một mức sau 2 câu sai liên tiếp, trong giới hạn 1–3. Đổi lớp/học kỳ/mức ban đầu hoặc bật tắt thích ứng đặt lại mức hiện hành; không xóa thống kê. Nếu tắt thích ứng, sử dụng mức phụ huynh chọn.
- Chọn thưởng 5, 10, 20 phút trên màn hình bài tập. Mỗi mức yêu cầu 1–10 câu đúng liên tiếp do phụ huynh chọn (mặc định tối thiểu 1/2/4, giữ ít nhất số câu cũ). Sai đặt lại chuỗi đúng và hiển thị đáp án; đổi mức thưởng đặt lại chuỗi. Thưởng tối đa bằng hạn mức ngày còn lại. Thưởng là thời gian đồng hồ tính từ lúc đạt yêu cầu, kể cả chuyển ứng dụng hoặc tắt màn hình, dùng chung cho các app. Không cộng dồn, không chuyển qua nửa đêm, mất thưởng khi khởi động lại.
- Nghỉ bắt buộc tùy chọn 0/1/3/5/10/15 phút sau khi hết thời gian thưởng, trước khi làm bài tiếp. Màn hình đếm ngược; lịch tự do vẫn được ưu tiên nếu đến giờ và còn hạn mức. Nghỉ tính theo giờ hệ thống, không kéo sang ngày mới. Mặc định tắt; phụ huynh có thể cấu hình 5 phút. Lưu cấu hình hoặc chuyển hồ sơ hủy thưởng và thời gian nghỉ.
- Báo cáo 7/30 ngày: số câu đúng, sai từng môn của hồ sơ đang chọn và thời gian giải trí chung thiết bị; xuất CSV UTF-8 qua trình chọn tệp Android để mở bằng Excel. Không tự tải lên mạng. Phụ huynh chọn nơi lưu, lưu ý bảo mật nếu chọn thư mục đám mây.
- PIN 6–12 chữ số, không mặc định; PBKDF2-HMAC-SHA256 với salt ngẫu nhiên, 120.000 vòng. Sai 5 lần chờ 30 giây. Mã PIN và cấu hình lưu cục bộ. Hỗ trợ tùy chọn chống gỡ StudyLock nếu thiết bị đã được cấp Device Owner hợp lệ; không tự cấp quyền, không chống tắt Trợ năng tuyệt đối. Có chức năng thu hồi trong khu vực PIN.

## Tham khảo phân nhóm nội dung
- Thông tư 32/2018/TT-BGDĐT: https://vbpl.moj.gov.vn/bogiaoducdaotao/Pages/vbpq-toanvan.aspx?ItemID=146721
- Chương trình môn Toán: https://accgroup.vn/thong-tu-32-2018-tt-bgddt-ve-mon-toan
- Gợi ý chủ đề Tiếng Anh 4 và 6 Global Success: https://studyai.vn/global-success/lop-4/ ; https://cuppy.hoclieu.vn/book/g6-english-global-success
- Câu hỏi do dự án tự soạn, không sao chép ngân hàng SGK; chưa được giáo viên thẩm định. Cần đối chiếu kế hoạch dạy học cụ thể của trường, đặc biệt khi chọn học kỳ.

## Tạo APK trên Windows
1. Tải Android Studio: https://developer.android.com/studio. Cài SDK Platform 35, JDK 17 (hoặc JDK tương thích đi kèm Studio).
2. Giải nén ZIP, mở thư mục StudyLock_v1_3 (chứa settings.gradle, build.gradle, app).
3. Dùng Android Gradle Plugin 8.7.3 và Gradle 8.9. ZIP không có Gradle Wrapper JAR. Nếu Studio không tự đồng bộ, tải Gradle 8.9 từ https://gradle.org/releases/ rồi cấu hình Gradle distribution trong IDE, hoặc chạy `C:\gradle-8.9\bin\gradle.bat wrapper --gradle-version 8.9` (sửa đường dẫn thực tế), sau đó ` .\gradlew.bat assembleDebug `. Cần mạng cho lần tải phụ thuộc đầu tiên.
4. Build > Generate App Bundles or APKs > Generate APKs. APK debug dự kiến ở `app/build/outputs/apk/debug/app-debug.apk`. Chỉ dùng thử. Bản phân phối cần ký và kiểm thử.
5. Cài lên điện thoại, tạo PIN (nếu mới), chọn hồ sơ, lớp/học kỳ, các mức thưởng, nghỉ, hạn mức, lịch, danh sách app. Bấm Lưu cấu hình. Chọn Cấp quyền Trợ năng và tự bật dịch vụ trong Cài đặt. Android 13+ có thể giới hạn quyền cho ứng dụng cài ngoài cửa hàng: chỉ tự cấp quyền nếu tin tưởng nguồn APK.
6. Nếu nâng từ v1.2: applicationId không đổi, versionCode 4. Cài đè cần chữ ký giống bản đã cài. Không gỡ bản cũ nếu muốn giữ dữ liệu. Sao lưu dữ liệu cần thiết trước.

## Kiểm thử thiết bị
- Lịch trống, hạn mức 1 phút: giải đúng mức 5 phút, chỉ nhận tối đa 1 phút. Hết hạn mức phải khóa toàn bộ nhóm, không cho làm bài. Chuyển app/tắt màn hình thử bộ đếm. Lịch 0 phút phải khóa cả ngày. Kiểm tra ngày mới, lịch qua nửa đêm, khởi động lại.
- Đặt số câu 1/2/4, thử từng mức, đổi mức giữa bài, trả lời sai. Bật nghỉ 1 phút và thử hết thưởng, chờ đếm ngược, kiểm tra lịch tự do vẫn hoạt động đúng.
- Tạo 2 hồ sơ khác lớp/học kỳ, trả lời đúng/sai, kiểm tra mức thích ứng riêng mỗi môn, chuyển hồ sơ và kiểm tra báo cáo riêng, thời gian giải trí chung không bị đặt lại. Xuất CSV 7/30 ngày, mở Excel, kiểm tra dấu tiếng Việt.
- Kiểm tra PIN, ra/vào app, cấp/thu hồi Trợ năng, gọi điện, Home, chia màn hình, PiP, bàn phím, ứng dụng nổi, trình duyệt nhúng, khởi động lại. Kiểm tra chế độ quản lý thiết bị chỉ trên thiết bị riêng của trẻ do phụ huynh quản lý hợp lệ; không thử cấp Device Owner trên điện thoại đang sử dụng nếu chưa hiểu hậu quả. Tham khảo https://developer.android.com/work/dpc/dedicated-devices và https://developer.android.com/reference/android/app/admin/DevicePolicyManager .

## Giới hạn
Trợ năng nhận tên gói từ sự kiện cửa sổ và hiển thị overlay, không đọc cây nội dung. Bộ đếm tiền cảnh lấy mẫu 0,5 giây, có thể đếm thiếu khi mất sự kiện, dịch vụ dừng, chia màn hình, PiP, trình duyệt nhúng, thay đổi giờ. Không khóa tuyệt đối; không chống xóa dữ liệu, đổi giờ hoặc reset máy. Không tự cấp quyền quản trị, không cản trở cuộc gọi khẩn cấp hoặc thao tác hệ thống. Phụ huynh có thể thu hồi quyền. Nếu phát hành Google Play, cần tuân thủ chính sách Trợ năng và dữ liệu người dùng. Nếu quên PIN phải đặt lại dữ liệu ứng dụng (mất cấu hình và thống kê).

## Kiểm tra logic Java đã thực hiện
Chạy bằng JDK 17 trong thư mục dự án (PowerShell):
```
mkdir test-classes
javac -encoding UTF-8 -d test-classes app/src/main/java/vn/studylock/Rules.java app/src/main/java/vn/studylock/DailyBudget.java app/src/main/java/vn/studylock/MathAnswer.java app/src/main/java/vn/studylock/Questions.java app/src/main/java/vn/studylock/Curriculum.java app/src/main/java/vn/studylock/LearningRules.java tests/LogicTest.java tests/CurriculumTest.java
java -cp test-classes LogicTest
java -cp test-classes CurriculumTest
```
Kết quả trong tests/test-result.txt: 90.000 lượt kiểm tra bộ sinh cũ, 108.000 lượt kiểm tra bộ sinh mới (lặp lại các mẫu, không phải 108.000 câu độc nhất); kiểm tra đủ 90 câu Tiếng Anh, cấu trúc đáp án, chấm đáp án tự nhập, ngưỡng thích ứng, biên thời gian nghỉ, hạn mức/lịch. Đây không phải kiểm chứng độc lập từng lời giải hoặc kiểm thử tích hợp Android. Môi trường tạo mã không có Android SDK/Gradle, vì vậy chưa xác nhận build APK hay hoạt động của giao diện, Trợ năng, CSV và lưu trữ hồ sơ trên thiết bị.
