package alerting;

import abstraction.CriticalAlertNotification;
import abstraction.DigestAlertNotification;
import exception.ChannelDeliveryException;
import factory.DynamicChannelResolver;
import implementor.AlertChannel;
import implementor.EmailAlertChannel;
import implementor.LegacyPagerAdapter;
import legacy.LegacyPagerService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class SystemTest {

    @Test
    @DisplayName("CriticalAlertNotification correctly formats and delegates to Implementor")
    void testCriticalAlertDelegation() {
        AlertChannel mockChannel = mock(AlertChannel.class);
        CriticalAlertNotification criticalAlert = new CriticalAlertNotification(mockChannel);

        criticalAlert.emit("Server Overheat", "Rack 4 temperature 90C");

        verify(mockChannel, times(1)).sendAlert(
                "[CRITICAL ALERT] SERVER OVERHEAT",
                "URGENT ACTION REQUIRED: Rack 4 temperature 90C"
        );
    }

    @Test
    @DisplayName("DigestAlertNotification truncates long details and delegates")
    void testDigestAlertDelegation() {
        AlertChannel mockChannel = mock(AlertChannel.class);
        DigestAlertNotification digestAlert = new DigestAlertNotification(mockChannel);

        String longText = "This is a very long text exceeding fifty characters boundary limits definitely.";
        digestAlert.emit("Status", longText);

        ArgumentCaptor<String> bodyCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockChannel).sendAlert(eq("[DIGEST] Status"), bodyCaptor.capture());

        assertTrue(bodyCaptor.getValue().endsWith("..."));
        assertEquals(50, bodyCaptor.getValue().length());
    }

    @Test
    @DisplayName("LegacyPagerAdapter translates successful status code 0 cleanly")
    void testAdapterSuccessTranslation() {
        // Создаем тестовый stub без капризов Mockito для классов
        LegacyPagerService stubPager = new LegacyPagerService() {
            @Override
            public int transmitRawBuzzer(int pagerPinCode, byte[] rawPayload, String deviceHardwareId) {
                return LegacyPagerService.STATUS_OK;
            }
        };

        LegacyPagerAdapter adapter = new LegacyPagerAdapter(stubPager, 101, "HW-PAGER-99");
        assertDoesNotThrow(() -> adapter.sendAlert("FIRE", "Zone A"));
    }

    @Test
    @DisplayName("LegacyPagerAdapter translates status -2 to ChannelDeliveryException")
    void testAdapterPayloadTooLargeTranslation() {
        LegacyPagerService stubPager = new LegacyPagerService() {
            @Override
            public int transmitRawBuzzer(int pagerPinCode, byte[] rawPayload, String deviceHardwareId) {
                return LegacyPagerService.STATUS_PAYLOAD_TOO_LARGE;
            }
        };

        LegacyPagerAdapter adapter = new LegacyPagerAdapter(stubPager, 101, "HW-PAGER-99");

        ChannelDeliveryException ex = assertThrows(ChannelDeliveryException.class,
                () -> adapter.sendAlert("ERROR", "Payload size test"));

        assertTrue(ex.getMessage().contains("hardware buffer limit"));
    }

    @Test
    @DisplayName("LegacyPagerAdapter translates status -1 auth failure to ChannelDeliveryException")
    void testAdapterAuthFailureTranslation() {
        LegacyPagerService stubPager = new LegacyPagerService() {
            @Override
            public int transmitRawBuzzer(int pagerPinCode, byte[] rawPayload, String deviceHardwareId) {
                return LegacyPagerService.STATUS_AUTH_FAILED;
            }
        };

        LegacyPagerAdapter adapter = new LegacyPagerAdapter(stubPager, 101, "BAD-HW");

        ChannelDeliveryException ex = assertThrows(ChannelDeliveryException.class,
                () -> adapter.sendAlert("ERROR", "Msg"));

        assertTrue(ex.getMessage().contains("Hardware authentication rejected"));
    }

    @Test
    @DisplayName("DynamicChannelResolver selects LegacyPagerAdapter for severe situations")
    void testDynamicSelectionModule() {
        LegacyPagerService dummyPager = new LegacyPagerService();
        DynamicChannelResolver resolver = new DynamicChannelResolver(dummyPager);

        AlertChannel highSeverity = resolver.resolve(10, false);
        AlertChannel offlineZone = resolver.resolve(2, true);
        AlertChannel lowSeverity = resolver.resolve(2, false);

        assertInstanceOf(LegacyPagerAdapter.class, highSeverity);
        assertInstanceOf(LegacyPagerAdapter.class, offlineZone);
        assertInstanceOf(EmailAlertChannel.class, lowSeverity);
    }
}