#include"SystemVIKeyboard.h"
#include <string.h>
#include "Arduino.h"
#include "Keyboard.h"
#include "Key.h"
#include "Keycap.h"
#include "MacroKey.h"
#include "NormalKey.h"

SystemVIKeyboard::SystemVIKeyboard(char* name, int columns,int rows,int* columnPins,int* rowPins) {
    int length=strlen(name);
    this->name=new char[length+1];
    for (int i=0;i<length;i++)this->name[i]=name[i];
    this->name[length]='\0';

    this->rows=rows;
    this->columns=columns;
    this->layerKeyPositionCount=0;

    this->columnPins=new int[columns];
    for (int i=0;i<columns;i++)this->columnPins[i]=columnPins[i];

    this->rowPins=new int[rows];
    for (int i=0;i<rows;i++)this->rowPins[i]=rowPins[i];

    this->layerKeyPositions=new LayerKeyPosition[1];

    for(int i=0;i<columns;i++){
        int pin=columnPins[i];
        pinMode(pin, INPUT_PULLUP);
    }

    for(int i=0;i<rows;i++){
        int pin = rowPins[i];
        pinMode(pin, OUTPUT);
        digitalWrite(pin, 1);
    }
    //init keys
    this->keys=new Keycap**[columns];
    for (int i=0;i<columns;i++) {
        this->keys[i]=new Keycap*[rows];
        for (int j=0;j<rows;j++) {
            this->keys[i][j]=new Keycap();
        }
    }

    this->printKeyEventsToSerial=true;

    Serial.begin(9600);
    Keyboard.begin();
}

SystemVIKeyboard::~SystemVIKeyboard() {
    delete [] this->name;
    delete [] this->rowPins;
    delete [] this->columnPins;
    for (int i=0;i<columns;i++) {
        for (int j=0;j<rows;j++) {
            delete this->keys[i][j];
        }
        delete [] this->keys[i];
    }
    delete [] this->keys;
}

void SystemVIKeyboard::update() {
    this->processSerialCommands();
    this->updateKeyState();
    this->executeKeyboardEvents();
}

void SystemVIKeyboard::updateKeyState() {
    for(int j=0;j<this->rows;j++){
        int rowPin=this->rowPins[j];
        digitalWrite(rowPin, 0);
        delayMicroseconds(1000);
        for(int i=0;i<this->columns;i++){
            int columnPin=columnPins[i];
            bool newState=digitalRead(columnPin) == 0;
            if(newState!=keys[i][j]->pressed) {
                keys[i][j]->justChanged=true;
                keys[i][j]->pressed=newState;
            }
        }
        digitalWrite(rowPin, 1);
    }
}

void SystemVIKeyboard::executeKeyboardEvents() {

    int layer=0;
    //process which layer is on

    for(int i=0;i<this->columns;i++){
        for(int j=0;j<this->rows;j++){
            if(Keycap *key=this->keys[i][j]; key->justChanged){
                key->justChanged=false;
                if(key->pressed){
                    key->onPress(layer);
                    if (this->printKeyEventsToSerial)printKeyPressToSerial(i,j);
                }else{
                    key->onRelease(layer);
                    if (this->printKeyEventsToSerial)printKeyReleaseToSerial(i,j);
                }
            }
        }
    }
}

void SystemVIKeyboard::processSerialCommands() {
    if(Serial.available()>0){
        char cmd=Serial.read();
        if(cmd=='r')this->reportLayout();
        if(cmd=='k')this->serialSetLayers();
        if(cmd=='l')this->serialSetLayer();
        if(cmd=='m')this->serialSetMacro();
        if(cmd=='A')this->serialAddLayerKeyPosition();
        if(cmd=='S')this->serialRemoveLayerKeyPosition();
        if(cmd=='e')this->printKeyEventsToSerial=true;
        if(cmd=='d')this->printKeyEventsToSerial=false;
        if(cmd=='n')this->printName();
    }
}

void SystemVIKeyboard::reportLayout() {
    byte header[]={(byte)'l',(byte)this->columns,(byte)this->rows};
    Serial.write(header,3);

    for(int i=0;i<this->columns;i++){
        for(int j=0;j<this->rows;j++){
            this->keys[i][j]->reportSerial(i,j);
        }
    }

    Serial.print('@');
}

void SystemVIKeyboard::setNormalKeycap(int column,int row,char*values,int physicalColumn,int physicalRow,int with,int height) {
    //set normal key
    delete this->keys[column][row];
    this->keys[column][row]=new Keycap();
    for (int i=0;i<4;i++) {
        Key *key=new NormalKey(values[i]);
        this->keys[column][row]->keys[i]=key;
    }
    this->keys[column][row]->physicalColumn=physicalColumn;
    this->keys[column][row]->physicalRow=physicalRow;
    this->keys[column][row]->width=with;
    this->keys[column][row]->height=height;
    this->keys[column][row]->active=true;
}
void SystemVIKeyboard::setNormalKeycap(int column, int row, char *values, int physicalX, int physicalY) {
    this->setNormalKeycap(column,row,values,physicalX,physicalY,0,0);
}

void SystemVIKeyboard::setNormalKeycap(int column,int row,char* values) {
    this->setNormalKeycap(column,row,values,0,0);
}

void SystemVIKeyboard::setNormalKeycap(int column,int row, int layer,char value) {
    //update normal key layer
    NormalKey *key=(NormalKey*)this->keys[column][row]->keys[layer];
    key->value=value;
}

void SystemVIKeyboard::setLayer(int column, int row, int layer, Key *value) {
    delete this->keys[column][row]->keys[layer];
    this->keys[column][row]->keys[layer]=value;
}

void SystemVIKeyboard::setLayers(int column, int row, Key *values) {
    for (int i=0;i<4;i++) this->setLayer(column,row,i,values+i);
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
    this->setLayer(column,row,layer,new MacroKey(n,actions));
}

void SystemVIKeyboard::serialAddLayerKeyPosition() {
    int column=Serial.read();
    int row=Serial.read();
    int layer=Serial.read();
    for (int i=0;i<4;i++) {
        delete this->keys[column][row]->keys[i];
        this->keys[column][row]->keys[i]=new NormalKey('\0');
    }
    LayerKeyPosition* layerKeys=new LayerKeyPosition[this->layerKeyPositionCount+1];
    for (int i=0;i<this->layerKeyPositionCount;i++) layerKeys[i]=this->layerKeyPositions[i];
    layerKeys[this->layerKeyPositionCount].layer=layer;
    layerKeys[this->layerKeyPositionCount].row=row;
    layerKeys[this->layerKeyPositionCount].column=column;
    delete[] this->layerKeyPositions;
    this->layerKeyPositionCount++;
    this->layerKeyPositions=layerKeys;
}

void SystemVIKeyboard::serialRemoveLayerKeyPosition() {
    int column=Serial.read();
    int row=Serial.read();
    LayerKeyPosition* layerKeys=new LayerKeyPosition[this->layerKeyPositionCount-1];
    int skip=0;
    for (int i=0;i<this->layerKeyPositionCount-1;i++) {
        if (this->layerKeyPositions[i].column==column&&this->layerKeyPositions[i].row==row) {
            skip=1;
        }
        layerKeys[i]=this->layerKeyPositions[i+skip];
    }
    delete[] this->layerKeyPositions;
    this->layerKeyPositionCount--;
    this->layerKeyPositions=layerKeys;
}