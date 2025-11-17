#include <Adafruit_NeoPixel.h>
#include "../../lib/Shared/src/keyboard/SystemVIKeyboard.h"

#define PIN       28
#define NUMPIXELS 61
Adafruit_NeoPixel strip(NUMPIXELS, PIN, NEO_GRB + NEO_KHZ800);
#define DELAYVAL 20

int rows=5;
int rowPins[] = {0, 2, 4, 5, 14};

int columns = 14;
int columnPins[] = { 12, 11, 10, 9, 8, 7, 6, 3, 1, 27, 13, 15, 26, 29 };

char name[] = "k_pro_blue";
#define NUM_LEDS 61    // Number of LEDs in your strip
SystemVIKeyboard *keyboard;

// Convert color wheel value (0–255) into RGB
uint32_t wheel(byte pos) {
    pos = 255 - pos;
    if (pos < 85) {
        return strip.Color(255 - pos * 3, 0, pos * 3);
    }
    if (pos < 170) {
        pos -= 85;
        return strip.Color(0, pos * 3, 255 - pos * 3);
    }
    pos -= 170;
    return strip.Color(pos * 3, 255 - pos * 3, 0);
}

void rainbowCycle(uint8_t wait) {
    static uint16_t j = 0;

    for (int i = 0; i < NUM_LEDS; i++) {
        int color_index = (i * 256 / NUM_LEDS + j) & 255;
        strip.setPixelColor(i, wheel(color_index));
    }

    strip.show();
    j++;        // slowly move rainbow forward
    delay(wait);
}
void setup() {
    strip.begin();
    strip.show();
}

void loop() {
    rainbowCycle(20); // smaller number = faster animation
}

