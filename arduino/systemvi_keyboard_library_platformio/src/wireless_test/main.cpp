#include <Adafruit_NeoPixel.h>
#include "../../lib/Shared/src/keyboard/SystemVIKeyboard.h"
#include "../../lib/Shared/src/keys/NormalKey.h"
#include <WiFi.h>
#include <esp_now.h>

uint8_t receiverMac[] = {
    0xD0,0xCF,0x13,0x09,0x8E,0xF8
};

#define PIN      41
#define NUM_LEDS 61
#define DELAY 3

Adafruit_NeoPixel strip(NUM_LEDS, PIN, NEO_GRB + NEO_KHZ800);

uint32_t wheel(byte pos) {
    pos = 255 - pos;
    float brightness = 1;
    if (pos < 85) {
        return strip.Color((255 - pos * 3)*brightness, 0, (pos * 3)*brightness);
    }
    if (pos < 170) {
        pos -= 85;
        return strip.Color(0, pos * 3*brightness, (255 - pos * 3)*brightness);
    }
    pos -= 170;
    return strip.Color(pos * 3 * brightness, (int)(255 - pos * 3)*brightness, 0);
}

void rainbowCycle(uint8_t wait) {
    static uint16_t j = 0;

    for (int i = 0; i < NUM_LEDS; i++) {
        int color_index = (i * 256 / NUM_LEDS + j) & 255;
        strip.setPixelColor(i, wheel(color_index));
    }

    strip.show();
    j++;
    delay(wait);
}

int rows=5;
int rowPins[] = { 1,2,3,38,45 };

int columns = 14;
int columnPins[14] = {
    4, 5, 6, 7,
    8, 9, 10, 11,
    12, 13, 14, 15,
    16, 17
};
// int columnPins[] = {
//  29, 26, 15, 13,
//  27, 1, 3, 6,
//  7, 8, 9, 10,
//  11, 12
//  };


SystemVIKeyboard *keyboard;

char name[] = "wireless_test";

uint8_t value=0;

void setup() {
    strip.begin();
    strip.show();
    keyboard = new SystemVIKeyboard(
        name,
        columns,
        rows,
        columnPins,
        rowPins,
        false,
        columns,
        rows
    );
    // keyboard->setNormalKeycap(13,0,         (char[]){static_cast<char>(KEY_ESC), '\0', '\0', '\0'}, 0, 0, 0, 0, 0, 0);
    keyboard->setNormalKeycap(12,0,         new  (char[4]){'1','\0','\0','\0'},       1,0,  0,0,    0,0);
    keyboard->setNormalKeycap(11,0,         new  (char[4]){'2','\0','\0','\0'},       2,0,  0,0,    0,0);
    keyboard->setNormalKeycap(10,0,         new  (char[4]){'3','\0','\0','\0'},       3,0,  0,0,    0,0);
    keyboard->setNormalKeycap( 9,0,         new  (char[4]){'4','\0','\0','\0'},       4,0,  0,0,    0,0);
    keyboard->setNormalKeycap( 8,0,         new  (char[4]){'5','\0','\0','\0'},       5,0,  0,0,    0,0);
    keyboard->setNormalKeycap( 7,0,         new  (char[4]){'6','\0','\0','\0'},       6,0,  0,0,    0,0);
    keyboard->setNormalKeycap( 6,0,         new  (char[4]){'7','\0','\0','\0'},       7,0,  0,0,    0,0);
    keyboard->setNormalKeycap( 5,0,         new  (char[4]){'8','\0','\0','\0'},       8,0,  0,0,    0,0);
    keyboard->setNormalKeycap( 4,0,         new  (char[4]){'9','\0','\0','\0'},       9,0,  0,0,    0,0);
    keyboard->setNormalKeycap( 3,0,         new  (char[4]){'0','\0','\0','\0'},      10,0,  0,0,    0,0);
    keyboard->setNormalKeycap( 2,0,         new  (char[4]){'-','\0','\0','\0'},      11,0,  0,0,    0,0);
    keyboard->setNormalKeycap( 1,0,         new  (char[4]){'=','\0','\0','\0'},      12,0,  0,0,    0,0);
    // keyboard->setNormalKeycap( 0,0,         (char[]){static_cast<char>(KEY_BACKSPACE),'\0','\0','\0'},      13,0,  0,0,    0,0);
    //row 1
    // keyboard->setNormalKeycap(13,1,         (char[]){static_cast<char>(KEY_TAB), '\0', '\0', '\0'}, 0, 1, 0, 0, 0, 0);
    keyboard->setNormalKeycap(12,1,         new (char[4]){'q','\0','\0','\0'},       1,1,  0,0,    0,0);
    keyboard->setNormalKeycap(11,1,         new (char[4]){'w','\0','\0','\0'},       2,1,  0,0,    0,0);
    keyboard->setNormalKeycap(10,1,         new (char[4]){'e','\0','\0','\0'},       3,1,  0,0,    0,0);
    keyboard->setNormalKeycap( 9,1,         new (char[4]){'r','\0','\0','\0'},       4,1,  0,0,    0,0);
    keyboard->setNormalKeycap( 8,1,         new (char[4]){'t','\0','\0','\0'},       5,1,  0,0,    0,0);
    keyboard->setNormalKeycap( 7,1,         new (char[4]){'y','\0','\0','\0'},       6,1,  0,0,    0,0);
    keyboard->setNormalKeycap( 6,1,         new (char[4]){'u','\0','\0','\0'},       7,1,  0,0,    0,0);
    keyboard->setNormalKeycap( 5,1,         new (char[4]){'i','\0','\0','\0'},       8,1,  0,0,    0,0);
    keyboard->setNormalKeycap( 4,1,         new (char[4]){'o','\0','\0','\0'},       9,1,  0,0,    0,0);
    keyboard->setNormalKeycap( 3,1,         new (char[4]){'p','\0','\0','\0'},      10,1,  0,0,    0,0);
    keyboard->setNormalKeycap( 2,1,         new (char[4]){'[','\0','\0','\0'},      11,1,  0,0,    0,0);
    keyboard->setNormalKeycap( 1,1,         new (char[4]){']','\0','\0','\0'},      12,1,  0,0,    0,0);
    keyboard->setNormalKeycap( 0,1,         new (char[4]){'\\','\0','\0','\0'},     13,1,  0,0,    0,0);
    //row 2
    // keyboard->setNormalKeycap(13,2,         (char[]){static_cast<char>(KEY_CAPS_LOCK), '\0', '\0', '\0'}, 0, 2, 0, 0, 0, 0);
    keyboard->setNormalKeycap(12,2,        new  (char[4]){'a','\0','\0','\0'},       1,2,  0,0,    0,0);
    keyboard->setNormalKeycap(11,2,        new  (char[4]){'s','\0','\0','\0'},       2,2,  0,0,    0,0);
    keyboard->setNormalKeycap(10,2,        new  (char[4]){'d','\0','\0','\0'},       3,2,  0,0,    0,0);
    keyboard->setNormalKeycap( 9,2,        new  (char[4]){'f','\0','\0','\0'},       4,2,  0,0,    0,0);
    keyboard->setNormalKeycap( 8,2,        new  (char[4]){'g','\0','\0','\0'},       5,2,  0,0,    0,0);
    keyboard->setNormalKeycap( 7,2,        new  (char[4]){'h','\0','\0','\0'},       6,2,  0,0,    0,0);
    keyboard->setNormalKeycap( 6,2,        new  (char[4]){'j','\0','\0','\0'},       7,2,  0,0,    0,0);
    keyboard->setNormalKeycap( 5,2,        new  (char[4]){'k','\0','\0','\0'},       8,2,  0,0,    0,0);
    keyboard->setNormalKeycap( 4,2,        new  (char[4]){'l','\0','\0','\0'},       9,2,  0,0,    0,0);
    keyboard->setNormalKeycap( 3,2,        new  (char[4]){';','\0','\0','\0'},      10,2,  0,0,    0,0);
    keyboard->setNormalKeycap( 2,2,        new  (char[4]){'\'','\0','\0','\0'},      11,2,  0,0,    0,0);
    // keyboard->setNormalKeycap( 0,2,         (char[]){static_cast<char>(KEY_RETURN),'\0','\0','\0'},     13,2,  0,0,    0,0);
    //row 3
    // keyboard->setNormalKeycap(13,3,         (char[]){static_cast<char>(KEY_LEFT_SHIFT), '\0', '\0', '\0'}, 0, 3, 0, 0, 0, 0);
    keyboard->setNormalKeycap(12,3,        new (char[4]){'z','\0','\0','\0'},       1,3,  0,0,    0,0);
    keyboard->setNormalKeycap(11,3,        new (char[4]){'x','\0','\0','\0'},       2,3,  0,0,    0,0);
    keyboard->setNormalKeycap(10,3,        new (char[4]){'c','\0','\0','\0'},       3,3,  0,0,    0,0);
    keyboard->setNormalKeycap( 9,3,        new (char[4]){'v','\0','\0','\0'},       4,3,  0,0,    0,0);
    keyboard->setNormalKeycap( 8,3,        new (char[4]){'b','\0','\0','\0'},       5,3,  0,0,    0,0);
    keyboard->setNormalKeycap( 7,3,        new (char[4]){'n','\0','\0','\0'},       6,3,  0,0,    0,0);
    keyboard->setNormalKeycap( 6,3,        new (char[4]){'m','\0','\0','\0'},       7,3,  0,0,    0,0);
    keyboard->setNormalKeycap( 5,3,        new (char[4]){',','\0','\0','\0'},       8,3,  0,0,    0,0);
    keyboard->setNormalKeycap( 4,3,        new (char[4]){'.','\0','\0','\0'},       9,3,  0,0,    0,0);
    keyboard->setNormalKeycap( 3,3,        new (char[4]){'/','\0','\0','\0'},      10,3,  0,0,    0,0);
    // keyboard->setNormalKeycap( 0,3,         (char[]){static_cast<char>(KEY_RIGHT_SHIFT),'\0','\0','\0'},     13,3,  0,0,    0,0);
    //row 4
    // keyboard->setNormalKeycap(13,4,         (char[]){static_cast<char>(KEY_LEFT_CTRL), '\0', '\0', '\0'},   0,4,  0,0,    0,0);
    // keyboard->setNormalKeycap(12,4,         (char[]){static_cast<char>(KEY_LEFT_GUI),'\0','\0','\0'},       1,4,  0,0,    0,0);
    // keyboard->setNormalKeycap(11,4,         (char[]){static_cast<char>(KEY_LEFT_ALT),'\0','\0','\0'},       2,4,  0,0,    0,0);
    keyboard->setNormalKeycap( 8,4,         new (char[4]){' ','\0','\0','\0'},                                   3,4,  0,0,    0,0);
    // keyboard->setNormalKeycap( 4,4,         (char[]){static_cast<char>(KEY_LEFT_ALT),'\0','\0','\0'},       4,4,  0,0,    0,0);
    // keyboard->setNormalKeycap( 3,4,         (char[]){static_cast<char>(KEY_LEFT_GUI),'\0','\0','\0'},       5,4,  0,0,    0,0);
    // keyboard->setNormalKeycap( 2,4,         (char[]){static_cast<char>(KEY_MENU),'\0','\0','\0'},           6,4,  0,0,    0,0);
    // keyboard->setNormalKeycap( 0,4,         (char[]){static_cast<char>(KEY_LEFT_CTRL),'\0','\0','\0'},      7,4,  0,0,    0,0);

    WiFi.mode(WIFI_STA);
    WiFi.disconnect();

    if (esp_now_init() != ESP_OK) {
        Serial.println("ESP-NOW init failed");
        return;
    }

    esp_now_peer_info_t peer{};
    memcpy(peer.peer_addr, receiverMac, 6);
    peer.channel = 0;        // same channel
    peer.encrypt = false;

    if (esp_now_add_peer(&peer) != ESP_OK) {
        Serial.println("Failed to add peer");
        return;
    }
}

void loop() {
    rainbowCycle(DELAY);
    keyboard->update();
    for (int i=0;i<columns;i++) {
        for (int j=0;j<rows;j++) {
            auto key=keyboard->keys[i][j];
            if (key->pressed) {
                value = ((NormalKey *) key->keys[0])->value;
                esp_err_t result = esp_now_send(receiverMac, &value, 1);

                if (result == ESP_OK) {
                    Serial.print("Sent byte: ");
                    Serial.println(value);
                } else {
                    Serial.println("Send error");
                }
            }
        }
    }
}