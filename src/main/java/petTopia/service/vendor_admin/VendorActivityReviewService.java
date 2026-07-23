package petTopia.service.vendor_admin;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;
import petTopia.model.vendor.VendorActivityReview;
import petTopia.repository.vendor.VendorActivityReviewRepository;

import java.util.List;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class VendorActivityReviewService {
    private final VendorActivityReviewRepository vendorActivityReviewRepository;

    public List<VendorActivityReview> getReviewsByActivityId(@RequestParam Integer activityId) {
        return vendorActivityReviewRepository.findByVendorActivityId(activityId);
    }

    @Transactional
    public void deleteReviewById(Integer reviewId) {
        vendorActivityReviewRepository.findById(reviewId)
                .ifPresent(vendorActivityReviewRepository::delete);
    }
}
