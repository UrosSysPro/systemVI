#ifndef KEY_H
#define KEY_H

#include "Arduino.h"

class Key {
    virtual void onPress(){};
    virtual void onRelease(){};
};

class Keycap {
public:
    bool pressed,justChanged,active;
    int width,height,matrixX,matrixY;
    Key *keys[4];
    void onPress(int layer);
    void onRelease(int layer);
    Keycap();
};


class NormalKey : public Key {
public:
    char value[4],currentlyDown;
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
    void onPress()override;
    void onRelease()override;
    MacroKey();
};

#endif
