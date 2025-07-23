#ifndef KEY_H
#define KEY_H

#include "Arduino.h"
#include "LittleFS.h"

class Key {
public:
    virtual bool onPress(int layer){return false;}
    virtual bool onRelease(int layer){return false;}
    virtual void reportSerial(){}
    virtual void printToFile(File *file){file->print("k");}
};

#endif
