package kr.nzzi.msa.pmg.pomangamapimonilith.domain.order.order.exception;

public class OrderException extends RuntimeException {
    public OrderException() {
        super();
    }
    public OrderException(String message) {
        super(message);
    }
    public OrderException(String message, Throwable cause) {
        super(message, cause);
    }
    public OrderException(Throwable cause) {
        super(cause);
    }
}
