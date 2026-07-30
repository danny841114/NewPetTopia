package petTopia.service.shop;

import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import petTopia.model.shop.ChatMessages;
import petTopia.model.shop.ChatPhoto;
import petTopia.repository.shop.ChatPhotoRepository;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ChatPhotoService {
    private final ChatPhotoRepository chatPhotoRepository;

    // 獲取該訊息的所有圖片
    public List<String> getChatPhotos(Integer chatMessagesId) {
        return chatPhotoRepository.findByChatMessagesId(chatMessagesId)
                .stream()
                .map(ChatPhoto::getPhoto)
                .collect(Collectors.toList());
    }

    // 儲存聊天訊息的圖片
    @Transactional
    public ChatPhoto savePhoto(ChatMessages chatMessages, String photo) {
        ChatPhoto chatPhoto = new ChatPhoto();

        chatPhoto.setChatMessages(chatMessages);
        chatPhoto.setPhoto(photo);

        return chatPhotoRepository.save(chatPhoto);
    }
}
