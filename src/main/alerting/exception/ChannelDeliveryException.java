package exception;

public class ChannelDeliveryException extends RuntimeException {
    public ChannelDeliveryException(String message) {
        super(message);
    }

    public ChannelDeliveryException(String message, Throwable cause) {
        super(message, cause);
    }
}