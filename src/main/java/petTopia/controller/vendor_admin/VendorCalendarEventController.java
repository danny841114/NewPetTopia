package petTopia.controller.vendor_admin;

import java.util.Date;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
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

    // TODO: REQUEST PARAM TO BODY
    @PostMapping("/api/vendor_admin/calendar/add")
    public ResponseEntity<?> addCalendarEvent(@RequestParam Integer vendorId,
                                              @RequestParam String eventTitle,
                                              @RequestParam("start_time") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") Date startTime,
                                              @RequestParam("end_time") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") Date endTime,
                                              @RequestParam String color) {
        AddCalendarEventRequest request = new AddCalendarEventRequest();

        request.setVendorId(vendorId);
        request.setEventTitle(eventTitle);
        request.setStartTime(startTime);
        request.setEndTime(endTime);
        request.setColor(color);

        CalendarEvent calendarEvent = vendorCalendarEventService.addCalendarEvent(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(calendarEvent);
    }

    // TODO: REQUEST PARAM TO BODY
    @PutMapping("/api/vendor_admin/calendar/update/{id}")
    public ResponseEntity<?> updateCalendar(@PathVariable Integer id,
                                            @RequestParam(required = false) String eventTitle,
                                            @RequestParam("start_time") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") Date startTime,
                                            @RequestParam("end_time") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") Date endTime,
                                            @RequestParam(required = false) String color) {
        UpdateCalendarEventRequest request = new UpdateCalendarEventRequest();

        request.setEventTitle(eventTitle);
        request.setStartTime(startTime);
        request.setEndTime(endTime);
        request.setColor(color);

        CalendarEvent calendarEvent = vendorCalendarEventService.updateCalendar(id, request);
        return ResponseEntity.ok(calendarEvent);
    }

    @DeleteMapping("/api/vendor_admin/calendar/delete/{id}")
    public ResponseEntity<?> deleteCalendar(@PathVariable Integer id) {
        vendorCalendarEventService.deleteCalendarById(id);
        return ResponseEntity.noContent().build();
    }
}
