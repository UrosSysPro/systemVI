#include "SystemVIKeyboard.h"

SystemVIKeyboard *keyboard;

void setup() {
    int columns=14;
    int rows=5;
    int columnPins[14] = {};
    int rowPins[5] = {};
    char name[]="kailh_silver";
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
}

void loop() {
    keyboard->update();
}