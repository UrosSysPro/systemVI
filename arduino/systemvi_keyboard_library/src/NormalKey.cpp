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

void NormalKey::printToFile(File *file,int i,int j,int layer) {
    char buffer[5];
    buffer[0]='n';
    buffer[1]=i;
    buffer[2]=j;
    buffer[3]=layer;
    buffer[4]=this->value;
    file->write(buffer,5);
}
