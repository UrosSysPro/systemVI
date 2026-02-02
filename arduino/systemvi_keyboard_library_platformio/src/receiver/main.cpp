#include <WiFi.h>
#include <esp_now.h>
#include <esp_wifi.h>
#include <Arduino.h>
#include "USB.h"
#include "USBHIDKeyboard.h"
#include "../../lib/Shared/src/esp_now/EspNowMessage.h"

USBHIDKeyboard Keyboard;

bool hasEspNowMessage = false;
EspNowMessage espNowMessage;

void onReceive(const uint8_t* senderAddress, const uint8_t* data, int len) {
    memcpy(espNowMessage.data, data, len);
    memcpy(espNowMessage.senderMacAddress, senderAddress, 6);
    hasEspNowMessage = true;
}

void setup() {
    // setCpuFrequencyMhz(80);
    Serial.begin(9600);
    Keyboard.begin();
    USB.begin();

    WiFi.mode(WIFI_STA);
    WiFi.setSleep(true);
    esp_wifi_set_ps(WIFI_PS_MIN_MODEM);
    esp_wifi_set_max_tx_power(20);
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
    delay(2);

    if (hasEspNowMessage) {
        hasEspNowMessage=false;
        bool pressed = espNowMessage.data[0];
        char key = espNowMessage.data[1];
        if (pressed) {
            Serial.printf("Pressed %c", key);
            Keyboard.press(key);
        }
        else {
            Serial.printf("Released %c", key);
            Keyboard.release(key);
        }
        // char cmd = espNowMessage.data[0];
        // switch (cmd) {
        //     case 'u': break;
        //     default:{}break;
        // }
    }
}
