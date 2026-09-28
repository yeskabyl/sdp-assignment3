package legacy;

import java.nio.charset.StandardCharsets;

public class LegacyPagerService {

    public static final int STATUS_OK = 0;
    public static final int STATUS_AUTH_FAILED = -1;
    public static final int STATUS_PAYLOAD_TOO_LARGE = -2;
    public static final int STATUS_DEVICE_OFFLINE = -3;

    public int transmitRawBuzzer(int pagerPinCode, byte[] rawPayload, String deviceHardwareId) {
        if (!"HW-PAGER-99".equals(deviceHardwareId)) {
            return STATUS_AUTH_FAILED;
        }
        if (rawPayload == null || rawPayload.length > 64) {
            return STATUS_PAYLOAD_TOO_LARGE;
        }
        if (pagerPinCode <= 0) {
            return STATUS_DEVICE_OFFLINE;
        }

        String decoded = new String(rawPayload, StandardCharsets.US_ASCII);
        System.out.printf("[Legacy Pager Device: %s, PIN: %d] BEEP! %s%n", deviceHardwareId, pagerPinCode, decoded);
        return STATUS_OK;
    }
}
