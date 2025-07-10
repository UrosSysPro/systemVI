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
    boolean onPress(int layer)override;
    boolean onRelease(int layer)override;
    MacroKey(int n,MacroAction* actions);
    ~MacroKey();
};

#endif