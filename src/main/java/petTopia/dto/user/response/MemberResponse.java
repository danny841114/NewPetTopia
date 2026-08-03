package petTopia.dto.user.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import petTopia.model.user.Member;
import petTopia.model.user.User;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MemberResponse {
    private Integer id;
    private String email;
    private Boolean emailVerified;
    private String name;
    private String phone;
    private LocalDate birthdate;
    private String address;
    private Boolean gender;
    private Boolean status;
    private LocalDateTime updatedDate;
    private String provider;
    private String userRole;

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

    public static MemberResponse getMemberData(User user, Member member){
        return MemberResponse.builder()
                .id(user.getId())
                .name(member.getName())
                .phone(member.getPhone())
                .gender(member.getGender())
                .address(member.getAddress())
                .birthdate(member.getBirthdate())
                .status(member.getStatus())
                .updatedDate(member.getUpdatedDate())
                .email(user.getEmail())
                .provider(user.getProvider().toString())
                .userRole(user.getUserRole().toString())
                .build();
    }
}
