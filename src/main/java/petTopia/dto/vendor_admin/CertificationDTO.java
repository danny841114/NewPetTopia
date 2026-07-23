package petTopia.dto.vendor_admin;

import java.util.Date;
import java.util.List;

import lombok.*;
import petTopia.model.vendor.Vendor;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CertificationDTO {
    private Vendor vendor;
    private Integer certificationId;
    private String certificationStatus;
    private String reason;
    private Date requestDate;
    private Date approvedDate;
    private List<CertificationTagDTO> certificationTags;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CertificationTagDTO {
        private String tagName;
        private boolean meetsStandard;
    }
}
