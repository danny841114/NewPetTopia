package petTopia.dto.user.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDetail {
    private Integer id;
    private String email;
    private Boolean emailVerified;
    private String name;
    private String phone;
    private LocalDateTime updatedDate;
}
