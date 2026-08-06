package petTopia.controller.vendor_admin;

import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import petTopia.dto.vendor_admin.request.AddCalendarEventRequest;
import petTopia.dto.vendor_admin.request.UpdateCalendarEventRequest;
import petTopia.model.vendor.CalendarEvent;
import petTopia.service.vendor_admin.VendorCalendarEventService;

@Slf4j
@RequiredArgsConstructor
@RestController
public class VendorCalendarEventController {
    private final VendorCalendarEventService vendorCalendarEventService;

    @GetMapping("/api/vendor_admin/calendar/{vendorId}")
    public ResponseEntity<?> getCalenderEvents(@PathVariable Integer vendorId) {
        List<CalendarEvent> calenderEvents = vendorCalendarEventService.getCalenderEventsByVendorId(vendorId);
        return ResponseEntity.ok(calenderEvents);
    }

    // TODO: 2026-08-06 Modify API spec, front-end not fixed
    //  REQUEST PARAM TO BODY
    //  Unify parameters naming to camel case
    //  CREATED need to guide to source
    @PostMapping("/api/vendor_admin/calendar/add")
    public ResponseEntity<CalendarEvent> addCalendarEvent(@RequestBody AddCalendarEventRequest request) {
        CalendarEvent calendarEvent = vendorCalendarEventService.addCalendarEvent(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(calendarEvent);
    }

    // TODO: 2026-08-06 Modify API spec, front-end not fixed
    //  REQUEST PARAM TO BODY
    //  Unify parameters naming to camel case
    @PutMapping("/api/vendor_admin/calendar/update/{id}")
    public ResponseEntity<CalendarEvent> updateCalendar(@PathVariable Integer id,
                                                        @RequestBody UpdateCalendarEventRequest request) {
        CalendarEvent calendarEvent = vendorCalendarEventService.updateCalendar(id, request);
        return ResponseEntity.ok(calendarEvent);
    }

    @DeleteMapping("/api/vendor_admin/calendar/delete/{id}")
    public ResponseEntity<Void> deleteCalendar(@PathVariable Integer id) {
        vendorCalendarEventService.deleteCalendarById(id);
        return ResponseEntity.noContent().build();
    }
}
