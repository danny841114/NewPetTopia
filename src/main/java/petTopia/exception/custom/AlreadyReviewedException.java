package petTopia.exception.custom;

import java.io.Serial;

public class AlreadyReviewedException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public AlreadyReviewedException(String message) {
        super(message);
    }
}
