#ifndef SNAP_TAP_PAIR
#define SNAP_TAP_PAIR
#include "SnapTapKey.h"

class SnapTapPair {
public:
    SnapTapKey first,second;
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