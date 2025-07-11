#ifndef MACROKEY_H
#define MACROKEY_H

#include "Key.h"

enum MacroActionType {
    PRESS=1,
    RELEASE=2,
};

class MacroAction {
public:
    MacroActionType type;
    char value;
    MacroAction();
};

class MacroKey : public Key {
public:
    int n;
    MacroAction* actions;
    bool onPress(int layer)override;
    bool onRelease(int layer)override;
    void reportSerial()override;
    MacroKey(int n,MacroAction* actions);
    ~MacroKey();
};

#endif