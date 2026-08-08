package petTopia.model.vendor;

import java.util.Date;
import java.util.List;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.minidev.json.annotate.JsonIgnore;
import petTopia.model.user.Member;

@Entity
@Table(name = "vendor_review")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VendorReview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "vendor_id", nullable = false)
    private Vendor vendor;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(name = "review_time", nullable = false)
    private Date reviewTime;

    @Column(name = "review_content")
    private String reviewContent;

    @Column(name = "rating_environment")
    private Integer ratingEnvironment;

    @Column(name = "rating_price")
    private Integer ratingPrice;

    @Column(name = "rating_service")
    private Integer ratingService;

    @JsonIgnore
    @OneToMany(mappedBy = "vendorReview", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ReviewPhoto> reviewPhotos;
}
