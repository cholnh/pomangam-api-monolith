package kr.nzzi.msa.pmg.pomangamapimonilith.domain.cs.notice.notice.exception;

public class NoticeException extends RuntimeException {
    public NoticeException() {
        super();
    }
    public NoticeException(String message) {
        super(message);
    }
    public NoticeException(String message, Throwable cause) {
        super(message, cause);
    }
    public NoticeException(Throwable cause) {
        super(cause);
    }
}
