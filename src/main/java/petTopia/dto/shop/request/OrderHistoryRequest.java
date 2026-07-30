package petTopia.dto.shop.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

@Getter
@Setter
public class OrderHistoryRequest {
    private String memberId;
    private String orderStatus;
    private String orderId;
    private String paymentStatus;
    private String keyword; // similar with productKeyword
    private String productKeyword;
    private String paymentCategory;
    private String shippingCategory;
    private Integer page = 0;
    private Integer size = 10; // 5 or 10

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endDate;
}
