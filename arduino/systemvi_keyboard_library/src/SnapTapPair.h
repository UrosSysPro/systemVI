#ifndef SNAP_TAP_PAIR
#define SNAP_TAP_PAIR
#include "SnapTapKey.h"

class SnapTapPair {
public:
    SnapTapKey first,second;
    void reportSerial() {
        // byte message[4];
        // message[0]=(byte)'p';
        // message[1]=column;
        // message[2]=row;
        // message[3]='@';
        // Serial.write(message,4);
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