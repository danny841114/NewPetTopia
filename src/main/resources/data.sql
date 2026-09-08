--------------------------------------------------
-- 0. 使用者資料
--------------------------------------------------
-- 插入使用者(會員)
INSERT INTO users (password, email, user_role, email_verified, provider) VALUES
('$2a$10$JEF.t.oPIVkTZSXFvFqfUuCSNhbhiDB4igj37fmWOb8rFpK5JrEVu', 'vendor1@example.com', 'VENDOR', 1, 'LOCAL'),
('$2a$10$JEF.t.oPIVkTZSXFvFqfUuCSNhbhiDB4igj37fmWOb8rFpK5JrEVu', 'vendor2@example.com', 'VENDOR', 1, 'LOCAL'),
('$2a$10$JEF.t.oPIVkTZSXFvFqfUuCSNhbhiDB4igj37fmWOb8rFpK5JrEVu', 'vendor3@example.com', 'VENDOR', 1, 'LOCAL'),
('$2a$10$JEF.t.oPIVkTZSXFvFqfUuCSNhbhiDB4igj37fmWOb8rFpK5JrEVu', 'vendor4@example.com', 'VENDOR', 1, 'LOCAL'),
('$2a$10$JEF.t.oPIVkTZSXFvFqfUuCSNhbhiDB4igj37fmWOb8rFpK5JrEVu', 'vendor5@example.com', 'VENDOR', 1, 'LOCAL'),
('$2a$10$JEF.t.oPIVkTZSXFvFqfUuCSNhbhiDB4igj37fmWOb8rFpK5JrEVu', 'vendor6@example.com', 'VENDOR', 1, 'LOCAL'),
('$2a$10$JEF.t.oPIVkTZSXFvFqfUuCSNhbhiDB4igj37fmWOb8rFpK5JrEVu', 'vendor7@example.com', 'VENDOR', 1, 'LOCAL'),
('$2a$10$JEF.t.oPIVkTZSXFvFqfUuCSNhbhiDB4igj37fmWOb8rFpK5JrEVu', 'vendor8@example.com', 'VENDOR', 1, 'LOCAL'),
('$2a$10$JEF.t.oPIVkTZSXFvFqfUuCSNhbhiDB4igj37fmWOb8rFpK5JrEVu', 'vendor9@example.com', 'VENDOR', 1, 'LOCAL'),
('$2a$10$JEF.t.oPIVkTZSXFvFqfUuCSNhbhiDB4igj37fmWOb8rFpK5JrEVu', 'vendor10@example.com', 'VENDOR', 1, 'LOCAL');

-- 插入使用者(店家)
INSERT INTO users (password, email, user_role, email_verified, provider) VALUES
('$2a$10$JEF.t.oPIVkTZSXFvFqfUuCSNhbhiDB4igj37fmWOb8rFpK5JrEVu', 'member1@example.com', 'MEMBER', 1, 'LOCAL'),
('$2a$10$JEF.t.oPIVkTZSXFvFqfUuCSNhbhiDB4igj37fmWOb8rFpK5JrEVu', 'member2@example.com', 'MEMBER', 1, 'LOCAL'),
('$2a$10$JEF.t.oPIVkTZSXFvFqfUuCSNhbhiDB4igj37fmWOb8rFpK5JrEVu', 'member3@example.com', 'MEMBER', 1, 'LOCAL'),
('$2a$10$JEF.t.oPIVkTZSXFvFqfUuCSNhbhiDB4igj37fmWOb8rFpK5JrEVu', 'member4@example.com', 'MEMBER', 1, 'LOCAL'),
('$2a$10$JEF.t.oPIVkTZSXFvFqfUuCSNhbhiDB4igj37fmWOb8rFpK5JrEVu', 'member5@example.com', 'MEMBER', 1, 'LOCAL'),
('$2a$10$JEF.t.oPIVkTZSXFvFqfUuCSNhbhiDB4igj37fmWOb8rFpK5JrEVu', 'member6@example.com', 'MEMBER', 1, 'LOCAL'),
('$2a$10$JEF.t.oPIVkTZSXFvFqfUuCSNhbhiDB4igj37fmWOb8rFpK5JrEVu', 'member7@example.com', 'MEMBER', 1, 'LOCAL'),
('$2a$10$JEF.t.oPIVkTZSXFvFqfUuCSNhbhiDB4igj37fmWOb8rFpK5JrEVu', 'member8@example.com', 'MEMBER', 1, 'LOCAL'),
('$2a$10$JEF.t.oPIVkTZSXFvFqfUuCSNhbhiDB4igj37fmWOb8rFpK5JrEVu', 'member9@example.com', 'MEMBER', 1, 'LOCAL'),
('$2a$10$JEF.t.oPIVkTZSXFvFqfUuCSNhbhiDB4igj37fmWOb8rFpK5JrEVu', 'member10@example.com', 'MEMBER', 1, 'LOCAL'),
('$2a$10$JEF.t.oPIVkTZSXFvFqfUuCSNhbhiDB4igj37fmWOb8rFpK5JrEVu', 'winter@example.com', 'MEMBER', 1, 'LOCAL');

-- 插入店家類別
INSERT INTO vendor_category (name) VALUES
('寵物美容'),
('寵物用品店'),
('寵物醫院'),
('寵物寄宿'),
('寵物餐廳'),
('寵物訓練'),
('水族用品'),
('爬蟲類專門店'),
('寵物攝影'),
('寵物手作工坊'),
('其他');

-- 插入店家資料
INSERT INTO vendor (id, name, description, logo_img, address, phone, contact_email, contact_person, taxid_number, status, vendor_category_id, registration_date, updated_date, event_count, total_rating, review_count, vendor_level)
VALUES
(1, '毛孩天堂寵物美容', '專業寵物美容與SPA，讓毛孩擁有最舒適的體驗', NULL, '台北市大安區信義路五段100號', '02-1234-5678', 'contact1@example.com', '張小姐', '12345678', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0, 0, 0, '普通'),
(2, '汪喵精品寵物用品', '提供各種寵物食品與用品，滿足毛孩需求', NULL, '台中市西屯區台灣大道三段200號', '04-8765-4321', 'contact2@example.com', '李先生', '23456789', 1, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0, 0, 0, '普通'),
(3, '安心動物醫院', '專業獸醫團隊，提供最安心的醫療服務', NULL, '新北市板橋區中山路一段300號', '02-5566-7788', 'contact3@example.com', '王醫師', '34567890', 1, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0, 0, 0, '普通'),
(4, '毛孩樂園寵物寄宿', '專業照顧，給毛孩一個舒適的家', NULL, '高雄市苓雅區成功一路50號', '07-3344-5566', 'contact4@example.com', '林小姐', '45678901', 1, 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0, 0, 0, '普通'),
(5, '寵物咖啡館喵喵汪汪', '享受美食與毛孩共度美好時光', NULL, '桃園市中壢區中華路88號', '03-5566-7788', 'contact5@example.com', '陳先生', '56789012', 1, 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0, 0, 0, '普通'),
(6, '狗狗訓練學院', '專業狗狗訓練課程，讓愛犬變成聽話乖寶寶', NULL, '新竹市東區光復路200號', '03-3344-5566', 'contact6@example.com', '楊教練', '67890123', 1, 6, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0, 0, 0, '普通'),
(7, '海洋樂趣水族館', '專營觀賞魚、海水魚與水族設備', NULL, '台南市中西區民族路77號', '06-7788-5566', 'contact7@example.com', '趙先生', '78901234', 1, 7, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0, 0, 0, '普通'),
(8, '爬寵世界', '專業飼養爬蟲類，提供高品質飼養環境與用品', NULL, '台北市松山區南京東路100號', '02-8899-6677', 'contact8@example.com', '吳先生', '89012345', 1, 8, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0, 0, 0, '普通'),
(9, '毛小孩攝影館', '專為寵物打造美麗回憶的攝影棚', NULL, '台中市南屯區五權西路300號', '04-4455-6677', 'contact9@example.com', '周小姐', '90123456', 1, 9, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0, 0, 0, '普通'),
(10, '手作寵物小物', '手工製作寵物衣物與配件，獨一無二的設計', NULL, '彰化市中正路150號', '04-7788-5566', 'contact10@example.com', '戴小姐', '01234567', 1, 10, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0, 0, 0, '普通');

-- 插入會員資料
INSERT INTO member (id, name, phone, birthdate, gender, address, status) VALUES
(11, '陳莉絲', '0912345678', '1990-01-01', 1, '台北市中正區仁愛路1號', 1),
(12, '王小明', '0923456789', '1991-02-02', 0, '新北市板橋區文化路2號', 1),
(13, '林大雄', '0934567890', '1992-03-03', 0, '台中市西屯區台灣大道3段3號', 1),
(14, '張志國', '0955678901', '1993-04-04', 0, '台南市中西區成功路4號', 1),
(15, '許小美', '0966789012', '1994-05-05', 1, '高雄市苓雅區三多路5號', 1),
(16, '楊志福', '0977890123', '1995-06-06', 0, '桃園市中壢區元化路6號', 1),
(17, '郭佳慧', '0988901234', '1996-07-07', 1, '新竹市東區光復路7號', 1),
(18, '劉建豪', '0910012345', '1997-08-08', 0, '彰化縣員林市中山路8號', 1),
(19, '曾小芸', '0921123456', '1998-09-09', 1, '嘉義市西區中興路9號', 1),
(20, '蘇志偉', '0932234567', '1999-10-10', 0, '基隆市仁愛區信義路10號', 1),
(21, '金冬天', '0987521314', '1999-11-11', 1, '台北市信義區松壽路21號', 1);

--------------------------------------------------
-- 1. 店家與活動基礎資料
--------------------------------------------------
-- 插入活動類型
INSERT INTO activity_type (name) VALUES
('寵物市集'),
('運動會'),
('下午茶'),
('聚餐'),
('DIY活動'),
('寵物攝影日'),
('其他');

-- 插入店家活動
INSERT INTO vendor_activity (vendor_id, name, description, start_time, end_time, is_registration_required, activity_type_id, registration_date, number_visitor, address)
VALUES
(1, '毛孩美容日', '提供免費寵物美容體驗，讓毛孩煥然一新', CURRENT_TIMESTAMP + INTERVAL '7' DAY, CURRENT_TIMESTAMP + INTERVAL '7' DAY, 1, 1, CURRENT_TIMESTAMP, 0, '台北市大安區信義路五段100號'),
(1, 'SPA放鬆體驗', '專業寵物 SPA，舒緩壓力與焦慮', CURRENT_TIMESTAMP + INTERVAL '15' DAY, CURRENT_TIMESTAMP + INTERVAL '15' DAY, 1, 3, CURRENT_TIMESTAMP, 0, '台北市大安區信義路五段100號'),
(2, '新品試吃會', '提供新款寵物食品試吃，讓毛孩找到最愛的口味', CURRENT_TIMESTAMP + INTERVAL '10' DAY, CURRENT_TIMESTAMP + INTERVAL '10' DAY, 0, 4, CURRENT_TIMESTAMP, 0, '台中市西屯區台灣大道三段200號'),
(2, '寵物市集', '各類寵物用品與手作商品展售', CURRENT_TIMESTAMP + INTERVAL '20' DAY, CURRENT_TIMESTAMP + INTERVAL '20' DAY, 0, 1, CURRENT_TIMESTAMP, 0, '台中市西屯區台灣大道三段200號'),
(3, '健康檢查日', '免費寵物健康檢查，提供專業建議', CURRENT_TIMESTAMP + INTERVAL '5' DAY, CURRENT_TIMESTAMP + INTERVAL '5' DAY, 1, 7, CURRENT_TIMESTAMP, 0, '新北市板橋區中山路一段300號'),
(3, '疫苗注射優惠', '特定疫苗施打享優惠價', CURRENT_TIMESTAMP + INTERVAL '12' DAY, CURRENT_TIMESTAMP + INTERVAL '12' DAY, 1, 7, CURRENT_TIMESTAMP, 0, '新北市板橋區中山路一段300號'),
(4, '毛孩運動會', '各種寵物比賽與遊戲，挑戰毛孩體能極限', CURRENT_TIMESTAMP + INTERVAL '8' DAY, CURRENT_TIMESTAMP + INTERVAL '8' DAY, 0, 2, CURRENT_TIMESTAMP, 0, '高雄市苓雅區成功一路50號'),
(4, '住宿體驗日', '免費體驗一天寵物寄宿服務', CURRENT_TIMESTAMP + INTERVAL '18' DAY, CURRENT_TIMESTAMP + INTERVAL '18' DAY, 1, 7, CURRENT_TIMESTAMP, 0, '高雄市苓雅區成功一路50號'),
(5, '寵物下午茶派對', '與毛孩一起享受下午茶時光', CURRENT_TIMESTAMP + INTERVAL '9' DAY, CURRENT_TIMESTAMP + INTERVAL '9' DAY, 0, 3, CURRENT_TIMESTAMP, 0, '桃園市中壢區中華路88號'),
(5, '寵物聚餐趴', '一起與寵物朋友們共享晚餐', CURRENT_TIMESTAMP + INTERVAL '17' DAY, CURRENT_TIMESTAMP + INTERVAL '17' DAY, 0, 4, CURRENT_TIMESTAMP, 0, '桃園市中壢區中華路88號'),
(6, '狗狗行為訓練體驗', '體驗基礎狗狗行為訓練課程', CURRENT_TIMESTAMP + INTERVAL '6' DAY, CURRENT_TIMESTAMP + INTERVAL '6' DAY, 1, 7, CURRENT_TIMESTAMP, 0, '新竹市東區光復路200號'),
(6, '狗狗社交日', '讓狗狗認識新朋友，增強社交能力', CURRENT_TIMESTAMP + INTERVAL '14' DAY, CURRENT_TIMESTAMP + INTERVAL '14' DAY, 0, 2, CURRENT_TIMESTAMP, 0, '新竹市東區光復路200號'),
(7, '水族設備體驗會', '介紹最新水族設備並提供試用', CURRENT_TIMESTAMP + INTERVAL '11' DAY, CURRENT_TIMESTAMP + INTERVAL '11' DAY, 0, 7, CURRENT_TIMESTAMP, 0, '台南市中西區民族路77號'),
(7, '海水魚飼養講座', '專業水族達人分享海水魚養殖技巧', CURRENT_TIMESTAMP + INTERVAL '16' DAY, CURRENT_TIMESTAMP + INTERVAL '16' DAY, 0, 7, CURRENT_TIMESTAMP, 0, '台南市中西區民族路77號'),
(8, '爬蟲飼養工作坊', '專業爬寵飼養知識分享', CURRENT_TIMESTAMP + INTERVAL '13' DAY, CURRENT_TIMESTAMP + INTERVAL '13' DAY, 1, 7, CURRENT_TIMESTAMP, 0, '台北市松山區南京東路100號'),
(8, '親子爬寵體驗日', '讓孩子親近爬蟲，培養對動物的興趣', CURRENT_TIMESTAMP + INTERVAL '21' DAY, CURRENT_TIMESTAMP + INTERVAL '21' DAY, 0, 2, CURRENT_TIMESTAMP, 0, '台北市松山區南京東路100號'),
(9, '寵物攝影日', '專業攝影師捕捉毛孩最美瞬間', CURRENT_TIMESTAMP + INTERVAL '7' DAY, CURRENT_TIMESTAMP + INTERVAL '7' DAY, 1, 6, CURRENT_TIMESTAMP, 0, '台中市南屯區五權西路300號'),
(9, '戶外攝影體驗', '帶毛孩到戶外拍攝自然美景', CURRENT_TIMESTAMP + INTERVAL '19' DAY, CURRENT_TIMESTAMP + INTERVAL '19' DAY, 1, 6, CURRENT_TIMESTAMP, 0, '台中市南屯區五權西路300號'),
(10, '手作寵物飾品課程', 'DIY 製作寵物配件，親手打造專屬小物', CURRENT_TIMESTAMP + INTERVAL '9' DAY, CURRENT_TIMESTAMP + INTERVAL '9' DAY, 1, 5, CURRENT_TIMESTAMP, 0, '彰化市中正路150號'),
(10, '寵物衣物縫紉班', '學習縫製寵物衣物，讓毛孩穿上獨特服裝', CURRENT_TIMESTAMP + INTERVAL '20' DAY, CURRENT_TIMESTAMP + INTERVAL '20' DAY, 1, 5, CURRENT_TIMESTAMP, 0, '彰化市中正路150號');

-- 插入行事曆事件
INSERT INTO calendar_event (vendor_id, event_title, start_time, end_time, vendor_activity_id, color, created_at, updated_at)
VALUES
(1, '毛孩美容日', CURRENT_TIMESTAMP + INTERVAL '7' DAY, CURRENT_TIMESTAMP + INTERVAL '7' DAY, 1, '#FF5733', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, 'SPA放鬆體驗', CURRENT_TIMESTAMP + INTERVAL '15' DAY, CURRENT_TIMESTAMP + INTERVAL '15' DAY, 2, '#33FF57', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, '新品試吃會', CURRENT_TIMESTAMP + INTERVAL '10' DAY, CURRENT_TIMESTAMP + INTERVAL '10' DAY, 3, '#3357FF', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, '寵物市集', CURRENT_TIMESTAMP + INTERVAL '20' DAY, CURRENT_TIMESTAMP + INTERVAL '20' DAY, 4, '#FF33A1', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, '健康檢查日', CURRENT_TIMESTAMP + INTERVAL '5' DAY, CURRENT_TIMESTAMP + INTERVAL '5' DAY, 5, '#FFFF33', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, '疫苗注射優惠', CURRENT_TIMESTAMP + INTERVAL '12' DAY, CURRENT_TIMESTAMP + INTERVAL '12' DAY, 6, '#33FFF0', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, '毛孩運動會', CURRENT_TIMESTAMP + INTERVAL '8' DAY, CURRENT_TIMESTAMP + INTERVAL '8' DAY, 7, '#FF6F33', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, '住宿體驗日', CURRENT_TIMESTAMP + INTERVAL '18' DAY, CURRENT_TIMESTAMP + INTERVAL '18' DAY, 8, '#33FFAB', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5, '寵物下午茶派對', CURRENT_TIMESTAMP + INTERVAL '9' DAY, CURRENT_TIMESTAMP + INTERVAL '9' DAY, 9, '#FF33E1', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5, '寵物聚餐趴', CURRENT_TIMESTAMP + INTERVAL '17' DAY, CURRENT_TIMESTAMP + INTERVAL '17' DAY, 10, '#33FF99', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(6, '狗狗行為訓練體驗', CURRENT_TIMESTAMP + INTERVAL '6' DAY, CURRENT_TIMESTAMP + INTERVAL '6' DAY, 11, '#FF5733', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(6, '狗狗社交日', CURRENT_TIMESTAMP + INTERVAL '14' DAY, CURRENT_TIMESTAMP + INTERVAL '14' DAY, 12, '#33FF73', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(7, '水族設備體驗會', CURRENT_TIMESTAMP + INTERVAL '11' DAY, CURRENT_TIMESTAMP + INTERVAL '11' DAY, 13, '#3333FF', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(7, '海水魚飼養講座', CURRENT_TIMESTAMP + INTERVAL '16' DAY, CURRENT_TIMESTAMP + INTERVAL '16' DAY, 14, '#FF33B7', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(8, '爬蟲飼養工作坊', CURRENT_TIMESTAMP + INTERVAL '13' DAY, CURRENT_TIMESTAMP + INTERVAL '13' DAY, 15, '#33FFB7', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(8, '親子爬寵體驗日', CURRENT_TIMESTAMP + INTERVAL '21' DAY, CURRENT_TIMESTAMP + INTERVAL '21' DAY, 16, '#FFCD33', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(9, '寵物攝影日', CURRENT_TIMESTAMP + INTERVAL '7' DAY, CURRENT_TIMESTAMP + INTERVAL '7' DAY, 17, '#FF8C33', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(9, '戶外攝影體驗', CURRENT_TIMESTAMP + INTERVAL '19' DAY, CURRENT_TIMESTAMP + INTERVAL '19' DAY, 18, '#33FFFC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(10, '手作寵物飾品課程', CURRENT_TIMESTAMP + INTERVAL '9' DAY, CURRENT_TIMESTAMP + INTERVAL '9' DAY, 19, '#FF63FF', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(10, '寵物衣物縫紉班', CURRENT_TIMESTAMP + INTERVAL '20' DAY, CURRENT_TIMESTAMP + INTERVAL '20' DAY, 20, '#FF3366', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 插入提醒事件
INSERT INTO calendar_event (vendor_id, event_title, start_time, end_time, vendor_activity_id, created_at, updated_at)
VALUES
(1, '活動報名截止提醒', '2025-03-05 10:00:00', '2025-03-05 10:00:00', 1, '2025-03-05 09:00:00', '2025-03-05 09:00:00'),
(1, '活動開始前通知', '2025-03-05 14:00:00', '2025-03-05 14:00:00', 1, '2025-03-05 09:00:00', '2025-03-05 09:00:00'),
(2, '活動報名截止提醒', '2025-03-06 10:00:00', '2025-03-06 10:00:00', 2, '2025-03-06 09:00:00', '2025-03-06 09:00:00'),
(2, '活動開始前通知', '2025-03-06 14:00:00', '2025-03-06 14:00:00', 2, '2025-03-06 09:00:00', '2025-03-06 09:00:00'),
(3, '活動報名截止提醒', '2025-03-07 10:00:00', '2025-03-07 10:00:00', 3, '2025-03-07 09:00:00', '2025-03-07 09:00:00'),
(3, '活動開始前通知', '2025-03-07 14:00:00', '2025-03-07 14:00:00', 3, '2025-03-07 09:00:00', '2025-03-07 09:00:00'),
(4, '活動報名截止提醒', '2025-03-08 10:00:00', '2025-03-08 10:00:00', 4, '2025-03-08 09:00:00', '2025-03-08 09:00:00'),
(4, '活動開始前通知', '2025-03-08 14:00:00', '2025-03-08 14:00:00', 4, '2025-03-08 09:00:00', '2025-03-08 09:00:00'),
(5, '活動報名截止提醒', '2025-03-09 10:00:00', '2025-03-09 10:00:00', 5, '2025-03-09 09:00:00', '2025-03-09 09:00:00'),
(5, '活動開始前通知', '2025-03-09 14:00:00', '2025-03-09 14:00:00', 5, '2025-03-09 09:00:00', '2025-03-09 09:00:00'),
(6, '活動報名截止提醒', '2025-03-10 10:00:00', '2025-03-10 10:00:00', 6, '2025-03-10 09:00:00', '2025-03-10 09:00:00'),
(6, '活動開始前通知', '2025-03-10 14:00:00', '2025-03-10 14:00:00', 6, '2025-03-10 09:00:00', '2025-03-10 09:00:00'),
(7, '活動報名截止提醒', '2025-03-11 10:00:00', '2025-03-11 10:00:00', 7, '2025-03-11 09:00:00', '2025-03-11 09:00:00'),
(7, '活動開始前通知', '2025-03-11 14:00:00', '2025-03-11 14:00:00', 7, '2025-03-11 09:00:00', '2025-03-11 09:00:00'),
(8, '活動報名截止提醒', '2025-03-12 10:00:00', '2025-03-12 10:00:00', 8, '2025-03-12 09:00:00', '2025-03-12 09:00:00'),
(8, '活動開始前通知', '2025-03-12 14:00:00', '2025-03-12 14:00:00', 8, '2025-03-12 09:00:00', '2025-03-12 09:00:00'),
(9, '活動報名截止提醒', '2025-03-13 10:00:00', '2025-03-13 10:00:00', 9, '2025-03-13 09:00:00', '2025-03-13 09:00:00'),
(9, '活動開始前通知', '2025-03-13 14:00:00', '2025-03-13 14:00:00', 9, '2025-03-13 09:00:00', '2025-03-13 09:00:00');

-- 插入活動人數
INSERT INTO activity_people_number (vendor_activity_id, max_participants, current_participants) VALUES
(1, 50, 0), (2, 30, 1), (3, 40, 1), (4, 60, 1), (5, 20, 0),
(6, 25, 1), (7, 100, 1), (8, 80, 1), (9, 35, 1), (10, 45, 0),
(11, 30, 1), (12, 50, 1), (13, 60, 0), (14, 40, 1), (15, 20, 1),
(16, 25, 1), (17, 70, 0), (18, 55, 1), (19, 30, 1), (20, 40, 1);

-- 插入認證標籤
INSERT INTO certification_tag (tag_name, keywords) VALUES
('服務優質', '優質,高級,細心,貼心,專業'),
('商品值得信賴', '信賴,放心,可靠,有保障,誠實'),
('顧客滿意', '滿意,好評,回購,信賴,超讚'),
('環境整潔', '整潔,乾淨,舒適,衛生,清爽'),
('快速反應', '快速,即時,馬上,立即,神速'),
('專業態度', '專業,認真,負責,技術,用心'),
('熱情友善', '熱情,友善,親切,溫暖,和善'),
('值得推薦', '推薦,介紹,必買,值得,好評'),
('品質保證', '品質,保證,耐用,高級,精緻'),
('物超所值', '便宜,划算,值得,性價比,優惠');

-- 插入店家認證申請資料
INSERT INTO vendor_certification (vendor_id, certification_status, reason, request_date, approved_date)
VALUES
(1, '申請中', NULL, CURRENT_TIMESTAMP, NULL),
(2, '申請中', NULL, CURRENT_TIMESTAMP, NULL),
(3, '已認證', '顧客評價高，醫療服務專業', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, '申請中', NULL, CURRENT_TIMESTAMP, NULL),
(5, '已認證', '優質服務，顧客反饋良好', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(6, '申請中', NULL, CURRENT_TIMESTAMP, NULL),
(7, '已認證', '顧客評價極高，環境整潔', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(8, '申請中', NULL, CURRENT_TIMESTAMP, NULL),
(9, '已認證', '寵物攝影服務專業，顧客滿意', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(10, '申請中', NULL, CURRENT_TIMESTAMP, NULL);

-- 插入店家認證標語資料
INSERT INTO vendor_certification_tag (certification_id, tag_id, vendor_id, meets_standard)
VALUES
(1, 1, 1, 1), (2, 2, 2, 0), (3, 3, 3, 1), (4, 4, 4, 1), (5, 5, 5, 1),
(6, 6, 6, 1), (7, 7, 7, 1), (8, 8, 8, 0), (9, 9, 9, 1), (10, 10, 10, 0);

-- 插入通知訊息
INSERT INTO notification (member_id, vendor_id, vendor_activity_id, notification_title, notification_content, is_read, sent_time)
VALUES
(11, 1, 1, '活動報名提醒', '您的活動報名即將截止，請儘快完成報名。', 0, CURRENT_TIMESTAMP),
(12, 2, 2, '活動開始通知', '您的活動將在1小時後開始，請準備好。', 0, CURRENT_TIMESTAMP),
(13, 3, 3, '活動報名提醒', '活動報名即將截止，請勿錯過報名時間。', 0, CURRENT_TIMESTAMP),
(14, 4, 4, '活動開始通知', '活動即將開始，請務必準時參加。', 0, CURRENT_TIMESTAMP),
(15, 5, 5, '活動報名提醒', '報名將於今日結束，請確保您的報名已經完成。', 0, CURRENT_TIMESTAMP),
(16, 6, 6, '活動開始通知', '活動開始前的提醒，請確保準時參加。', 0, CURRENT_TIMESTAMP),
(17, 7, 7, '活動報名提醒', '活動即將結束，請儘早報名參加。', 0, CURRENT_TIMESTAMP),
(18, 8, 8, '活動開始通知', '活動即將開始，記得準時出席！', 0, CURRENT_TIMESTAMP),
(19, 9, 9, '活動報名提醒', '報名結束前最後的機會，請儘早報名。', 0, CURRENT_TIMESTAMP),
(20, 10, 10, '活動開始通知', '您的活動即將開始，請不要錯過！', 0, CURRENT_TIMESTAMP);

--------------------------------------------------
-- 2. 店家評價、收藏、活動評價與報名
--------------------------------------------------
-- 店家評論
INSERT INTO vendor_review (vendor_id, member_id, review_time, review_content, rating_environment, rating_price, rating_service)
VALUES
(1, 11, '2025-01-10 00:00:00', '店內環境乾淨，沒有異味，寵物用品種類豐富。', 5, 4, 5),
(2, 12, '2025-01-11 00:00:00', '價格親民，幫狗狗洗澡的美容師很有耐心。', 4, 5, 5),
(3, 13, '2025-01-12 00:00:00', '員工對貓咪很友善，但環境稍微擁擠了些。', 3, 4, 4),
(4, 14, '2025-01-13 00:00:00', '寵物美容技術不錯，但等待時間有點長。', 4, 3, 3),
(5, 15, '2025-01-14 00:00:00', '店員推薦的貓砂很好用，價格合理，會再回購。', 5, 5, 4),
(6, 16, '2025-01-15 00:00:00', '狗狗美容完很可愛，但價格稍貴。', 4, 3, 5),
(7, 17, '2025-01-16 00:00:00', '有很多進口寵物食品，品質很好，值得推薦！', 5, 4, 5),
(8, 18, '2025-01-17 00:00:00', '店內的倉鼠籠選擇多，價格也算合理。', 4, 4, 4),
(9, 19, '2025-01-18 00:00:00', '這間店的貓咪玩具種類多，價格也不貴。', 4, 5, 5),
(10, 20, '2025-01-19 00:00:00', '工作人員專業，但服務態度可以再提升。', 3, 4, 3);

-- 店家評論 ( memberId = 21 )
INSERT INTO vendor_review (vendor_id, member_id, review_time, review_content, rating_environment, rating_price, rating_service)
VALUES
(3, 21, '2025-03-05 14:10:30', '店內環境非常乾淨，貓咪區和狗狗區分開，很貼心！', 5, 4, 5),
(7, 21, '2025-03-12 16:45:20', '價格合理，美容師很專業，狗狗剪毛後看起來超可愛！', 4, 5, 5),
(2, 21, '2025-03-18 10:30:45', '服務態度很好，還提供試吃小點心給寵物，超讚！', 5, 4, 5),
(9, 21, '2025-03-25 18:20:15', '環境稍微擁擠，但用品種類多樣，值得一逛！', 3, 4, 4),
(5, 21, '2025-04-02 12:55:50', '價格偏高，但使用的都是高級用品，品質有保障。', 4, 3, 5);

-- 店家收藏
INSERT INTO vendor_like (member_id, vendor_id)
VALUES
(11, 1),(12, 2),(13, 3),(14, 4),(15, 5),
(16, 6),(17, 7),(18, 8),(19, 9),(20, 10),
(11, 2),(12, 3),(13, 1),(14, 5),(15, 6),
(16, 7),(17, 4),(18, 9),(19, 10),(20, 8);

-- 店家收藏 ( memberId = 21 )
INSERT INTO vendor_like (member_id, vendor_id)
VALUES
(21, 1),(21, 2),(21, 3),(21, 4),(21, 5);

-- 活動收藏
INSERT INTO activity_like (member_id, vendor_activity_id)
VALUES
(11, 3),(12, 5),(13, 8),(14, 2),(15, 10),
(16, 7),(17, 1),(18, 12),(19, 15),(20, 9),
(11, 6),(12, 14),(13, 11),(14, 19),(15, 4),
(16, 16),(17, 20),(18, 17),(19, 13),(20, 18),
(11, 2),(12, 9),(13, 14),(14, 7),(15, 1),
(16, 12),(17, 5),(18, 3),(19, 6),(20, 8),
(11, 17),(12, 13),(13, 16),(14, 20),(15, 18),
(16, 11),(17, 15),(18, 4),(19, 10),(20, 19);

-- 活動收藏 ( memberId = 21 )
INSERT INTO activity_like (member_id, vendor_activity_id)
VALUES
(21, 3),(21, 7),(21, 4),(21, 10),
(21, 11),(21, 19),(21, 6),(21, 8);

-- 活動評論
INSERT INTO vendor_activity_review (vendor_id, member_id, review_time, review_content, vendor_activity_id)
VALUES
(2, 12, '2024-07-15 00:00:00', '寵物活動非常有趣，狗狗玩得很開心！', 5),
(7, 18, '2024-09-22 00:00:00', '工作人員很友善，貓咪也感到很放鬆。', 12),
(4, 15, '2024-11-03 00:00:00', '活動場地很乾淨，適合帶寵物來玩。', 8),
(9, 11, '2024-05-19 00:00:00', '我的兔子第一次參加活動，表現得很活躍。', 3),
(1, 20, '2024-08-30 00:00:00', '活動內容豐富，寵物們都玩得很盡興。', 17),
(6, 14, '2024-12-10 00:00:00', '非常推薦這個活動，主人和寵物都能享受。', 9),
(3, 16, '2024-06-25 00:00:00', '活動安排得很周到，狗狗交到了新朋友。', 14),
(8, 13, '2024-10-05 00:00:00', '貓咪在活動中表現得很勇敢，主人也很開心。', 6),
(5, 19, '2024-04-12 00:00:00', '場地設施完善，寵物們都很安全。', 11),
(10, 17, '2024-03-28 00:00:00', '活動結束後，狗狗回家睡得特別香。', 2);

-- 活動評論 ( memberId = 21 )
INSERT INTO vendor_activity_review (vendor_id, member_id, review_time, review_content, vendor_activity_id)
VALUES
(3, 21, '2025-03-03 15:20:10', '活動安排得很棒，小狗運動會讓毛孩玩得超開心！', 5),
(6, 21, '2025-03-08 10:45:30', '貓咪瑜伽課程很有趣，老師也很專業！', 12),
(4, 21, '2025-03-14 18:10:45', '店家準備了許多免費試吃點心，寵物和主人都很滿意！', 8),
(2, 21, '2025-03-18 14:30:20', '參加寵物攝影活動，攝影師非常有耐心，拍出來的照片超可愛！', 3),
(8, 21, '2025-03-21 09:55:50', '手作寵物玩具課程很實用，下次還會參加！', 15),
(4, 21, '2025-03-25 16:40:15', '狗狗游泳體驗活動很棒，場地乾淨，狗狗玩得很開心！', 7),
(5, 21, '2025-03-29 12:20:05', '寵物講座內容豐富，學到很多新知識！', 10),
(10, 21, '2025-04-02 17:15:30', '狗狗社交日活動很棒，讓毛孩認識了很多新朋友！', 20),
(1, 21, '2025-04-05 11:35:40', '寵物美容示範課程很有幫助，學到不少居家美容技巧。', 2),
(9, 21, '2025-04-09 14:50:25', '戶外寵物健行活動風景超美，狗狗跑得很開心！', 18);

-- 活動報名
INSERT INTO activity_registration (vendor_activity_id, member_id, registration_time, status)
VALUES
(10, 15, '2025-03-01 10:15:30', 'pending'),
(3, 19, '2025-03-02 11:45:10', 'confirmed'),
(17, 11, '2025-03-03 14:30:25', 'pending'),
(8, 16, '2025-03-04 09:20:50', 'confirmed'),
(5, 12, '2025-03-05 16:05:40', 'pending'),
(14, 18, '2025-03-06 08:55:15', 'confirmed'),
(1, 13, '2025-03-07 13:40:20', 'pending'),
(9, 20, '2025-03-08 15:25:55', 'confirmed'),
(12, 14, '2025-03-09 12:10:05', 'pending'),
(6, 17, '2025-03-10 18:50:30', 'confirmed');

-- 活動報名 ( memberId = 21 )
INSERT INTO activity_registration (vendor_activity_id, member_id, registration_time, status)
VALUES
(3, 21, '2025-03-02 10:30:15', 'pending'),
(7, 21, '2025-03-05 14:20:50', 'confirmed'),
(12, 21, '2025-03-10 09:45:30', 'pending'),
(18, 21, '2025-03-14 16:35:10', 'confirmed'),
(5, 21, '2025-03-18 11:50:25', 'pending'),
(15, 21, '2025-03-22 13:25:40', 'confirmed'),
(9, 21, '2025-03-26 17:15:55', 'pending'),
(20, 21, '2025-03-30 12:05:20', 'confirmed');

-- 友善店家 (無vendorId)
INSERT INTO friendly_shop (name, vendor_category_id, address, longitude, latitude)
VALUES
('喵星人樂園', 1, '台北市中山區民生東路200號', 121.5456789, 25.0512345),
('奇幻水族坊', 2, '高雄市左營區博愛一路99號', 120.3123456, 22.6789123),
('寵物時尚館', 3, '新北市永和區中正路500號', 121.5234567, 25.0056789),
('犬貓專業訓練中心', 4, '台南市東區林森路300號', 120.3129876, 22.9786543),
('小動物之家', 5, '桃園市八德區建國路120號', 121.2987654, 24.9654321),
('異寵天地', 1, '新竹市北區北大路99號', 120.9876543, 24.8023456),
('寵物營養專家', 2, '彰化市延平路75號', 120.6543210, 24.0578901),
('狗狗樂園', 3, '基隆市中正區義一路250號', 121.7356789, 25.1334567),
('貓咪咖啡屋', 1, '台中市北屯區松竹路88號', 120.6734567, 24.1765432),
('水族達人', 2, '台南市安平區安平路160號', 120.2456789, 23.0123456);

-- 友善店家 (有vendorId)
INSERT INTO friendly_shop (name, vendor_id, vendor_category_id, address, longitude, latitude)
VALUES
('毛孩天堂寵物美容', 1, 1, '台北市大安區信義路五段100號', 121.565616, 25.0325434),
('汪喵精品寵物用品', 2, 2, '台中市西屯區台灣大道三段200號', 120.6460599, 24.1649589),
('安心動物醫院', 3, 3, '新北市板橋區中山路一段300號', 120.6460599, 24.1649589),
('毛孩樂園寵物寄宿', 4, 4, '高雄市苓雅區成功一路50號', 120.2982766, 22.6131145),
('寵物咖啡館喵喵汪汪', 5, 5, '桃園市中壢區中華路88號', 121.241931, 24.9664219),
('狗狗訓練學院', 6, 6, '新竹市東區光復路200號', 120.9758385, 24.7949863),
('海洋樂趣水族館', 7, 7, '台南市中西區民族路77號', 120.204719, 22.996198),
('爬寵世界', 8, 8, '台北市松山區南京東路100號', 121.5542437, 25.0513848),
('毛小孩攝影館', 9, 9, '台中市南屯區五權西路300號', 120.6477881, 24.1402007),
('手作寵物小物', 10, 10, '彰化市中正路150號', 120.4402128, 24.0566409);

--------------------------------------------------
-- 3. 商品與聊天假資料
--------------------------------------------------
INSERT INTO product_category (name) VALUES
('食品保健'), ('日常用品'), ('服飾'), ('玩具'), ('其他');

INSERT INTO product_color (name) VALUES
('紅'), ('橙'), ('黃'), ('綠'), ('藍'), ('紫'), ('黑'), ('白');

INSERT INTO product_size (name) VALUES
('小'), ('中'), ('大');

INSERT INTO product_detail (product_category_id, name, description) VALUES
(3, '寵物學院風針織背心', '這款學院風寵物針織衣服簡直是冬天寶貝們的時尚救星...'),
(4, '貓咪棉繩玩具球', '擔心主子獨自在家無聊 精力無處發洩 變成拆家小惡魔...'),
(1, '無塵豆腐貓砂', '環保可沖馬桶，強力吸水凝結，持久除臭。'),
(2, '鮮味雞肉凍乾', '高蛋白、低脂肪的凍乾雞肉，適合作為獎勵零食。'),
(1, '營養貓糧', '含有豐富維生素與礦物質，滿足貓咪日常營養需求。'),
(2, '抗菌濕巾', '有效抑制細菌，適合清潔寵物用品與身體。'),
(3, '防水寵物雨衣', '輕便透氣，適合雨天使用。'),
(4, '智能寵物玩具', '多模式互動，讓寵物保持活力。'),
(5, '除臭寵物墊', '長效吸附異味，維持環境清新。'),
(1, '高蛋白牛肉乾', '適合訓練獎勵，提供優質蛋白質。'),
(2, '強力除臭噴霧', '快速分解異味，適用於寵物環境。'),
(3, '夏季透氣寵物衣', '涼爽透氣，防止中暑。'),
(4, '雷射逗貓棒', '互動遊戲，讓貓咪保持活力。'),
(5, '活性炭濾芯', '高效過濾異味和雜質。'),
(1, '鮭魚貓零食', '富含Omega-3，促進皮毛健康。'),
(2, '天然木屑貓砂', '無塵低敏，環保可降解。'),
(3, '防風防水寵物外套', '適合冬季和雨天使用。'),
(5, '寵物智能餵食器', '定時餵食，保持良好飲食習慣。'),
(4, '彈跳寵物球', '內藏零食設計，提高玩樂興趣。');

INSERT INTO product (product_detail_id, product_size_id, product_color_id, stock_quantity, unit_price, discount_price, status, created_time) VALUES
(1, 1, 1, 40, 100, NULL, 1, '2024-01-01 00:00:00'),
(1, 1, 2, 35, 100, 90, 1, '2024-01-01 00:00:00'),
(1, 1, 4, 30, 100, NULL, 1, '2024-01-01 00:00:00'),
(1, 1, 5, 40, 100, NULL, 1, '2024-01-01 00:00:00'),
(1, 1, 6, 35, 100, 90, 1, '2024-01-01 00:00:00'),
(1, 2, 3, 40, 120, 110, 1, '2024-01-01 00:00:00'),
(1, 2, 4, 35, 120, 110, 1, '2024-01-01 00:00:00'),
(1, 2, 5, 30, 120, NULL, 1, '2024-01-01 00:00:00'),
(1, 2, 6, 40, 120, NULL, 1, '2024-01-01 00:00:00'),
(1, 3, 7, 35, 140, 130, 1, '2024-01-01 00:00:00'),
(1, 3, 8, 35, 140, 130, 1, '2024-01-01 00:00:00'),
(2, NULL, 1, 20, 50, NULL, 1, '2024-03-01 00:00:00'),
(2, NULL, 2, 30, 50, NULL, 1, '2024-03-01 00:00:00'),
(2, NULL, 3, 40, 50, NULL, 1, '2024-03-01 00:00:00'),
(2, NULL, 4, 50, 50, NULL, 1, '2024-03-01 00:00:00'),
(2, NULL, 5, 60, 50, NULL, 1, '2024-03-01 00:00:00'),
(3, NULL, NULL, 100, 250, 220, 1, '2024-04-01 00:00:00'),
(4, NULL, NULL, 120, 150, NULL, 1, '2024-04-01 00:00:00'),
(5, NULL, NULL, 50, 200, 150, 1, '2024-04-01 00:00:00'),
(6, NULL, NULL, 300, 250, 230, 1, '2024-04-01 00:00:00'),
(7, NULL, NULL, 50, 600, NULL, 1, '2024-04-01 00:00:00'),
(8, NULL, NULL, 80, 750, NULL, 1, '2024-04-01 00:00:00'),
(9, NULL, NULL, 120, 500, NULL, 1, '2024-08-01 00:00:00'),
(10, NULL, NULL, 110, 300, 280, 1, '2024-08-01 00:00:00'),
(11, NULL, NULL, 230, 450, 400, 1, '2024-08-01 00:00:00'),
(12, NULL, NULL, 35, 550, NULL, 1, '2024-08-01 00:00:00'),
(13, NULL, NULL, 70, 420, 400, 1, '2024-08-01 00:00:00'),
(14, NULL, NULL, 20, 280, 260, 1, '2025-01-01 00:00:00'),
(15, NULL, NULL, 60, 360, 330, 1, '2025-01-01 00:00:00'),
(16, NULL, NULL, 100, 280, 260, 1, '2025-01-01 00:00:00'),
(17, NULL, NULL, 40, 500, NULL, 1, '2025-01-01 00:00:00'),
(18, NULL, NULL, 30, 520, 480, 1, '2025-01-01 00:00:00'),
(19, NULL, NULL, 20, 100, 80, 0, '2025-01-01 00:00:00');

-- 聊天訊息
--INSERT INTO chat_messages (sender_id, receiver_id, content, is_read)
--VALUES (21, 23, '你好', false);

--------------------------------------------------
-- 4. 付款與訂單假資料
--------------------------------------------------
-- 優惠券
INSERT INTO coupons (name, discount_type, discount_value, min_order_value, limit_count, valid_start, valid_end, status) VALUES
('新會員限定優惠50元', 0, 50.00, 200.00, 2, '2025-01-01 00:00:00', '2025-12-31 23:59:59', 1),
('周年慶九折爽爽送', 1, 0.10, 100.00, 10, '2025-01-01 00:00:00', '2025-12-31 23:59:59', 1),
('母親節100元大放送', 0, 100.00, 300.00, 5, '2025-02-01 00:00:00', '2025-06-30 23:59:59', 1);

-- 插入訂單狀態
INSERT INTO order_status (id, name) VALUES (1, '待處理'), (2, '待出貨'), (3, '配送中'), (4, '待收貨'), (5, '已完成'), (6, '已取消');

-- 插入付款狀態
INSERT INTO payment_status (id, name) VALUES (1, '待付款'), (2, '已付款'), (3, '付款失敗');

-- 插入運送方式
INSERT INTO shipping_category (name, shipping_cost, shipping_day) VALUES
('宅配', 50.00, 7),
('快遞', 80.00, 3);

-- 插入付款方式
INSERT INTO payment_category (name) VALUES
('信用卡付款'),
('貨到付款');

-- 會員優惠券
INSERT INTO member_coupon (member_id, coupons_id, usage_count, status) VALUES
(21, 1, 0, 1), (21, 2, 0, 1), (21, 3, 0, 1);

-- 購物車
INSERT INTO cart (member_id, product_id, quantity) VALUES
(21, 12, 2), (21, 7, 3);

-- 訂單明細
INSERT INTO orders (id, member_id, subtotal, coupon_id, discount_amount, shipping_fee, total_amount, order_status_id, created_time, updated_date, note) VALUES
(1, 21, 540.00, NULL, 0.00, 50.00, 590.00, 5, '2025-01-25 15:36:18.973', '2025-03-25 19:11:30.843', NULL),
(2, 21, 570.00, NULL, 0.00, 50.00, 620.00, 5, '2025-01-27 12:11:18.973', '2025-03-25 19:11:30.863', NULL),
(3, 21, 1000.00, 2, 100.00, 50.00, 950.00, 5, '2025-01-28 12:11:18.973', '2025-04-03 13:28:17.237', NULL),
(4, 21, 1170.00, 2, 117.00, 50.00, 1103.00, 5, '2025-01-30 12:11:18.973', '2025-04-03 13:35:53.237', '20250203更改配送資訊'),
(5, 21, 1000.00, NULL, 0.00, 50.00, 1050.00, 5, '2025-02-03 12:11:18.973', '2025-04-03 13:28:17.257', NULL),
(6, 21, 1100.00, 2, 110.00, 50.00, 1040.00, 5, '2025-02-07 12:11:18.973', '2025-04-03 13:28:17.260', NULL),
(7, 21, 1670.00, 3, 100.00, 80.00, 1650.00, 6, '2025-02-10 12:11:18.973', '2025-04-03 13:35:17.707', '消費者於20250214致電取消訂單'),
(9, 21, 1610.00, 3, 100.00, 80.00, 1590.00, 5, '2025-02-20 12:11:18.973', '2025-04-03 14:46:39.980', NULL),
(11, 21, 1960.00, 2, 196.00, 50.00, 1814.00, 5, '2025-03-03 12:11:18.973', '2025-04-03 14:46:39.993', NULL),
(12, 21, 960.00, 2, 96.00, 50.00, 914.00, 5, '2025-03-04 12:11:18.973', '2025-04-03 14:46:39.997', NULL),
(13, 11, 1560.00, 2, 156.00, 80.00, 1484.00, 5, '2025-03-05 12:11:18.973', '2025-04-03 14:46:40.003', NULL),
(14, 15, 1600.00, 2, 160.00, 80.00, 1520.00, 5, '2025-03-11 12:11:18.973', '2025-04-04 10:06:43.790', NULL),
(15, 11, 400.00, 2, 40.00, 50.00, 410.00, 5, '2025-03-06 12:11:18.973', '2025-04-04 10:06:43.807', NULL),
(16, 12, 550.00, NULL, 0.00, 50.00, 600.00, 5, '2025-03-07 12:11:18.973', '2025-04-04 10:20:03.380', NULL),
(17, 16, 400.00, NULL, 0.00, 50.00, 450.00, 5, '2025-03-09 12:11:18.973', '2025-04-04 10:28:16.587', NULL),
(18, 13, 400.00, NULL, 0.00, 50.00, 450.00, 5, '2025-03-09 12:11:18.973', '2025-04-04 10:29:05.760', NULL),
(19, 18, 400.00, NULL, 0.00, 50.00, 450.00, 5, '2025-03-09 12:11:18.973', '2025-04-04 10:29:05.770', NULL),
(20, 12, 230.00, 2, 23.00, 50.00, 257.00, 6, '2025-03-13 12:11:18.973', '2025-04-04 10:10:18.330', '消費者20250313致電取消'),
(21, 14, 1660.00, 3, 100.00, 80.00, 1640.00, 5, '2025-03-17 12:11:18.973', '2025-04-04 10:11:54.377', NULL),
(22, 16, 130.00, NULL, 0.00, 50.00, 180.00, 5, '2025-03-20 12:11:18.973', '2025-04-04 10:29:05.777', NULL),
(23, 16, 670.00, 2, 67.00, 50.00, 653.00, 5, '2025-03-29 12:11:18.973', '2025-04-04 10:14:41.313', NULL),
(24, 18, 20780.00, 1, 50.00, 50.00, 20780.00, 5, '2025-04-01 12:11:18.973', '2025-04-04 10:20:15.027', NULL),
(25, 12, 2550.00, NULL, 0.00, 50.00, 2600.00, 5, '2025-04-02 12:11:18.973', '2025-04-04 10:28:30.127', NULL),
(26, 21, 570.00, 2, 57.00, 50.00, 563.00, 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL);