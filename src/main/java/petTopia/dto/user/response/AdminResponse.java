package petTopia.dto.user.response;

import lombok.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import petTopia.model.user.Admin;
import petTopia.model.user.User;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminResponse {
    private String email;
    private Integer adminId;
    private String role;
    private Boolean isAuthenticated;
    private List<SimpleGrantedAuthority> authorities;
    private AdminInfo adminInfo;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AdminInfo {
        private String name;
        private Admin.AdminRole role;
        private LocalDateTime registrationDate;
    }

    public static AdminResponse fromEntity(User user, Admin admin) {
        AdminInfo adminInfo = AdminInfo.builder()
                .name(admin.getName())
                .role(admin.getRole())
                .registrationDate(admin.getRegistrationDate())
                .build();

        return AdminResponse.builder()
                .email(user.getEmail())
                .adminId(admin.getId())
                .role("ADMIN")
                .isAuthenticated(true)
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .adminInfo(adminInfo)
                .build();
    }
}
