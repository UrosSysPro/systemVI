#ifndef KEY_H
#define KEY_H

#include "Arduino.h"
// #include ""

class Key {
public:
    Key();
    bool pressed,justChanged,active;
    int width,height,matrixX,matrixY;
    virtual void onPress();
    virtual void onRelease();
};

class NormalKey : public Key {
public:
    NormalKey();
    char value[4],currentlyDown;
};

class MacroAction {
public:
    MacroAction();
    int type;
    char value;
};

class MacroKey : public Key {

};
#endif
