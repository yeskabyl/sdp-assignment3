// refined abstraction

package abstraction;

import implementor.AlertChannel;

public class DigestAlertNotification extends AlertNotification {

    public DigestAlertNotification(AlertChannel channel) {
        super(channel);
    }

    @Override
    public void emit(String title, String details) {
        String compactHeader = "[DIGEST] " + title;
        String compactBody = details != null && details.length() > 50
                ? details.substring(0, 47) + "..."
                : details;
        channel.sendAlert(compactHeader, compactBody);
    }
}
