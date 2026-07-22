package petTopia.service.vendor;

import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import petTopia.model.vendor.VendorCategory;
import petTopia.repository.vendor.VendorCategoryRepository;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class VendorCategoryService {
    private final VendorCategoryRepository vendorCategoryRepository;

    public List<VendorCategory> findAllVendorCategoriesWithVendors() {
        return this.findAllVendorCategories()
                .stream()
                .filter(c -> !c.getVendors().isEmpty())
                .collect(Collectors.toList());
    }

    public List<VendorCategory> findAllVendorCategories() {
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        return vendorCategoryRepository.findAll(sort);
    }
}
