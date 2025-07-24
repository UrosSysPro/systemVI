#ifndef NORMALKEY
#define NORMALKEY

#include "Key.h"
#include "LittleFS.h"

class NormalKey : public Key {
public:
    char value;
    bool onPress(int layer)override;
    bool onRelease(int layer)override;
    void reportSerial()override;
    void printToFile(File *file,int i,int j,int layer) override;
    NormalKey(char value);
};

#endif