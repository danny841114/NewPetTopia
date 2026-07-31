package petTopia.dto.shop;

import java.util.Date;
import java.util.List;

import lombok.*;
import petTopia.model.shop.ChatMessages;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessagesDto {
    private Integer id;
    private Integer senderId;
    private Integer receiverId;
    private String content;
    private Boolean isRead;
    private Date sendTime;
    private List<byte[]> photos;

    public static ChatMessagesDto convertToDto(ChatMessages chatMessages, List<byte[]> photos) {
        return ChatMessagesDto.builder()
                .id(chatMessages.getId())
                .senderId(chatMessages.getSender().getId())
                .receiverId(chatMessages.getReceiver().getId())
                .content(chatMessages.getContent())
                .isRead(chatMessages.getIsRead())
                .sendTime(chatMessages.getSendTime())
                .photos(photos)
                .build();
    }
}
