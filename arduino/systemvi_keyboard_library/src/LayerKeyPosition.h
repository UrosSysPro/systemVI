#ifndef LAYER_KEY_POSITION
#define LAYER_KEY_POSITION

#include "Arduino.h"
#include "LittleFS.h"

class LayerKeyPosition {
public:
    int column,row,layer;
    void reportSerial() {
        byte message[4];
        message[0]=(byte)'l';
        message[1]=this->column;
        message[2]=this->row;
        message[3]=this->layer;
        Serial.write(message,4);
    }
    void printToFile(File *file) {
        byte message[4];
        message[0]=(byte)'l';
        message[1]=this->column;
        message[2]=this->row;
        message[3]=this->layer;
        file->write(message,4);
    }
    LayerKeyPosition(int column, int row, int layer) {
        this->column=column;
        this->row=row;
        this->layer=layer;
    }
    LayerKeyPosition() {
        this->column=-1;
        this->row=-1;
        this->layer=-1;
    }
};

#endif