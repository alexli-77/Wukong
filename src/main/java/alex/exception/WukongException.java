package alex.exception;

import alex.common.ErrorReason;

public class WukongException extends RuntimeException{
    private ErrorReason error;

    protected WukongException() {
    }

    public WukongException(String message) {
        super(message);
        this.error = ErrorReason.internalError(message);
    }

    public WukongException(Throwable cause) {
        super(cause);
    }

    public WukongException(ErrorReason error) {
        super(error.getReason());
        this.error = error;
    }

    public int status() {
        return error.getStatus();
    }
}
