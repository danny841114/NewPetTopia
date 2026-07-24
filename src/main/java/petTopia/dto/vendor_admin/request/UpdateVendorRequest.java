package petTopia.dto.vendor_admin.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class UpdateVendorRequest {
    private String vendorName;
    private String contactEmail;
    private String vendorPhone;
    private String vendorAddress;
    private String vendorDescription;
    private String contactPerson;
    private String vendorTaxIdNumber;
    private Integer categoryId;
    private MultipartFile vendorLogoImg;
    private MultipartFile[] files;
    private List<Integer> deletedImageIds;
}
