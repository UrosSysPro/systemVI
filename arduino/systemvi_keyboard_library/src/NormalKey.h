#ifndef NORMALKEY
#define NORMALKEY

#include "Key.h"

class NormalKey : public Key {
public:
    char value;
    boolean onPress(int layer)override;
    boolean onRelease(int layer)override;
    NormalKey(char value);
};

#endif