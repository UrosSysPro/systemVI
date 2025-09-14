#include "Arduino.h"
#include "Keyboard.h"
#include "builders/KeyboardBuilder.h"
#include "keyboard/SystemVIKeyboard.h"

SystemVIKeyboard *keyboard;
char name[]="dactyl 7x5";
int columns = 7;
int rows = 6;
int columnPins[] = {0,1,2,3,6,7,8};
int rowPins[] = {9,10,11,12,15,14};

void setup() {
    keyboard = KeyboardBuilder()
        .setName(name)
        ->setColumns(columns,columnPins)
        ->setRows(rows,rowPins)
        ->setDebugPrint(true)
        ->build();
    //                        matrix position   layers                              physical    size        padding
    keyboard->setNormalKeycap(0,0,              (char[]){'\0','\0','\0','\0',},     0,0,        0,0,        0,0);
}

void loop() {

    keyboard->update();
}