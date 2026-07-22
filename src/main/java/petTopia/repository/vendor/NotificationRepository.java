package petTopia.repository.vendor;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import petTopia.model.vendor.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    // 根據店家ID查詢通知
    List<Notification> findByVendorId(int vendorId);

    // 根據活動ID查詢通知
    List<Notification> findByVendorActivityId(int vendorActivityId);

    List<Notification> findByMemberId(Integer memberId);

    void deleteByMemberId(Integer memberId);
}

