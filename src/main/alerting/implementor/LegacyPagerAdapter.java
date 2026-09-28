package implementor;

import exception.ChannelDeliveryException;
import legacy.LegacyPagerService;

import java.nio.charset.StandardCharsets;

public class LegacyPagerAdapter implements AlertChannel {
    private final LegacyPagerService legacyPagerService;
    private final int pagerPinCode;
    private final String deviceHardwareId;

    public LegacyPagerAdapter(LegacyPagerService legacyPagerService, int pagerPinCode, String deviceHardwareId) {
        this.legacyPagerService = legacyPagerService;
        this.pagerPinCode = pagerPinCode;
        this.deviceHardwareId = deviceHardwareId;
    }

    @Override
    public void sendAlert(String header, String body) throws ChannelDeliveryException {
        String combined = (header + ": " + (body != null ? body : "")).trim();
        byte[] rawBytes = combined.getBytes(StandardCharsets.US_ASCII);

        int statusCode;
        try {
            statusCode = legacyPagerService.transmitRawBuzzer(pagerPinCode, rawBytes, deviceHardwareId);
        } catch (Exception e) {
            throw new ChannelDeliveryException("Unexpected failure communicating with hardware pager", e);
        }

        switch (statusCode) {
            case LegacyPagerService.STATUS_OK:
                return;
            case LegacyPagerService.STATUS_AUTH_FAILED:
                throw new ChannelDeliveryException("Hardware authentication rejected device ID: " + deviceHardwareId);
            case LegacyPagerService.STATUS_PAYLOAD_TOO_LARGE:
                throw new ChannelDeliveryException("Alert exceeds 64-byte hardware buffer limit (" + rawBytes.length + " bytes)");
            case LegacyPagerService.STATUS_DEVICE_OFFLINE:
                throw new ChannelDeliveryException("Pager hardware is unreachable for PIN: " + pagerPinCode);
            default:
                throw new ChannelDeliveryException("Unknown hardware pager failure code: " + statusCode);
        }
    }
}