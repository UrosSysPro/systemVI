#ifndef KEY_H
#define KEY_H

#include "Arduino.h"

class Key {
public:
    virtual boolean onPress(int layer){return false;}
    virtual boolean onRelease(int layer){return false;}
};

#endif
