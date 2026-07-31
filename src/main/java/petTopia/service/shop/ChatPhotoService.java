package petTopia.service.shop;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import petTopia.dto.shop.request.UploadPhotoRequest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ChatPhotoService {
    private static final String PATH_CHATROOM_PHOTO = "src/main/resources/static/chatRoomPhoto";

    // TODO: Need to fix path
    public List<Map<String, String>> uploadPhoto(UploadPhotoRequest request) throws IOException {
        List<String> base64Images = request.getImage();

        if (base64Images == null) throw new EntityNotFoundException("Base64 image list is null");

        List<Map<String, String>> uploadedImages = new ArrayList<>();

        // 檢查資料夾是否存在
        Path uploadDir = Paths.get(PATH_CHATROOM_PHOTO);
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        for (String base64String : base64Images) {
            if (base64String == null) continue;

            // 解碼 Base64 -> byte[]
            byte[] imageBytes = Base64.getDecoder().decode(base64String.split(",")[1]);

            // 產生唯一檔名
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
            String formattedDate = now.format(formatter);
            String fileName = request.getUserId() + "_" + formattedDate + "_" + UUID.randomUUID().toString().substring(0, 8) + ".jpg";
            Path filePath = uploadDir.resolve(fileName);

            // 儲存圖片
            Files.write(filePath, imageBytes);

            // 準備回傳 URL
            Map<String, String> response = new HashMap<>();
            response.put("url", "/chatRoomPhoto/" + fileName);
            uploadedImages.add(response);
        }

        return uploadedImages;
    }
}
