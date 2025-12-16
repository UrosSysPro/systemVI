// #include <Adafruit_NeoPixel.h>
//
// #define PIN      18
// #define NUM_LEDS 61
// #define DELAYVAL 20
//
// Adafruit_NeoPixel strip(NUM_LEDS, PIN, NEO_GRB + NEO_KHZ800);
//
// uint32_t wheel(byte pos) {
//     pos = 255 - pos;
//     float brightness = 0.5;
//     if (pos < 85) {
//         return strip.Color((255 - pos * 3)*brightness, 0, (pos * 3)*brightness);
//     }
//     if (pos < 170) {
//         pos -= 85;
//         return strip.Color(0, pos * 3*brightness, (255 - pos * 3)*brightness);
//     }
//     pos -= 170;
//     return strip.Color(pos * 3 * brightness, (int)(255 - pos * 3)*brightness, 0);
// }
//
// void rainbowCycle(uint8_t wait) {
//     static uint16_t j = 0;
//
//     for (int i = 0; i < NUM_LEDS; i++) {
//         int color_index = (i * 256 / NUM_LEDS + j) & 255;
//         strip.setPixelColor(i, wheel(color_index));
//     }
//
//     strip.show();
//     j++;
//     delay(wait);
// }
//
// void setup() {
//     strip.begin();
//     strip.show();
// }
//
// void loop() {
//     rainbowCycle(3);
// }

#include "Arduino.h"

int pin=1;

void setup() {
    pinMode(pin,OUTPUT);
    digitalWrite(pin,1);
}

void loop() {
    digitalWrite(pin,1);
    sleep(100);
}