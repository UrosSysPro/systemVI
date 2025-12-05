#include <Adafruit_NeoPixel.h>
#include "../../lib/Shared/src/keyboard/SystemVIKeyboard.h"
#include "Keyboard.h"

#define PIN       28
#define NUMPIXELS 61
Adafruit_NeoPixel strip(NUMPIXELS, PIN, NEO_GRB + NEO_KHZ800);
#define DELAYVAL 20

int rows=5;
int rowPins[] = { 0, 2, 4, 5, 22};

int columns = 14;
// int columnPins[] = { 12, 11, 10, 9, 8, 7, 6, 3, 1, 27, 13, 15, 26, 21 };
int columnPins[] = { 21,26,15,13,27,1,3,6,7,8,9,10,11,12 };

char name[] = "k_pro_blue";
#define NUM_LEDS 61    // Number of LEDs in your strip
SystemVIKeyboard *keyboard;

// Convert color wheel value (0–255) into RGB
uint32_t wheel(byte pos) {
    pos = 255 - pos;
    float brightness = 0.5;
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
    j++;        // slowly move rainbow forward
    delay(wait);
}

void setup() {
    strip.begin();
    strip.show();
    keyboard = new SystemVIKeyboard(
        name,
        columns,
        rows,
        columnPins,
        rowPins,
        true,
        columns,
        rows
    );

    keyboard->setNormalKeycap(13,0,         (char[]){static_cast<char>(KEY_ESC), '\0', '\0', '\0'}, 0, 0, 0, 0, 0, 0);
    keyboard->setNormalKeycap(12,0,         (char[]){'1','\0','\0','\0'},       1,0,  0,0,    0,0);
    keyboard->setNormalKeycap(11,0,         (char[]){'2','\0','\0','\0'},       2,0,  0,0,    0,0);
    keyboard->setNormalKeycap(10,0,         (char[]){'3','\0','\0','\0'},       3,0,  0,0,    0,0);
    keyboard->setNormalKeycap( 9,0,         (char[]){'4','\0','\0','\0'},       4,0,  0,0,    0,0);
    keyboard->setNormalKeycap( 8,0,         (char[]){'5','\0','\0','\0'},       5,0,  0,0,    0,0);
    keyboard->setNormalKeycap( 7,0,         (char[]){'6','\0','\0','\0'},       6,0,  0,0,    0,0);
    keyboard->setNormalKeycap( 6,0,         (char[]){'7','\0','\0','\0'},       7,0,  0,0,    0,0);
    keyboard->setNormalKeycap( 5,0,         (char[]){'8','\0','\0','\0'},       8,0,  0,0,    0,0);
    keyboard->setNormalKeycap( 4,0,         (char[]){'9','\0','\0','\0'},       9,0,  0,0,    0,0);
    keyboard->setNormalKeycap( 3,0,         (char[]){'0','\0','\0','\0'},      10,0,  0,0,    0,0);
    keyboard->setNormalKeycap( 2,0,         (char[]){'-','\0','\0','\0'},      11,0,  0,0,    0,0);
    keyboard->setNormalKeycap( 1,0,         (char[]){'=','\0','\0','\0'},      12,0,  0,0,    0,0);
    keyboard->setNormalKeycap( 0,0,         (char[]){static_cast<char>(KEY_BACKSPACE),'\0','\0','\0'},      13,0,  0,0,    0,0);
    //row 1
    keyboard->setNormalKeycap(13,1,         (char[]){static_cast<char>(KEY_TAB), '\0', '\0', '\0'}, 0, 1, 0, 0, 0, 0);
    keyboard->setNormalKeycap(12,1,         (char[]){'q','\0','\0','\0'},       1,1,  0,0,    0,0);
    keyboard->setNormalKeycap(11,1,         (char[]){'w','\0','\0','\0'},       2,1,  0,0,    0,0);
    keyboard->setNormalKeycap(10,1,         (char[]){'e','\0','\0','\0'},       3,1,  0,0,    0,0);
    keyboard->setNormalKeycap( 9,1,         (char[]){'r','\0','\0','\0'},       4,1,  0,0,    0,0);
    keyboard->setNormalKeycap( 8,1,         (char[]){'t','\0','\0','\0'},       5,1,  0,0,    0,0);
    keyboard->setNormalKeycap( 7,1,         (char[]){'y','\0','\0','\0'},       6,1,  0,0,    0,0);
    keyboard->setNormalKeycap( 6,1,         (char[]){'u','\0','\0','\0'},       7,1,  0,0,    0,0);
    keyboard->setNormalKeycap( 5,1,         (char[]){'i','\0','\0','\0'},       8,1,  0,0,    0,0);
    keyboard->setNormalKeycap( 4,1,         (char[]){'o','\0','\0','\0'},       9,1,  0,0,    0,0);
    keyboard->setNormalKeycap( 3,1,         (char[]){'p','\0','\0','\0'},      10,1,  0,0,    0,0);
    keyboard->setNormalKeycap( 2,1,         (char[]){'[','\0','\0','\0'},      11,1,  0,0,    0,0);
    keyboard->setNormalKeycap( 1,1,         (char[]){']','\0','\0','\0'},      12,1,  0,0,    0,0);
    keyboard->setNormalKeycap( 0,1,         (char[]){'\\','\0','\0','\0'},     13,1,  0,0,    0,0);
    //row 2
    keyboard->setNormalKeycap(13,2,         (char[]){static_cast<char>(KEY_CAPS_LOCK), '\0', '\0', '\0'}, 0, 2, 0, 0, 0, 0);
    keyboard->setNormalKeycap(12,2,         (char[]){'a','\0','\0','\0'},       1,2,  0,0,    0,0);
    keyboard->setNormalKeycap(11,2,         (char[]){'s','\0','\0','\0'},       2,2,  0,0,    0,0);
    keyboard->setNormalKeycap(10,2,         (char[]){'d','\0','\0','\0'},       3,2,  0,0,    0,0);
    keyboard->setNormalKeycap( 9,2,         (char[]){'f','\0','\0','\0'},       4,2,  0,0,    0,0);
    keyboard->setNormalKeycap( 8,2,         (char[]){'g','\0','\0','\0'},       5,2,  0,0,    0,0);
    keyboard->setNormalKeycap( 7,2,         (char[]){'h','\0','\0','\0'},       6,2,  0,0,    0,0);
    keyboard->setNormalKeycap( 6,2,         (char[]){'j','\0','\0','\0'},       7,2,  0,0,    0,0);
    keyboard->setNormalKeycap( 5,2,         (char[]){'k','\0','\0','\0'},       8,2,  0,0,    0,0);
    keyboard->setNormalKeycap( 4,2,         (char[]){'l','\0','\0','\0'},       9,2,  0,0,    0,0);
    keyboard->setNormalKeycap( 3,2,         (char[]){';','\0','\0','\0'},      10,2,  0,0,    0,0);
    keyboard->setNormalKeycap( 2,2,         (char[]){'\'','\0','\0','\0'},      11,2,  0,0,    0,0);
    keyboard->setNormalKeycap( 0,2,         (char[]){static_cast<char>(KEY_RETURN),'\0','\0','\0'},     13,2,  0,0,    0,0);
    //row 3
    keyboard->setNormalKeycap(13,3,         (char[]){static_cast<char>(KEY_LEFT_SHIFT), '\0', '\0', '\0'}, 0, 3, 0, 0, 0, 0);
    keyboard->setNormalKeycap(12,3,         (char[]){'z','\0','\0','\0'},       1,3,  0,0,    0,0);
    keyboard->setNormalKeycap(11,3,         (char[]){'x','\0','\0','\0'},       2,3,  0,0,    0,0);
    keyboard->setNormalKeycap(10,3,         (char[]){'c','\0','\0','\0'},       3,3,  0,0,    0,0);
    keyboard->setNormalKeycap( 9,3,         (char[]){'v','\0','\0','\0'},       4,3,  0,0,    0,0);
    keyboard->setNormalKeycap( 8,3,         (char[]){'b','\0','\0','\0'},       5,3,  0,0,    0,0);
    keyboard->setNormalKeycap( 7,3,         (char[]){'n','\0','\0','\0'},       6,3,  0,0,    0,0);
    keyboard->setNormalKeycap( 6,3,         (char[]){'m','\0','\0','\0'},       7,3,  0,0,    0,0);
    keyboard->setNormalKeycap( 5,3,         (char[]){',','\0','\0','\0'},       8,3,  0,0,    0,0);
    keyboard->setNormalKeycap( 4,3,         (char[]){'.','\0','\0','\0'},       9,3,  0,0,    0,0);
    keyboard->setNormalKeycap( 3,3,         (char[]){'/','\0','\0','\0'},      10,3,  0,0,    0,0);
    keyboard->setNormalKeycap( 0,3,         (char[]){static_cast<char>(KEY_RIGHT_SHIFT),'\0','\0','\0'},     13,3,  0,0,    0,0);
    //row 4
    keyboard->setNormalKeycap(13,4,         (char[]){static_cast<char>(KEY_LEFT_CTRL), '\0', '\0', '\0'},   0,4,  0,0,    0,0);
    keyboard->setNormalKeycap(12,4,         (char[]){static_cast<char>(KEY_LEFT_GUI),'\0','\0','\0'},       1,4,  0,0,    0,0);
    keyboard->setNormalKeycap(11,4,         (char[]){static_cast<char>(KEY_LEFT_ALT),'\0','\0','\0'},       2,4,  0,0,    0,0);
    keyboard->setNormalKeycap( 8,4,         (char[]){' ','\0','\0','\0'},                                   3,4,  0,0,    0,0);
    keyboard->setNormalKeycap( 4,4,         (char[]){static_cast<char>(KEY_LEFT_ALT),'\0','\0','\0'},       4,4,  0,0,    0,0);
    keyboard->setNormalKeycap( 3,4,         (char[]){static_cast<char>(KEY_LEFT_GUI),'\0','\0','\0'},       5,4,  0,0,    0,0);
    keyboard->setNormalKeycap( 2,4,         (char[]){static_cast<char>(KEY_MENU),'\0','\0','\0'},           6,4,  0,0,    0,0);
    keyboard->setNormalKeycap( 0,4,         (char[]){static_cast<char>(KEY_LEFT_CTRL),'\0','\0','\0'},      7,4,  0,0,    0,0);
}

void loop() {
    keyboard->update();
    rainbowCycle(3);
}

