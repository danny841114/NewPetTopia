package petTopia.dto.user.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BatchUpdateMembersRequest {
    private List<Integer> memberIds;
    private String action;
}
