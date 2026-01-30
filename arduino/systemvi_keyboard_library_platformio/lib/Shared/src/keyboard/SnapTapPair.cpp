#include "SystemVIKeyboard.h"
#include <cstring>
#include "Arduino.h"
#include "keys/Key.h"
#include "keycaps/Keycap.h"
#include "keys/MacroKey.h"
#include "keys/NormalKey.h"
#include "LittleFS.h"
#ifdef ARDUINO_KEYBOARD
#include "Keyboard.h"
#endif

void SystemVIKeyboard::addSnapTapKeyPair(int column0,int row0,int column1,int row1) {
    SnapTapPair* snapTapKeys=new SnapTapPair[this->snapTapPairCount+2];
    for (int i=0;i<this->snapTapPairCount;i++) snapTapKeys[i]=this->snapTapPairs[i];
    //first pair
    snapTapKeys[this->snapTapPairCount].first.column=column0;
    snapTapKeys[this->snapTapPairCount].first.row=row0;
    snapTapKeys[this->snapTapPairCount].second.column=column1;
    snapTapKeys[this->snapTapPairCount].second.row=row1;
    //second pair
    snapTapKeys[this->snapTapPairCount+1].first.column=column1;
    snapTapKeys[this->snapTapPairCount+1].first.row=row1;
    snapTapKeys[this->snapTapPairCount+1].second.column=column0;
    snapTapKeys[this->snapTapPairCount+1].second.row=row0;

    delete[] this->snapTapPairs;
    this->snapTapPairCount+=2;
    this->snapTapPairs=snapTapKeys;
}

void SystemVIKeyboard::removeSnapTapKeyPair(int column, int row) {
    SnapTapPair* snapTapKeys=new SnapTapPair[this->snapTapPairCount-2];
    int skip=0;
    for (int i=0;i<this->snapTapPairCount-2;i++) {
        if (this->snapTapPairs[i].first.column==column&&this->snapTapPairs[i].first.row==row) {
            skip=2;
        }
        snapTapKeys[i]=this->snapTapPairs[i+skip];
    }
    delete[] this->snapTapPairs;
    this->snapTapPairCount-=2;
    this->snapTapPairs=snapTapKeys;
}
