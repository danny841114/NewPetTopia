package petTopia.dto.user.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateMemberRequest {
    private String name;
    private String phone;
    private String address;
    private String birthdate;
    private Boolean emailVerified;
}
