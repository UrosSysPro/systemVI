#ifndef SNAP_TAP_PAIR
#define SNAP_TAP_PAIR

#include "Arduino.h"
#include "SnapTapKey.h"

class SnapTapPair {
public:
    SnapTapKey first,second;

    void reportSerial() {
        byte message[5];
        message[0]=(byte)'s';
        message[1]=this->first.column;
        message[2]=this->first.row;
        message[3]=this->second.column;
        message[4]=this->second.row;
        Serial.write(message,5);
    }

    SnapTapPair() {
        this->first=SnapTapKey();
        this->second=SnapTapKey();
    }

    SnapTapPair(SnapTapKey first,SnapTapKey second) {
        this->first=first;
        this->second=second;
    }
};

#endif