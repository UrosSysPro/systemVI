#ifndef KEY_H
#define KEY_H

#include "Arduino.h"

class Key {
public:
    bool pressed,justChanged,active;
    int width,height,matrixX,matrixY;
    virtual void onPress();
    virtual void onRelease();
    Key();
};

class NormalKey : public Key {
public:
    char value[4]{},currentlyDown{};
    void onPress()override;
    void onRelease()override;
    NormalKey();
};

class MacroAction {
public:
    int type;
    char value;
    MacroAction();
};

class MacroKey : public Key {
public:
    MacroAction* actions;
    MacroKey();
};

#endif
