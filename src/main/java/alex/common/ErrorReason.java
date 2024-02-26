package alex.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ErrorReason {
    private int status;
    private String type;
    private String reason;
    /**
     * 500 internal error
     */
    private static final String TYPE_INTERNAL_SERVER_ERROR = "internal_server_error";
    private static final String UNKNOWN_ERROR_REASON = "unknown server error";
    public static final ErrorReason INTERNAL_ERROR = new ErrorReason(Status.INTERNAL_SERVER_ERROR, TYPE_INTERNAL_SERVER_ERROR, UNKNOWN_ERROR_REASON);

    public static ErrorReason internalError(String msg) {
        return new ErrorReason(Status.INTERNAL_SERVER_ERROR, TYPE_INTERNAL_SERVER_ERROR, msg);
    }
}
