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
    //                        matrix position   layers                                              physical    size        padding
    keyboard->setNormalKeycap(6,0,              (char[]){(char)KEY_ESC,'\0','\0','\0',},             0,0,        0,0,        0,0);
    keyboard->setNormalKeycap(5,0,              (char[]){'1',          '\0','\0','\0',},             1,0,        0,0,        0,0);
    keyboard->setNormalKeycap(4,0,              (char[]){'2',          '\0','\0','\0',},             2,0,        0,0,        0,0);
    keyboard->setNormalKeycap(3,0,              (char[]){'3',          '\0','\0','\0',},             3,0,        0,0,        0,0);
    keyboard->setNormalKeycap(2,0,              (char[]){'4',          '\0','\0','\0',},             4,0,        0,0,        0,0);
    keyboard->setNormalKeycap(1,0,              (char[]){'5',          '\0','\0','\0',},             5,0,        0,0,        0,0);
    keyboard->setNormalKeycap(0,0,              (char[]){'6',          '\0','\0','\0',},             6,0,        0,0,        0,0);

    keyboard->setNormalKeycap(6,1,              (char[]){'`',          '\0','\0','\0',},             0,1,        0,0,        0,0);
    keyboard->setNormalKeycap(5,1,              (char[]){'q',          '\0','\0','\0',},             1,1,        0,0,        0,0);
    keyboard->setNormalKeycap(4,1,              (char[]){'w',          '\0','\0','\0',},             2,1,        0,0,        0,0);
    keyboard->setNormalKeycap(3,1,              (char[]){'e',          '\0','\0','\0',},             3,1,        0,0,        0,0);
    keyboard->setNormalKeycap(2,1,              (char[]){'r',          '\0','\0','\0',},             4,1,        0,0,        0,0);
    keyboard->setNormalKeycap(1,1,              (char[]){'t',          '\0','\0','\0',},             5,1,        0,0,        0,0);
}

void loop() {

    keyboard->update();
}