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

bool MacroKey::onPress(int layer) {
    return true;
}

bool MacroKey::onRelease(int layer) {
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

void MacroKey::reportSerial() {
    char message[2];
    message[0]='m';
    message[1]=this->n;
    Serial.write((byte*)message,2);
    for (int i=0;i<this->n;i++) {
        message[0]=this->actions[i].value;
        message[1]=this->actions[i].type;
        Serial.write((byte*)message,2);
    }
}