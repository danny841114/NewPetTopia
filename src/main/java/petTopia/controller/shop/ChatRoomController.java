package petTopia.controller.shop;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import petTopia.service.shop.ChatPhotoService;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/chatRoom")
@RestController
public class ChatRoomController {
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessagesService chatMessagesService;
    private final ChatPhotoService chatPhotoService;

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
    public ResponseEntity<?> uploadPhoto(@RequestBody UploadPhotoRequest request) throws IOException {
        List<Map<String, String>> uploadedImages = chatPhotoService.uploadPhoto(request);
        return ResponseEntity.ok(uploadedImages);
    }
}
