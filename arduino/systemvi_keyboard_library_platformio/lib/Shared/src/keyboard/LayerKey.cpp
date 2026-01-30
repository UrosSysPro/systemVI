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

void SystemVIKeyboard::addLayerKeyPosition(int column,int row,int layer) {
    for (int i=0;i<4;i++) {
        delete this->keys[column][row]->keys[i];
        this->keys[column][row]->keys[i]=new NormalKey('\0');
    }
    LayerKeyPosition* layerKeys=new LayerKeyPosition[this->layerKeyPositionCount+1];
    for (int i=0;i<this->layerKeyPositionCount;i++) layerKeys[i]=this->layerKeyPositions[i];
    layerKeys[this->layerKeyPositionCount].layer=layer;
    layerKeys[this->layerKeyPositionCount].row=row;
    layerKeys[this->layerKeyPositionCount].column=column;
    delete[] this->layerKeyPositions;
    this->layerKeyPositionCount++;
    this->layerKeyPositions=layerKeys;
}

void SystemVIKeyboard::removeLayerKeyPosition(int column,int row) {
    LayerKeyPosition* layerKeys=new LayerKeyPosition[this->layerKeyPositionCount-1];
    int skip=0;
    for (int i=0;i<this->layerKeyPositionCount-1;i++) {
        if (this->layerKeyPositions[i].column==column&&this->layerKeyPositions[i].row==row) {
            skip=1;
        }
        layerKeys[i]=this->layerKeyPositions[i+skip];
    }
    delete[] this->layerKeyPositions;
    this->layerKeyPositionCount--;
    this->layerKeyPositions=layerKeys;
}
