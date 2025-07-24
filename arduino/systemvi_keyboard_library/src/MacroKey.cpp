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
        if (type==PRESS) {
            Keyboard.press(value);
        }else {
            Keyboard.release(value);
        }
        delay(20);
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

void MacroKey::printToFile(File *file,int i,int j,int layer) {
    char buffer[5];
    buffer[0]='m';
    buffer[1]=i;
    buffer[2]=j;
    buffer[3]=layer;
    buffer[4]=this->n;
    file->write(buffer,5);
    for (int i=0;i<this->n;i++) {
        buffer[0]=this->actions[i].value;
        buffer[1]=this->actions[i].type;
        file->write(buffer,2);
    }
}


MacroAction::MacroAction(char value, MacroActionType type) {
    this->value=value;
    this->type=type;
}

MacroAction::MacroAction() {
    this->value=0;
    this->type=PRESS;
}
