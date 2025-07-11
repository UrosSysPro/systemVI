//
// Created by uros on 7/10/25.
//
#include "Keycap.h"
#include "Key.h"
#include "Keyboard.h"

Keycap::Keycap() {
    for (int i=0;i<4;i++)this->keys[i]=new Key();
    this->active=false;
    this->width=0;
    this->height=0;
    this->justChanged=false;
    this->pressed=false;
    this->currentlyUsed=-1;
    this->matrixX=0;
    this->matrixY=0;
    this->physicalColumn=0;
    this->physicalRow=0;
}

Keycap::~Keycap() {
    for (int i=0;i<4;i++)delete this->keys[i];
}

void Keycap::onPress(int layer) {
    this->currentlyUsed=-1;
    for (int k=layer;k>=0;k--) {
        if (this->keys[k]->onPress(layer)) {
            this->currentlyUsed=k;
            break;
        }
    }
}

void Keycap::onRelease(int layer) {
    if (this->currentlyUsed!=-1) {
        this->keys[this->currentlyUsed]->onRelease(layer);
    }
}
void Keycap::reportSerial(int column,int row) {
    char message[7];
    message[0]='c';
    message[1]=column;
    message[2]=row;
    message[3]=this->width;
    message[4]=this->height;
    message[5]=this->physicalColumn;
    message[6]=this->physicalRow;

    if (this->active) {
        Serial.write((byte*)message,7);
        for (int i=0;i<4;i++) {
            this->keys[i]->reportSerial();
        }
    }
}