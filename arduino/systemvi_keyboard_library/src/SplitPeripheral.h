//
// Created by uros on 7/17/2025.
//

#ifndef SPLITPERIPHERAL_H
#define SPLITPERIPHERAL_H

#include "Arduino.h"
#include "Keycap.h"
#include "Wire.h"

class SplitPeripheral {
private:
    int *columnPins,*rowPins;
    int columns,rows;
    int wireSdaPin,wireSclPin,thisAdress,otherAdress;
    bool debugPrint;
    Keycap ***keys;
    char *name;
public:
    void updateKeyState();
    void processSerialCommands();
    void

    SplitPeripheral(
        char *name,
        int columns,
        int rows,
        int *columnPins,
        int *rowPins,
        int wireSdaPin,
        int wireSclPin,
        bool debugPrint
    );
    ~SplitPeripheral();
};

#endif //SPLITPERIPHERAL_H
