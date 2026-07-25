package petTopia.dto.user.response;

import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberPageResponse {
    private List<Map<String, Object>> content;
    private Integer totalElements;
    private Integer totalPages;
    private Integer currentPage;
}
