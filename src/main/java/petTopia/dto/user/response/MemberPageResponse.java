package petTopia.dto.user.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberPageResponse {
    private List<UserDetail> content;
    private Integer totalElements;
    private Integer totalPages;
    private Integer currentPage;
}
