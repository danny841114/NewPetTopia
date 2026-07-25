package petTopia.dto.user.response;

import lombok.*;
import petTopia.model.user.Member;
import petTopia.model.user.User;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberResponse {
    private Integer id;
    private String email;
    private Boolean emailVerified;
    private String name;
    private String phone;
    private LocalDate birthdate;
    private String address;

    public static MemberResponse fromEntity(User user, Member member) {
        return MemberResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .emailVerified(user.isEmailVerified())
                .name(member.getName())
                .phone(member.getPhone())
                .birthdate(member.getBirthdate())
                .address(member.getAddress())
                .build();
    }
}
