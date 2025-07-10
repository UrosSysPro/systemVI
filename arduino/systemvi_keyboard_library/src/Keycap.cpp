//
// Created by uros on 7/10/25.
//
#include "Keycap.h"
#include "Key.h"
#include "Keyboard.h"

Keycap::Keycap() {
    for (int i=0;i<4;i++)this->keys[i]=new Key();
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
        this->keys[this->currentlyUsed].onRelease(layer);
    }
}