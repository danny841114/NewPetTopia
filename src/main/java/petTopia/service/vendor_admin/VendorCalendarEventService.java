package petTopia.service.vendor_admin;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import petTopia.dto.vendor_admin.request.AddCalendarEventRequest;
import petTopia.dto.vendor_admin.request.UpdateCalendarEventRequest;
import petTopia.model.vendor.CalendarEvent;
import petTopia.model.vendor.Vendor;
import petTopia.model.vendor.VendorActivity;
import petTopia.repository.vendor.CalendarEventRepository;
import petTopia.repository.vendor.VendorActivityRepository;
import petTopia.repository.vendor.VendorRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class VendorCalendarEventService {
    private final VendorRepository vendorRepository;
    private final CalendarEventRepository calendarEventRepository;
    private final VendorActivityRepository vendorActivityRepository;

    public List<CalendarEvent> getCalenderEventsByVendorId(Integer vendorId) {
        vendorRepository.findById(vendorId)
                .orElseThrow(() -> new EntityNotFoundException("Vendor not found"));

        return calendarEventRepository.findByVendorId(vendorId);
    }

    @Transactional
    public CalendarEvent addCalendarEvent(AddCalendarEventRequest request) {
        Vendor vendor = vendorRepository.findById(request.getVendorId())
                .orElseThrow(() -> new EntityNotFoundException("Vendor not found"));

        CalendarEvent calendarEvent = new CalendarEvent();

        calendarEvent.setEventTitle(request.getEventTitle());
        calendarEvent.setStartTime(request.getStartTime());
        calendarEvent.setEndTime(request.getEndTime());
        calendarEvent.setVendor(vendor);
        calendarEvent.setCreatedAt(new Date());
        calendarEvent.setUpdatedAt(new Date());
        calendarEvent.setColor(request.getColor());

        return calendarEventRepository.save(calendarEvent);
    }

    // TODO: Logic should be reconfirmed
    @Transactional
    public CalendarEvent updateCalendar(Integer eventId, UpdateCalendarEventRequest request) {
        CalendarEvent calendarEvent = calendarEventRepository.findByEventId(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Calendar event not found"));

        calendarEvent.setEventTitle(request.getEventTitle());
        calendarEvent.setStartTime(request.getStartTime());
        calendarEvent.setEndTime(request.getEndTime());
        calendarEvent.setColor(request.getColor());
        calendarEvent.setUpdatedAt(new Date());

        CalendarEvent savedCalendarEvent = calendarEventRepository.save(calendarEvent);

        // TODO: NO IDEA
        if (calendarEvent.getVendorActivity() != null) {
            Integer activityId = calendarEvent.getVendorActivity().getId();

            Optional<CalendarEvent> calendarOpt = calendarEventRepository.findByVendorActivityId(activityId);

            if (calendarOpt.isPresent()) {
                Optional<VendorActivity> vendorActivityOpt = vendorActivityRepository.findById(activityId);

                if (vendorActivityOpt.isPresent()) {
                    VendorActivity vendorActivity = vendorActivityOpt.get();

                    vendorActivity.setName(request.getEventTitle());
                    vendorActivity.setStartTime(request.getStartTime());
                    vendorActivity.setEndTime(request.getEndTime());

                    vendorActivityRepository.save(vendorActivity);
                }
            }
        }

        return savedCalendarEvent;
    }

    @Transactional
    public void deleteCalendarById(Integer calendarId) {
        calendarEventRepository.findByEventId(calendarId)
                .ifPresent(calendarEventRepository::delete);
    }
}
