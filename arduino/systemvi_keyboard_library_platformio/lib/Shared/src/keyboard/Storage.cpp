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

void SystemVIKeyboard::loadFromFlash() {
    if (!LittleFS.begin()) {
        this->serialMessage("failed to mount file system");
        return;
    }

    if (!LittleFS.exists("/keymaps/keymap0.txt")) {
        this->serialMessage("File doesnt exist");
        return;
    }

    File file=LittleFS.open("/keymaps/keymap0.txt","r");
    if (!file) {
        this->serialMessage("failed to open file for reading");
        return;
    }

    this->serialMessage("Start Read from flash");
    this->removeLayout();
    while (file.position()<file.size()) {
        char type=file.read();
        if (type=='n') {
            int i=file.read();
            int j=file.read();
            int layer=file.read();
            char value=file.read();
            delete this->keys[i][j]->keys[layer];
            this->keys[i][j]->keys[layer]=new NormalKey(value);
        }
        if (type=='m') {
            int i=file.read();
            int j=file.read();
            int layer=file.read();
            int n=file.read();
            MacroAction *actions=new MacroAction[n];
            for (int k=0;k<n;k++) {
                char value=file.read();
                bool type=file.read()==1;
                if (type)actions[k].type=PRESS; else actions[k].type=RELEASE;
                actions[k].value=value;
            }
            int nameLength=file.read();
            char *name=new char[nameLength];
            for (int k=0;k<nameLength;k++)name[k]=file.read();
            name[nameLength]='\0';
            delete this->keys[i][j]->keys[layer];
            this->keys[i][j]->keys[layer]=new MacroKey(n,actions,name);
        }
        if (type=='s') {
            int column0=file.read();
            int row0=file.read();
            int column1=file.read();
            int row1=file.read();
            this->addSnapTapKeyPair(column0,row0,column1,row1);
        }
        if (type=='l') {
            int column=file.read();
            int row=file.read();
            int layer=file.read();
            this->addLayerKeyPosition(column,row,layer);
        }
    }

    file.close();
    Serial.print("mEnded Read from flash\n@");
    LittleFS.end();
}

void SystemVIKeyboard::saveToFlash() {
    if (!LittleFS.begin()) {
        this->serialMessage("failed to mount file system");
        return;
    }
    Serial.print("mStart Write to flash\n@");
    File file=LittleFS.open("/keymaps/keymap0.txt","w");
    if (!file) {
        this->serialMessage("failed to open file for writing");
        return;
    }

    for (int i=0;i<this->columns;i++) {
        for (int j=0;j<this->rows;j++) {
            if (this->keys[i][j]->active) {
                this->keys[i][j]->printToFile(&file,i,j);
            }
        }
    }
    for (int i=0;i<this->snapTapPairCount;i+=2) {
        this->snapTapPairs[i].printToFile(&file);
    }
    for (int i=0;i<this->layerKeyPositionCount;i++) {
        this->layerKeyPositions[i].printToFile(&file);
    }

    file.close();
    Serial.print("mEnded Write to flash\n@");
    LittleFS.end();
}

void SystemVIKeyboard::eraceFlash() {
    if (!LittleFS.begin()) {
        this->serialMessage("failed to mount file system");
        return;
    }
    LittleFS.remove("/keymaps/keymap0.txt");
    LittleFS.end();
}
