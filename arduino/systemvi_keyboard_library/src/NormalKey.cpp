#include "Keyboard.h"
#include "Key.h"
#include "NormalKey.h"

NormalKey::NormalKey(char value) {
    this->value = value;
}

boolean NormalKey::onPress(int layer) {
    if (this->value) {
        Keyboard.press(this->value);
        return true;
    }
    return false;
}
boolean NormalKey::onRelease(int layer) {
    Keyboard.release(this->value);
    return true;
}