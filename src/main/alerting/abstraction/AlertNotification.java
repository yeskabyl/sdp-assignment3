// abstraction
package abstraction;

import implementor.AlertChannel;

public abstract class AlertNotification {
    protected final AlertChannel channel;

    protected AlertNotification(AlertChannel channel) {
        if (channel == null) {
            throw new IllegalArgumentException("Channel must not be null");
        }
        this.channel = channel;
    }

    public abstract void emit(String title, String details);
}
