#ifdef ARDUINO_KEYBOARD
#include "Keyboard.h"
#endif
#include "keys/Key.h"
#include "keys/NormalKey.h"

NormalKey::NormalKey(char value) {
    this->value = value;
}

bool NormalKey::onPress(int layer) {
    if (this->value) {
#ifdef ARDUINO_KEYBOARD
        Keyboard.press(this->value);
#endif
        return true;
    }
    return false;
}

bool NormalKey::onRelease(int layer) {
#ifdef ARDUINO_KEYBOARD
    Keyboard.release(this->value);
#endif
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
    file->write((uint8_t*)buffer,5);
}
