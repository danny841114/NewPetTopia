package petTopia.model.shop;

import java.io.Serial;
import java.io.Serializable;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@EqualsAndHashCode
@Getter
@Setter
@NoArgsConstructor
@Embeddable
public class MemberCouponId implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Integer memberId;
    private Integer couponId;
}
