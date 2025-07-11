#ifndef KEY_H
#define KEY_H

#include "Arduino.h"

class Key {
public:
    virtual bool onPress(int layer){return false;}
    virtual bool onRelease(int layer){return false;}
    virtual void reportSerial(){}
};

#endif
