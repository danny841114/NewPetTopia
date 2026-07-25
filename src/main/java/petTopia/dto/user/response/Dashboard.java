package petTopia.dto.user.response;

import lombok.*;
import petTopia.model.user.User;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Dashboard {
    private List<User> members;
    private List<User> vendors;
    private AdminInfo adminInfo;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AdminInfo{
        private String id;
        private String email;
        private String role;
    }
}
