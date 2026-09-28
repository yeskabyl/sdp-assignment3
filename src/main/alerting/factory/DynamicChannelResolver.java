package factory;

import implementor.AlertChannel;
import implementor.EmailAlertChannel;
import implementor.LegacyPagerAdapter;
import implementor.TelegramAlertChannel;
import legacy.LegacyPagerService;

public class DynamicChannelResolver {
    private final LegacyPagerService legacyPagerService;

    public DynamicChannelResolver(LegacyPagerService legacyPagerService) {
        this.legacyPagerService = legacyPagerService;
    }

    public AlertChannel resolve(int severityScore, boolean isOfflineZone) {
        if (isOfflineZone || severityScore >= 9) {
            return new LegacyPagerAdapter(legacyPagerService, 101, "HW-PAGER-99");
        } else if (severityScore >= 5) {
            return new TelegramAlertChannel("BOT_SECURE_TOKEN_XYZ");
        } else {
            return new EmailAlertChannel("operations@example.com");
        }
    }
}
