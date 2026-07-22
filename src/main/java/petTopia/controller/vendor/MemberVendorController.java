package petTopia.controller.vendor;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import petTopia.dto.vendor.VendorLikeDto;
import petTopia.dto.vendor.VendorReviewDto;
import petTopia.service.vendor.VendorLikeService;
import petTopia.service.vendor.VendorReviewService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/vendor")
public class MemberVendorController {
    private final VendorLikeService vendorLikeService;
    private final VendorReviewService vendorReviewService;

    @GetMapping("/member/{memberId}/like")
    public ResponseEntity<List<VendorLikeDto>> getLikeList(@PathVariable Integer memberId) {
        List<VendorLikeDto> likeList = vendorLikeService.findListByMemberId(memberId);
        return ResponseEntity.ok(likeList);
    }

    @GetMapping("/member/{memberId}/review")
    public ResponseEntity<List<VendorReviewDto>> getReviewList(@PathVariable Integer memberId) {
        List<VendorReviewDto> likeList = vendorReviewService.findReviewListByMemberId(memberId);
        return ResponseEntity.ok(likeList);
    }

    @DeleteMapping("/like/{likeId}/delete")
    public ResponseEntity<?> deleteLike(@PathVariable Integer likeId) {
        boolean result = vendorLikeService.deleteByLikeId(likeId);
        return ResponseEntity.ok(result);
    }
}
