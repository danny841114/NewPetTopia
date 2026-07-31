package petTopia.service.shop;

import java.io.IOException;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import petTopia.dto.shop.ChatMessagesDto;
import petTopia.dto.shop.request.MessageRequest;
import petTopia.dto.shop.response.ChatRoomMemberDto;
import petTopia.model.shop.ChatMessages;
import petTopia.model.shop.ChatPhoto;
import petTopia.model.user.Member;
import petTopia.repository.shop.ChatMessagesRepository;
import petTopia.repository.shop.ChatPhotoRepository;
import petTopia.repository.user.MemberRepository;
import petTopia.repository.user.UserRepository;
import petTopia.util.ImageConverter;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ChatMessagesService {
    private final ChatMessagesRepository chatMessagesRepository;
    private final UserRepository userRepository;
    private final ChatPhotoRepository chatPhotoRepository;
    private final MemberRepository memberRepository;

    private static final String PATH = "src/main/resources/static";

    @Transactional
    public ChatMessagesDto sendMessage(MessageRequest message) {
        ChatMessages savedMessage = this.saveMessage(message);

        List<String> urlPhotos = message.getPhotos();
        List<byte[]> bytePhotos = new ArrayList<>();

        if (urlPhotos != null && !urlPhotos.isEmpty()) {
            for (String photo : urlPhotos) {
                ChatPhoto savedPhoto = this.savePhoto(savedMessage, photo);

                try {
                    byte[] bytePhoto = ImageConverter.convertUrlToByteArray(PATH + savedPhoto.getPhoto());
                    bytePhotos.add(bytePhoto);
                } catch (IOException e) {
                    log.error("Add photo into chat message failed", e);
                }
            }
        }

        return ChatMessagesDto.convertToDto(savedMessage, bytePhotos);
    }

    public List<ChatRoomMemberDto> getChatUsers(@NonNull Integer senderId) {
        List<ChatRoomMemberDto> chatUsers = new ArrayList<>();

        List<Integer> chatUserIds = chatMessagesRepository.findDistinctChatUserIds(senderId);

        if (chatUserIds != null && !chatUserIds.isEmpty()) {
            List<Member> members = memberRepository.findAllById(chatUserIds);

            for (Member member : members) {
                chatUsers.add(ChatRoomMemberDto.convertToDto(member));
            }
        }

        return chatUsers;
    }

    public List<ChatMessagesDto> getChatMessages(Integer senderId, Integer receiverId) {
        List<ChatMessagesDto> chatMessagesDtos = new ArrayList<>();

        List<ChatMessages> chatMessagesHistory = this.getChatMessagesHistory(senderId, receiverId);

        for (ChatMessages chatMessages : chatMessagesHistory) {
            List<byte[]> bytePhotos = new ArrayList<>();

            List<String> urlPhotos = this.getChatPhotos(chatMessages.getId());

            for (String photo : urlPhotos) {
                try {
                    byte[] bytePhoto = ImageConverter.convertUrlToByteArray(PATH + photo);
                    bytePhotos.add(bytePhoto);
                } catch (IOException e) {
                    log.error("Add photo into chat message failed", e);
                }
            }

            chatMessagesDtos.add(ChatMessagesDto.convertToDto(chatMessages, bytePhotos));
        }

        return chatMessagesDtos;
    }

    private ChatMessages saveMessage(MessageRequest message) {
        ChatMessages chatMessages = new ChatMessages();

        Date parsedDate = Date.from(Instant.parse(message.getSendTime()));

        userRepository.findById(message.getSenderId())
                .ifPresent(chatMessages::setSender);
        userRepository.findById(message.getReceiverId())
                .ifPresent(chatMessages::setReceiver);
        chatMessages.setContent(message.getContent());
        chatMessages.setSendTime(parsedDate);
        chatMessages.setIsRead(false);

        return chatMessagesRepository.save(chatMessages);
    }

    private ChatPhoto savePhoto(ChatMessages chatMessages, String photo) {
        ChatPhoto chatPhoto = new ChatPhoto();

        chatPhoto.setChatMessages(chatMessages);
        chatPhoto.setPhoto(photo);

        return chatPhotoRepository.save(chatPhoto);
    }

    private List<ChatMessages> getChatMessagesHistory(Integer senderId, Integer receiverId) {
        List<ChatMessages> senderMessages = chatMessagesRepository.findBySenderIdAndReceiverIdOrderByIdAsc(senderId, receiverId);
        List<ChatMessages> receiverMessages = chatMessagesRepository.findBySenderIdAndReceiverIdOrderByIdAsc(receiverId, senderId);

        List<ChatMessages> allMessages = new ArrayList<>();
        allMessages.addAll(senderMessages);
        allMessages.addAll(receiverMessages);

        allMessages.sort(Comparator.comparing(ChatMessages::getId));
        return allMessages;
    }

    private List<String> getChatPhotos(Integer chatMessagesId) {
        return chatPhotoRepository.findByChatMessagesId(chatMessagesId)
                .stream()
                .map(ChatPhoto::getPhoto)
                .collect(Collectors.toList());
    }
}
