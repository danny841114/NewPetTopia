package petTopia.dto.shop.response;

import lombok.*;
import petTopia.model.user.Member;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomMemberDto {
    private Integer id;
    private String name;

    public static ChatRoomMemberDto convertToDto(Member member) {
        return ChatRoomMemberDto.builder()
                .id(member.getId())
                .name(member.getName())
                .build();
    }
}
