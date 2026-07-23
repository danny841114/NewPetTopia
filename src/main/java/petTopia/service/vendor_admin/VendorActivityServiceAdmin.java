package petTopia.service.vendor_admin;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import petTopia.dto.vendor_admin.TopActivityDTO;
import petTopia.model.vendor.VendorActivity;
import petTopia.repository.vendor.CalendarEventRepository;
import petTopia.repository.vendor.VendorActivityRepository;
import petTopia.repository.vendor_admin.ActivityRegistrationRepository;
import petTopia.repository.vendor_admin.VendorActivityImagesRepository;
import org.springframework.jdbc.core.JdbcTemplate;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class VendorActivityServiceAdmin {
    private final VendorActivityRepository vendorActivityRepository;
    private final VendorActivityImagesRepository vendorActivityImagesRepository;
    private final CalendarEventRepository calendarEventRepository;
    private final ActivityRegistrationRepository activityRegistrationRepository;
    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public VendorActivity saveVendorActivity(VendorActivity vendorActivity) {
        return vendorActivityRepository.save(vendorActivity);
    }

    public List<VendorActivity> getAllVendorActivities() {
        return vendorActivityRepository.findAll();
    }

    public List<VendorActivity> getVendorActivityByVendorId(Integer vendorId) {
        return vendorActivityRepository.findByVendorId(vendorId);
    }

    @Transactional
    public void deleteVendorActivity(Integer id) {
        calendarEventRepository.deleteByVendorActivityId(id);
        vendorActivityRepository.deleteById(id);
    }

    @Transactional
    public void addActivity(VendorActivity activity) {
        vendorActivityRepository.save(activity);
    }

    public Optional<VendorActivity> getVendorActivityById(Integer id) {
        return vendorActivityRepository.findById(id);
    }

    public List<TopActivityDTO> getTop5Activities() {
        Pageable pageable = PageRequest.of(0, 5);
        return activityRegistrationRepository.findTop5Activities(pageable);
    }

    // TODO: move to repository layer
    public boolean checkTimeConflictDetail(Integer vendorId, Integer activityId, String startTime, String endTime) throws DataAccessException {
        String query = "SELECT COUNT(*) FROM vendor_activity WHERE vendor_id = ? AND id != ? AND (" +
                "(start_time BETWEEN ? AND ?) " +
                "OR (end_time BETWEEN ? AND ?) " +
                "OR (? BETWEEN start_time AND end_time) " +
                "OR (? BETWEEN start_time AND end_time))";

        int count = 0;

        try {
            count = jdbcTemplate.queryForObject(query, Integer.class, vendorId, activityId, startTime, endTime, startTime, endTime, startTime, endTime);
        } catch (DataAccessException e) {
            log.error("Check time conflict detail failed", e);
        }

        return count > 0;
    }

    // TODO: move to repository layer
    public boolean checkTimeConflict(Integer vendorId, String startTime, String endTime) throws DataAccessException {
        String query = "SELECT COUNT(*) FROM vendor_activity WHERE vendor_id = ? AND (start_time BETWEEN ? AND ?) OR (end_time BETWEEN ? AND ?) OR (? BETWEEN start_time AND end_time) OR (? BETWEEN start_time AND end_time)";

        int count = 0;

        try {
            count = jdbcTemplate.queryForObject(query, Integer.class, vendorId, startTime, endTime, startTime, endTime, startTime, endTime);

        } catch (DataAccessException e) {
            log.error("Check time conflict failed", e);
        }

        return count > 0;
    }
}
