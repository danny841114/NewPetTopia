package petTopia.dto.vendor_admin;

import lombok.*;
import petTopia.model.user.Member;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberDTO {
    private Integer id;
    private String name;
    private String phone;
    private LocalDate birthdate;
    private Boolean gender;

    public static MemberDTO fromEntity(Member member) {
        return MemberDTO.builder()
                .id(member.getId())
                .name(member.getName())
                .phone(member.getPhone())
                .birthdate(member.getBirthdate())
                .gender(member.getGender())
                .build();
    }
}
