#include "Keyboard.h"
#include "Key.h"
#include "NormalKey.h"

NormalKey::NormalKey(char value) {
    this->value = value;
}

bool NormalKey::onPress(int layer) {
    if (this->value) {
        Keyboard.press(this->value);
        return true;
    }
    return false;
}

bool NormalKey::onRelease(int layer) {
    Keyboard.release(this->value);
    return true;
}

void NormalKey::reportSerial() {
    char message[2];
    message[0]='n';
    message[1]=this->value;
    Serial.write((byte*)message,2);
}

void NormalKey::printToFile(File *file) {
    char buffer[3];
    buffer[0]='n';
    if (this->value=='\0') {
        buffer[1]=1;
    }else {
        buffer[1]=this->value;
    }
    buffer[2]='\0';
    file->print(buffer);
}
