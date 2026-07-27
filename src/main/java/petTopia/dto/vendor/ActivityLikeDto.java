package petTopia.dto.vendor;

import java.util.Base64;

import lombok.*;
import petTopia.model.vendor.ActivityLike;
import petTopia.util.ImageConverter;

import static petTopia.constant.ImageUrl.LOGO_IMG_URL_PREFIX;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityLikeDto {
    /* 評價資訊 */
    private Integer id;
    private Integer vendorId;
    private Integer activityId;

    /* 會員資訊 */
    private Integer memberId;
    private String name;
    private boolean gender;
    private byte[] profilePhoto;
    private String profilePhotoBase64;
    private String profilePhotoUrl;

    /* 設定圖片之Base64 */
    public void setProfilePhoto(byte[] profilePhoto) {
        if (profilePhoto != null) {
            String mimeType = ImageConverter.getMimeType(profilePhoto);
            this.profilePhotoBase64 = "data:%s;base64,".formatted(mimeType)
                    + Base64.getEncoder().encodeToString(profilePhoto);
        }
        this.profilePhoto = profilePhoto;
    }

    public static ActivityLikeDto fromEntity(ActivityLike like) {
        if (like == null || like.getVendorActivity() == null || like.getMember() == null) return null;

        String imgUrl = LOGO_IMG_URL_PREFIX.replace("{id}", String.valueOf(like.getMember().getId()));

        return ActivityLikeDto.builder()
                .id(like.getId())
                .vendorId(like.getVendorActivity().getVendor().getId())
                .activityId(like.getVendorActivity().getId())
                .memberId(like.getMember().getId())
                .name(like.getMember().getName())
                .gender(like.getMember().getGender())
                .profilePhotoUrl(imgUrl)
                .build();
    }
}
