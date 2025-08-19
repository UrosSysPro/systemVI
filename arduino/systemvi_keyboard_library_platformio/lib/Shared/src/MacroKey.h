#ifndef MACROKEY_H
#define MACROKEY_H

#include "Key.h"
#include "LittleFS.h"

enum MacroActionType {
    PRESS=1,
    RELEASE=0,
};

class MacroAction {
public:
    MacroActionType type;
    char value;
    MacroAction(char value,MacroActionType type);
    MacroAction();
};

class MacroKey : public Key {
public:
    int n;
    MacroAction* actions;
    char* name;
    bool onPress(int layer)override;
    bool onRelease(int layer)override;
    void reportSerial()override;
    void printToFile(File *file,int i,int j,int layer) override;
    MacroKey(int n,MacroAction* actions,char* name);
    ~MacroKey();
};

#endif