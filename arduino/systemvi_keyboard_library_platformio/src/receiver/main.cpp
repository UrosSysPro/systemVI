#include <WiFi.h>
#include <esp_now.h>
#include <esp_wifi.h>
#include <Arduino.h>
#include "USB.h"
#include "USBHIDKeyboard.h"

USBHIDKeyboard Keyboard;

volatile char justClicked = 0;

void onReceive(const uint8_t *senderAddress, const uint8_t *data, int len) {
    if (len == 1) {
        justClicked = data[0];
    }
}

void setup() {
    setCpuFrequencyMhz(80);
    Serial.begin(9600);

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

    Keyboard.begin();
    USB.begin();
}


void loop() {
    delay(10);

    if (justClicked) {
        Serial.print("Just clicked: ");
        Serial.println(justClicked);

        Keyboard.write(justClicked);

        justClicked = 0;
    }
}
