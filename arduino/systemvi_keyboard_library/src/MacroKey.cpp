#include "Key.h"
#include "Keyboard.h"
#include "MacroKey.h"

MacroKey::MacroKey(int n,MacroAction* actions) {
    this->n=n;
    this->actions=actions;
}

MacroKey::~MacroKey() {
    delete[] this->actions;
}

boolean MacroKey::onPress(int layer) {
    return true;
}

boolean MacroKey::onRelease(int layer) {
    for (int i=0;i<this->n;i++) {
        int type=this->actions[i].type;
        char value=this->actions[i].value;
        if (type) {
            Keyboard.press(value);
        }else {
            Keyboard.release(value);
        }
    }
    return true;
}