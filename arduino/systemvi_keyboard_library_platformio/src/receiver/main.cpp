#include <WiFi.h>
#include <esp_now.h>
#include "Adafruit_TinyUSB.h"

Adafruit_USBD_HID usb_hid;

uint8_t const desc_hid_report[] = {
    TUD_HID_REPORT_DESC_KEYBOARD()
};

// For keycode definition check out https://github.com/hathach/tinyusb/blob/master/src/class/hid/hid.h
uint8_t hidcode[] = {HID_KEY_0, HID_KEY_1, HID_KEY_2, HID_KEY_3};

void onReceive(const uint8_t *senderMacAddress,const uint8_t *data, int len) {
    if (len == 1) {
        Serial.print("Received byte: ");
        Serial.println(data[0]);
    }
}

void process_hid() {
    // used to avoid send multiple consecutive zero report for keyboard
    static bool keyPressedPreviously = false;

    uint8_t count = 0;
    uint8_t keycode[6] = {0};

    // scan normal key and send report
    for (uint8_t i = 0; i < pincount; i++) {
        if (activeState == digitalRead(pins[i])) {
            // if pin is active (low), add its hid code to key report
            keycode[count++] = hidcode[i];

            // 6 is max keycode per report
            if (count == 6) break;
        }
    }

    if (TinyUSBDevice.suspended() && count) {
        // Wake up host if we are in suspend mode
        // and REMOTE_WAKEUP feature is enabled by host
        TinyUSBDevice.remoteWakeup();
    }

    // skip if hid is not ready e.g still transferring previous report
    if (!usb_hid.ready()) return;

    if (count) {
        // Send report if there is key pressed
        uint8_t const report_id = 0;
        uint8_t const modifier = 0;

        keyPressedPreviously = true;
        usb_hid.keyboardReport(report_id, modifier, keycode);
    } else {
        // Send All-zero report to indicate there is no keys pressed
        // Most of the time, it is, though we don't need to send zero report
        // every loop(), only a key is pressed in previous loop()
        if (keyPressedPreviously) {
            keyPressedPreviously = false;
            usb_hid.keyboardRelease(0);
        }
    }
}

void hid_report_callback(uint8_t report_id, hid_report_type_t report_type, uint8_t const* buffer, uint16_t bufsize) {
    (void) report_id;
    (void) bufsize;

    // LED indicator is output report with only 1 byte length
    if (report_type != HID_REPORT_TYPE_OUTPUT) return;

    // The LED bit map is as follows: (also defined by KEYBOARD_LED_* )
    // Kana (4) | Compose (3) | ScrollLock (2) | CapsLock (1) | Numlock (0)
    uint8_t ledIndicator = buffer[0];

#ifdef LED_BUILTIN
    // turn on LED if capslock is set
    digitalWrite(LED_BUILTIN, ledIndicator & KEYBOARD_LED_CAPSLOCK);
#endif
}

void setup(){
    //esp now
    Serial.begin(9600);
    delay(1000);

    WiFi.mode(WIFI_STA);
    WiFi.disconnect();

    Serial.print("Receiver MAC: ");
    Serial.println(WiFi.macAddress());

    if (esp_now_init() != ESP_OK) {
        Serial.println("ESP-NOW init failed");
        return;
    }

    esp_now_register_recv_cb(onReceive);

    //tiny usb
    if (!TinyUSBDevice.isInitialized()) {
        TinyUSBDevice.begin(0);
    }

    // Setup HID
    usb_hid.setBootProtocol(HID_ITF_PROTOCOL_KEYBOARD);
    usb_hid.setPollInterval(2);
    usb_hid.setReportDescriptor(desc_hid_report, sizeof(desc_hid_report));
    usb_hid.setStringDescriptor("TinyUSB Keyboard");

    // Set up output report (on control endpoint) for Capslock indicator
    usb_hid.setReportCallback(NULL, hid_report_callback);

    usb_hid.begin();

    // If already enumerated, additional class driverr begin() e.g msc, hid, midi won't take effect until re-enumeration
    if (TinyUSBDevice.mounted()){
        TinyUSBDevice.detach();
        delay(10);
        TinyUSBDevice.attach();
    }
}

void loop() {
    delay(1000);
    // nothing
#ifdef TINYUSB_NEED_POLLING_TASK
    // Manual call tud_task since it isn't called by Core's background
    TinyUSBDevice.task();
#endif

    // not enumerated()/mounted() yet: nothing to do
    if (!TinyUSBDevice.mounted()) {
        return;
    }

    // poll gpio once each 2 ms
    static uint32_t ms = 0;
    if (millis() - ms > 2) {
        ms = millis();
        process_hid();
    }
}
