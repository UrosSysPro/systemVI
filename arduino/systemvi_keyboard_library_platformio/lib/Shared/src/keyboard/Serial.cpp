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

void SystemVIKeyboard::processSerialCommands() {
    if(Serial.available()>0){
        char cmd=Serial.read();
        //current keymap
        if(cmd=='r')this->reportLayout();
        if(cmd=='R')this->removeLayout();
        //set keys and macros
        if(cmd=='k')this->serialSetLayers();
        if(cmd=='l')this->serialSetLayer();
        if(cmd=='m')this->serialSetMacro();
        //set layer keys
        if(cmd=='A')this->serialAddLayerKeyPosition();
        if(cmd=='S')this->serialRemoveLayerKeyPosition();
        //snap tap
        if(cmd=='E')this->serialAddSnapTapKeyPair();
        if(cmd=='D')this->serialRemoveSnapTapKeyPair();
        //debug print
        if(cmd=='e')this->printKeyEventsToSerial=true;
        if(cmd=='d')this->printKeyEventsToSerial=false;
        if(cmd=='n')this->printName();
        //flash storage
        if(cmd=='f')this->saveToFlash();
        if(cmd=='F')this->loadFromFlash();
        if(cmd=='q')this->eraceFlash();
    }
}

void SystemVIKeyboard::reportLayout() {
    byte header[]={(byte)'l',(byte)this->reportedColumns,(byte)this->reportedRows};
    Serial.write(header,3);

    for(int i=0;i<this->columns;i++){
        for(int j=0;j<this->rows;j++){
            this->keys[i][j]->reportSerial(i,j);
        }
    }

    for (int i=0;i<this->layerKeyPositionCount;i++) {
        this->layerKeyPositions[i].reportSerial();
    }

    for (int i=0;i<this->snapTapPairCount;i+=2) {
        this->snapTapPairs[i].reportSerial();
    }

    Serial.print('@');
}

void SystemVIKeyboard::removeLayout() {
    for(int i=0;i<this->columns;i++){
        for(int j=0;j<this->rows;j++){
            for(auto & key : this->keys[i][j]->keys) {
                delete key;
                key=new NormalKey('\0');
            }
        }
    }
    delete [] this->snapTapPairs;
    delete [] this->layerKeyPositions;
    this->snapTapPairCount=0;
    this->layerKeyPositionCount=0;
    this->snapTapPairs=new SnapTapPair[1];
    this->layerKeyPositions=new LayerKeyPosition[1];
}

void SystemVIKeyboard::printKeyPressToSerial(int column,int row) {
    byte message[4];
    message[0]=(byte)'p';
    message[1]=column;
    message[2]=row;
    message[3]='@';
    Serial.write(message,4);
}

void SystemVIKeyboard::printKeyReleaseToSerial(int column, int row) {
    byte message[4];
    message[0]=(byte)'r';
    message[1]=column;
    message[2]=row;
    message[3]='@';
    Serial.write(message,4);
}

void SystemVIKeyboard::printName() {
    char *message=this->name;
    int size=strlen(message);
    Serial.print('n');
    Serial.write((byte*)message,size);
    Serial.print('@');
}

void SystemVIKeyboard::serialSetLayer() {
    int column=Serial.read();
    int row=Serial.read();
    int layer=Serial.read();
    char value=Serial.read();
    this->setLayer(column,row,layer,new NormalKey(value));
}

void SystemVIKeyboard::serialSetLayers() {
    int column=Serial.read();
    int row=Serial.read();
    for (int i=0;i<4;i++)this->setLayer(column,row,i,new NormalKey((char)Serial.read()));
}

void SystemVIKeyboard::serialSetMacro() {
    int column=Serial.read();
    int row=Serial.read();
    int layer=Serial.read();
    int n=Serial.read();
    MacroAction* actions=new MacroAction[n];
    for (int i=0;i<n;i++) {
        char value=Serial.read();
        MacroActionType type=Serial.read()==1?PRESS:RELEASE;
        actions[i]=MacroAction(value,type);
    }
    int nameLength=Serial.read();
    char* name=new char[nameLength+1];
    for (int i=0;i<nameLength;i++) name[i]=Serial.read();
    name[nameLength]='\0';
    this->setLayer(column,row,layer,new MacroKey(n,actions,name));
}

void SystemVIKeyboard::serialAddLayerKeyPosition() {
    int column=Serial.read();
    int row=Serial.read();
    int layer=Serial.read();
    this->addLayerKeyPosition(column,row,layer);
}

void SystemVIKeyboard::serialRemoveLayerKeyPosition() {
    int column=Serial.read();
    int row=Serial.read();
    this->removeLayerKeyPosition(column,row);
}

void SystemVIKeyboard::serialAddSnapTapKeyPair() {
    int column0=Serial.read();
    int row0=Serial.read();
    int column1=Serial.read();
    int row1=Serial.read();
    this->addSnapTapKeyPair(column0,row0,column1,row1);
}

void SystemVIKeyboard::serialRemoveSnapTapKeyPair() {
    int column=Serial.read();
    int row=Serial.read();
    this->removeSnapTapKeyPair(column,row);
}

void SystemVIKeyboard::serialMessage(char message[]) {
    Serial.printf("m%s\n@",message);
}
