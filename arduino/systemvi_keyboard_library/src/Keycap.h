//
// Created by uros on 7/10/25.
//

#ifndef KEYMAP_H
#define KEYMAP_H
#include "Key.h"

class Keycap {
public:
    bool
        pressed,
        justChanged,
        active;
    int
        width,
        height,
        matrixX,
        matrixY,
        physicalColumn,
        physicalRow,
        paddingLeft,
        paddingBottom;
    int currentlyUsed;
    Key *keys[4];
    void onPress(int layer);
    void onRelease(int layer);
    void reportSerial(int column,int row);
    Keycap();
    ~Keycap();
};

#endif //KEYMAP_H
