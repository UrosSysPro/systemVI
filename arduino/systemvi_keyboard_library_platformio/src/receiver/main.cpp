#include <WiFi.h>
#include <esp_now.h>
#include <esp_wifi.h>
#include <Arduino.h>
#include "USB.h"
#include "USBHIDKeyboard.h"
#include "../../lib/Shared/src/keys/Key.h"

USBHIDKeyboard Keyboard;

volatile bool pressed = false;
volatile char justClicked = 0;

void onReceive(const uint8_t* senderAddress, const uint8_t* data, int len) {
    if (len == 2) {
        pressed = data[0];
        justClicked = data[1];
    }
}

void setup() {
    setCpuFrequencyMhz(80);
    Serial.begin(9600);
    Keyboard.begin();
    USB.begin();

    WiFi.mode(WIFI_STA);
    WiFi.setSleep(true);
    esp_wifi_set_ps(WIFI_PS_MIN_MODEM);
    esp_wifi_set_max_tx_power(44);
    WiFi.disconnect();


    Serial.print("Receiver MAC: ");
    Serial.println(WiFi.macAddress());

    if (esp_now_init() != ESP_OK) {
        Serial.println("ESP-NOW init failed");
        return;
    }
    esp_now_register_recv_cb(onReceive);
}


void loop() {
    delay(10);

    if (justClicked) {
        if (pressed) {
            Serial.printf("Pressed %c", justClicked);
            Keyboard.press(justClicked);
        }
        else {
            Serial.printf("Released %c", justClicked);
            Keyboard.release(justClicked);
        }

        justClicked = 0;
    }
}
