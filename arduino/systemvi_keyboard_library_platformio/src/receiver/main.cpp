#include <WiFi.h>
#include <esp_now.h>
#include <esp_wifi.h>
#include <Arduino.h>
#include "Adafruit_TinyUSB.h"

Adafruit_USBD_HID usb_hid;

uint8_t const desc_hid_report[] = {
    TUD_HID_REPORT_DESC_KEYBOARD()
};

volatile char justClicked;

void onReceive(const uint8_t *senderAddress, const uint8_t *data, int len) {
    if (len == 1) {
        justClicked = data[0];
    }
}

void hid_report_callback(uint8_t report_id, hid_report_type_t report_type, uint8_t const* buffer, uint16_t bufsize) {

}

void setup() {
    // setCpuFrequencyMhz(80);
    Serial.begin(9600);
    delay(3000);

    WiFi.mode(WIFI_STA);
    WiFi.setSleep(true);
    esp_wifi_set_ps(WIFI_PS_MIN_MODEM);
    WiFi.disconnect();

    Serial.print("Receiver MAC: ");
    Serial.println(WiFi.macAddress());

    if (esp_now_init() != ESP_OK) {
        Serial.println("ESP-NOW init failed");
        return;
    }

    esp_now_register_recv_cb(onReceive);

    if (!TinyUSBDevice.isInitialized()) {
        TinyUSBDevice.begin(0);
    }
    usb_hid.setBootProtocol(HID_ITF_PROTOCOL_KEYBOARD);
    usb_hid.setPollInterval(2);
    usb_hid.setReportDescriptor(desc_hid_report, sizeof(desc_hid_report));
    usb_hid.setStringDescriptor("TinyUSB Keyboard");
    usb_hid.setReportCallback(nullptr,hid_report_callback);
    usb_hid.begin();
    // if (TinyUSBDevice.mounted()){
    //     TinyUSBDevice.detach();
    //     delay(10);
    //     TinyUSBDevice.attach();
    // }
}


void loop() {
    delay(10);
#ifdef TINYUSB_NEED_POLLING_TASK
    TinyUSBDevice.task();
#endif

    if (justClicked) {
        if (TinyUSBDevice.mounted()&&usb_hid.ready()) {
            uint8_t keycode[6] = { HID_KEY_A, 0, 0, 0, 0, 0 };
            usb_hid.keyboardReport(0, 0, keycode);
            delay(100);
            usb_hid.keyboardRelease(0);
        }else {
            Serial.print("TinyUSBDevice.mounted(): ");
            Serial.println(TinyUSBDevice.mounted());
            Serial.print("usb_hid.ready(): ");
            Serial.println(usb_hid.ready());
        }

        Serial.print("Just clicked: ");
        Serial.println(justClicked);
        justClicked = 0;
    }
}
