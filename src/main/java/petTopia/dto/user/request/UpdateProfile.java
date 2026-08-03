package petTopia.dto.user.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UpdateProfile {
    private String name;
    private String phone;
    private String address;
    private Boolean gender;
    private LocalDate birthdate;
}
