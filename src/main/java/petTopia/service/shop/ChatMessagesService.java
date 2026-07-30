package petTopia.service.shop;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import petTopia.model.shop.ChatMessages;
import petTopia.repository.shop.ChatMessagesRepository;
import petTopia.repository.user.UserRepository;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ChatMessagesService {
    private final ChatMessagesRepository chatMessagesRepository;
    private final UserRepository userRepository;

    // 管理員獲取所有聊天用戶ID
    public List<Integer> getChatUsers(Integer senderId) {
        return chatMessagesRepository.findDistinctChatUserIds(senderId);
    }

    // 使用者獲取歷史聊天訊息
    public List<ChatMessages> getChatMessagesHistory(Integer senderId, Integer receiverId) {
        List<ChatMessages> senderMessages = chatMessagesRepository.findBySenderIdAndReceiverIdOrderByIdAsc(senderId, receiverId);
        List<ChatMessages> receiverMessages = chatMessagesRepository.findBySenderIdAndReceiverIdOrderByIdAsc(receiverId, senderId);

        List<ChatMessages> allMessages = new ArrayList<>();
        allMessages.addAll(senderMessages);
        allMessages.addAll(receiverMessages);

        allMessages.sort(Comparator.comparing(ChatMessages::getId));
        return allMessages;
    }

    // 儲存輸入的聊天訊息 回傳該訊息
    @Transactional
    public ChatMessages saveMessage(Integer senderId, Integer receiverId, String content, Date sendTime) {
        ChatMessages chatMessages = new ChatMessages();

        userRepository.findById(senderId)
                .ifPresent(chatMessages::setSender);

        userRepository.findById(receiverId)
                .ifPresent(chatMessages::setReceiver);

        chatMessages.setContent(content);
        chatMessages.setSendTime(sendTime);
        chatMessages.setIsRead(false);

        return chatMessagesRepository.save(chatMessages);
    }
}
