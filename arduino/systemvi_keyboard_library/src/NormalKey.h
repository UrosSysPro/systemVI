#ifndef NORMALKEY
#define NORMALKEY

#include "Key.h"

class NormalKey : public Key {
public:
    char value;
    bool onPress(int layer)override;
    bool onRelease(int layer)override;
    void reportSerial()override;
    NormalKey(char value);
};

#endif