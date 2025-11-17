#include <Adafruit_NeoPixel.h>

#define PIN       28
#define NUMPIXELS 61
Adafruit_NeoPixel pixels(NUMPIXELS, PIN, NEO_GRB + NEO_KHZ800);
#define DELAYVAL 500

void setup() {
    pixels.begin();
}

void loop() {
    pixels.clear();

    for(int i=0; i<NUMPIXELS; i++) {

        pixels.setPixelColor(i, pixels.Color(0, 0, 20,0));
        pixels.show();
        delay(DELAYVAL);
    }
}
