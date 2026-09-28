package implementor;

import exception.ChannelDeliveryException;

public class EmailAlertChannel implements AlertChannel {
    private final String recipientEmail;

    public EmailAlertChannel(String recipientEmail) {
        this.recipientEmail = recipientEmail;
    }

    @Override
    public void sendAlert(String header, String body) throws ChannelDeliveryException {
        if (recipientEmail == null || !recipientEmail.contains("@")) {
            throw new ChannelDeliveryException("Invalid recipient email: " + recipientEmail);
        }
        System.out.printf("[Email to %s] SUBJECT: %s | BODY: %s%n", recipientEmail, header, body);
    }
}
