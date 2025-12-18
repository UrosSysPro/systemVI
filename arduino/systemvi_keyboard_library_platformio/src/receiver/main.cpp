#include <WiFi.h>
#include <esp_now.h>

void onReceive(const uint8_t *info,
               const uint8_t *data, int len) {
    if (len == 1) {
        Serial.print("Received byte: ");
        Serial.println(data[0]);
    }
}

void setup() {
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
}

void loop() {
    delay(1000);
    // nothing
}
