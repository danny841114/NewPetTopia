package petTopia.model.shop;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import petTopia.model.user.Admin;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "admin_product_reply")
public class AdminProductReply {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "product_review_id", nullable = false)
    private ProductReview productReview;

    @ManyToOne
    @JoinColumn(name = "admin_id", nullable = false)
    private Admin admin;

    @Column(name = "reply_text", nullable = false)
    private String replyText;

    @Column(name = "reply_time", nullable = false)
    private LocalDateTime replyTime = java.time.LocalDateTime.now();
}
