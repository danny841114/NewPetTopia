package petTopia.repository.vendor;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import petTopia.model.vendor.CalendarEvent;

public interface CalendarEventRepository extends JpaRepository<CalendarEvent, Integer> {
    Optional<CalendarEvent> findByVendorActivityId(Integer vendorActivityId);

    List<CalendarEvent> findByVendorId(Integer vendorId);

    Optional<CalendarEvent> findByEventId(Integer id);

    void deleteByVendorActivityId(Integer id);
}
