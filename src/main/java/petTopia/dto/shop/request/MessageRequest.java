package petTopia.dto.shop.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MessageRequest {
    private Integer senderId;
    private Integer receiverId;
    private String content;
    private String sendTime;
    private List<String> photos;
}
