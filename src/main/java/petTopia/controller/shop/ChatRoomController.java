package petTopia.controller.shop;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import petTopia.dto.shop.ChatMessagesDto;
import petTopia.dto.shop.request.MessageRequest;
import petTopia.dto.shop.request.UploadPhotoRequest;
import petTopia.dto.shop.response.ChatRoomMemberDto;
import petTopia.service.shop.ChatMessagesService;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/chatRoom")
@RestController
public class ChatRoomController {
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessagesService chatMessagesService;

    private static final String PATH_CHATROOM_PHOTO = "src/main/resources/static/chatRoomPhoto";

    // 客戶端發送至 `/app/send`
    @MessageMapping("/send")
    public ResponseEntity<ChatMessagesDto> sendMessage(@Payload MessageRequest message) {
        ChatMessagesDto chatMessagesDto = chatMessagesService.sendMessage(message);

        // **發送給發送者**
        messagingTemplate.convertAndSend("/topic/messages/" + message.getSenderId(), chatMessagesDto);

        // **發送給接收者**
        messagingTemplate.convertAndSend("/topic/messages/" + message.getReceiverId(), chatMessagesDto);

        return ResponseEntity.ok(chatMessagesDto);
    }

    // 後台聊天室 => 獲取所有聊天用戶
    @GetMapping("/api/getChatUsers")
    public ResponseEntity<?> getChatUsers(@RequestParam Integer senderId) {
        List<ChatRoomMemberDto> chatUsers = chatMessagesService.getChatUsers(senderId);

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("chatUsers", chatUsers);

        return ResponseEntity.ok(responseBody);
    }

    // 前台&後台聊天室 => 獲取歷史訊息
    @GetMapping("/api/getChatMessagesHistory")
    public ResponseEntity<?> getChatMessagesHistory(@RequestParam Integer senderId,
                                                    @RequestParam Integer receiverId) {
        List<ChatMessagesDto> chatMessagesDtos = chatMessagesService.getChatMessages(senderId, receiverId);

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("chatMessagesHistory", chatMessagesDtos);

        return ResponseEntity.ok(responseBody);
    }

    // 上傳圖片
    @PostMapping("/api/uploadPhoto")
    public ResponseEntity<?> uploadPhoto(@RequestBody UploadPhotoRequest request) {
        try {
            String userId = request.getUserId();
            List<String> base64Images = request.getImage();
            List<Map<String, String>> uploadedImages = new ArrayList<>();

            // 檢查資料夾是否存在
            Path uploadDir = Paths.get(PATH_CHATROOM_PHOTO);
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            for (String base64String : base64Images) {
                // 解碼 Base64 -> byte[]
                byte[] imageBytes = Base64.getDecoder().decode(base64String.split(",")[1]);

                // 產生唯一檔名
                LocalDateTime now = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
                String formattedDate = now.format(formatter);
                String fileName = userId + "_" + formattedDate + "_" + UUID.randomUUID().toString().substring(0, 8) + ".jpg";
                Path filePath = uploadDir.resolve(fileName);

                // 儲存圖片
                Files.write(filePath, imageBytes);

                // 準備回傳 URL
                Map<String, String> response = new HashMap<>();
                response.put("url", "/chatRoomPhoto/" + fileName);
                uploadedImages.add(response);
            }

            return ResponseEntity.ok(uploadedImages);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
