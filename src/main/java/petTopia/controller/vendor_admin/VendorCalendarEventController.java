package petTopia.controller.vendor_admin;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import petTopia.model.vendor.CalendarEvent;
import petTopia.model.vendor.Vendor;
import petTopia.model.vendor.VendorActivity;
import petTopia.repository.vendor.CalendarEventRepository;
import petTopia.repository.vendor.VendorActivityRepository;
import petTopia.repository.vendor.VendorRepository;

@Slf4j
@RequiredArgsConstructor
@RestController
public class VendorCalendarEventController {
    private final VendorRepository vendorRepository;
    private final CalendarEventRepository calendarEventRepository;
    private final VendorActivityRepository vendorActivityRepository;

    @GetMapping("/api/vendor_admin/calendar/{vendorId}")
    public ResponseEntity<?> getCalenderEventsByVendorId(@PathVariable Integer vendorId) {
        Optional<Vendor> optional = vendorRepository.findById(vendorId);

        if (optional.isPresent()) {
            List<CalendarEvent> calender = calendarEventRepository.findByVendorId(vendorId);
            return ResponseEntity.ok(calender);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/api/vendor_admin/calendar/add")
    public ResponseEntity<?> addCalendarEvent(@RequestParam Integer vendorId,
                                              @RequestParam String eventTitle,
                                              @RequestParam("start_time") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") Date startTime,
                                              @RequestParam("end_time") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") Date endTime,
                                              @RequestParam String color) {
        try {
            Vendor vendor = vendorRepository.findById(vendorId)
                    .orElseThrow(() -> new EntityNotFoundException("Vendor not found"));

            CalendarEvent calendarEvent = new CalendarEvent();
            calendarEvent.setEventTitle(eventTitle);
            calendarEvent.setStartTime(startTime);
            calendarEvent.setEndTime(endTime);
            calendarEvent.setVendor(vendor);
            calendarEvent.setCreatedAt(new Date());
            calendarEvent.setUpdatedAt(new Date());
            calendarEvent.setColor(color);
            CalendarEvent savedCalendarEvent = calendarEventRepository.save(calendarEvent);

            return ResponseEntity.status(HttpStatus.CREATED).body(savedCalendarEvent);
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/api/vendor_admin/calendar/update/{id}")
    public ResponseEntity<?> updateCalendar(@PathVariable Integer id,
                                            @RequestParam(required = false) String eventTitle,
                                            @RequestParam("start_time") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") Date startTime,
                                            @RequestParam("end_time") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") Date endTime,
                                            @RequestParam(required = false) String color) {
        Optional<CalendarEvent> calendarOpt = calendarEventRepository.findByEventId(id);
        if (calendarOpt.isPresent()) {
            CalendarEvent calendarEvent = calendarOpt.get();

            calendarEvent.setEventTitle(eventTitle);
            calendarEvent.setStartTime(startTime);
            calendarEvent.setEndTime(endTime);
            calendarEvent.setColor(color);
            calendarEvent.setUpdatedAt(new Date());

            calendarEventRepository.save(calendarEvent);

            if (calendarEvent.getVendorActivity() != null) {
                Integer activityId = calendarOpt.get().getVendorActivity().getId();
                Optional<CalendarEvent> calendarOpt2 = calendarEventRepository.findByVendorActivityId(activityId);
                if (calendarOpt2.isPresent()) {
                    Optional<VendorActivity> vendorActivityOpt = vendorActivityRepository.findById(activityId);

                    if (vendorActivityOpt.isPresent()) {
                        VendorActivity vendorActivity = vendorActivityOpt.get();

                        vendorActivity.setName(eventTitle);
                        vendorActivity.setStartTime(startTime);
                        vendorActivity.setEndTime(endTime);

                        vendorActivityRepository.save(vendorActivity);
                    }
                }
            }

            return ResponseEntity.ok(calendarEvent);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/api/vendor_admin/calendar/delete/{id}")
    public ResponseEntity<?> deleteCalendar(@PathVariable Integer id) {
        Optional<CalendarEvent> calendarOpt = calendarEventRepository.findByEventId(id);

        if (calendarOpt.isPresent()) {
            CalendarEvent calendarEvent = calendarOpt.get();
            calendarEventRepository.delete(calendarEvent);
            return ResponseEntity.ok(calendarEvent);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
