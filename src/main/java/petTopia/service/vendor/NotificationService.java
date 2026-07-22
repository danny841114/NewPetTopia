package petTopia.service.vendor;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import petTopia.model.vendor.Notification;
import petTopia.repository.vendor.NotificationRepository;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;

    // 取得會員的所有通知
    public List<Notification> getNotificationsByMember(Integer memberId) {
        return notificationRepository.findByMemberId(memberId);
    }

    // 標記通知為已讀
    @Transactional
    public void markNotificationAsRead(Integer id) {
        notificationRepository.findById(id).ifPresent(notification -> {
            notification.setIsRead(true);
            notificationRepository.save(notification);
        });
    }

    // 清除會員的所有通知
    @Transactional
    public void clearNotificationsByMember(Integer memberId) {
        notificationRepository.deleteByMemberId(memberId);
    }
}
