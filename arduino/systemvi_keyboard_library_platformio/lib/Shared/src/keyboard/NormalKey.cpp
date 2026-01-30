#include "SystemVIKeyboard.h"
#include <cstring>
#include "Arduino.h"
#include "keys/Key.h"
#include "keycaps/Keycap.h"
#include "keys/MacroKey.h"
#include "keys/NormalKey.h"
#include "LittleFS.h"
#ifdef ARDUINO_KEYBOARD
#include "Keyboard.h"
#endif

void SystemVIKeyboard::setNormalKeycap(int column,int row,char*values,int physicalColumn,int physicalRow,int with,int height,int paddingLeft,int paddingBottom) {
    //set normal key
    delete this->keys[column][row];
    this->keys[column][row]=new Keycap();
    for (int i=0;i<4;i++) {
        Key *key=new NormalKey(values[i]);
        this->keys[column][row]->keys[i]=key;
    }
    this->keys[column][row]->physicalColumn=physicalColumn;
    this->keys[column][row]->physicalRow=physicalRow;
    this->keys[column][row]->width=with;
    this->keys[column][row]->height=height;
    this->keys[column][row]->active=true;
    this->keys[column][row]->paddingLeft=paddingLeft;
    this->keys[column][row]->paddingBottom=paddingBottom;
}
void SystemVIKeyboard::setNormalKeycap(int column, int row, char *values, int physicalX, int physicalY,int width,int height) {
    this->setNormalKeycap(column,row,values,physicalX,physicalY,width,height,0,0);
}
void SystemVIKeyboard::setNormalKeycap(int column, int row, char *values, int physicalX, int physicalY) {
    this->setNormalKeycap(column,row,values,physicalX,physicalY,0,0,0,0);
}

void SystemVIKeyboard::setNormalKeycap(int column,int row,char* values) {
    this->setNormalKeycap(column,row,values,0,0);
}

void SystemVIKeyboard::setNormalKeycap(int column,int row, int layer,char value) {
    //update normal key layer
    NormalKey *key=(NormalKey*)this->keys[column][row]->keys[layer];
    key->value=value;
}

void SystemVIKeyboard::setLayer(int column, int row, int layer, Key *value) {
    delete this->keys[column][row]->keys[layer];
    this->keys[column][row]->keys[layer]=value;
}

void SystemVIKeyboard::setLayers(int column, int row, Key *values) {
    for (int i=0;i<4;i++) this->setLayer(column,row,i,values+i);
}
