package petTopia.util;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

public class HeadersUtil {
    public static HttpHeaders createHeadersWithMediaTypeJpg() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        return headers;
    }
}
