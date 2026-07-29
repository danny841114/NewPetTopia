package petTopia.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import petTopia.dto.admin.DashboardStats;
import petTopia.service.dashboard.DashboardService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admin/dashboard")
public class DashboardController {
    private final DashboardService dashboardService;

    @GetMapping("/stats")
    public ResponseEntity<?> getDashboardStats() {
        DashboardStats dashboardStats = dashboardService.getCurrentCounts();
        return ResponseEntity.ok(dashboardStats);
    }
}