package implementor;

import exception.ChannelDeliveryException;

public class TelegramAlertChannel implements AlertChannel {
    private final String botToken;

    public TelegramAlertChannel(String botToken) {
        this.botToken = botToken;
    }

    @Override
    public void sendAlert(String header, String body) throws ChannelDeliveryException {
        if (body == null || body.isBlank()) {
            throw new ChannelDeliveryException("Telegram message body cannot be empty");
        }
        System.out.printf("[Telegram Bot %s] %s: %s%n", botToken, header, body);
    }
}
