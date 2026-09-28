// refined abstraction

package abstraction;

import implementor.AlertChannel;

public class CriticalAlertNotification extends AlertNotification {

    public CriticalAlertNotification(AlertChannel channel) {
        super(channel);
    }

    @Override
    public void emit(String title, String details) {
        String emergencyHeader = "[CRITICAL ALERT] " + title.toUpperCase();
        String emergencyBody = "URGENT ACTION REQUIRED: " + details;
        channel.sendAlert(emergencyHeader, emergencyBody);
    }
}
