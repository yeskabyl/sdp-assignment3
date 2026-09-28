package implementor;

import exception.ChannelDeliveryException;

public interface AlertChannel {
    void sendAlert(String header, String body) throws ChannelDeliveryException;
}
