#include"SystemVIKeyboard.h"
#include <string.h>
#include "Arduino.h"
#include "Keyboard.h"

class ReportedKey{
public:
    byte x,y,value[4],width,height,physicalX,physicalY,active;
    ReportedKey(){}
    ReportedKey(byte x,byte y,byte* value,int width,int height,int px,int py,bool active){
        this->x=x;
        this->y=y;
        for(int i=0;i<4;i++){
            this->value[i]=value[i];
        }
        this->width=width;
        this->height=height;
        this->physicalX=px;
        this->physicalY=py;
        this->active=active;
    }
};

SystemVIKeyboard::SystemVIKeyboard(char* name, int columns,int rows,int* columnPins,int* rowPins) {
    int length=strlen(name);
    this->name=new char[length+1];
    for (int i=0;i<length;i++)this->name[i]=name[i];
    this->name[length]='\0';

    this->rows=rows;
    this->columns=columns;

    this->columnPins=new int[columns];
    for (int i=0;i<columns;i++)this->columnPins[i]=columnPins[i];

    this->rowPins=new int[rows];
    for (int i=0;i<rows;i++)this->rowPins[i]=rowPins[i];

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
    this->keys=new Keycap **[columns];
    for (int i=0;i<columns;i++) {
        this->keys[i]=new Keycap*[rows];
        for (int j=0;j<rows;j++) {
            this->keys[i][j]=new Keycap();
        }
    }

    Serial.begin(9600);
    Keyboard.begin();
}

void SystemVIKeyboard::updateKeyState() {
    for(int j=0;j<this->rows;j++){
        int rowPin=this->rowPins[j];
        digitalWrite(rowPin, 0);
        delayMicroseconds(1000);
        for(int i=0;i<this->columns;i++){
            int columnPin=columnPins[i];
            bool newState=digitalRead(columnPin) == 0;
            if(newState!=keys[i][j]->pressed)keys[i][j]->justChanged=true;
            keys[i][j]->pressed=newState;
        }
        digitalWrite(rowPin, 1);
    }
}

void SystemVIKeyboard::executeKeyboardEvents() {

    int layer=0;
    //process which layer is on

    // if(keys[6][5].pressed){
        // keys[6][5].justChanged=false;
        // layer=1;
    // }

    for(int i=0;i<this->columns;i++){
        for(int j=0;j<this->columns;j++){
            if(keys[i][j]->justChanged){
                keys[i][j]->justChanged=false;
                Keycap* key=keys[i][j];
                if(keys[i][j]->pressed){
                    key->onPress(layer);
                }else{
                    key->onRelease(layer);
                }
            }
        }
    }
}

void SystemVIKeyboard::processSerialCommands() {
    if(Serial.available()>0){
        char cmd=Serial.read();
        if(cmd=='r')this->reportLayout();
        if(cmd=='k')this->setNormalKey();
        if(cmd=='l')this->setNormalKeyLayer();
        if(cmd=='e')this->printKeyEventsToSerial=true;
        if(cmd=='d')this->printKeyEventsToSerial=false;
        if(cmd=='n')printName();
    }
}

void SystemVIKeyboard::reportLayout() {
    int n=this->columns*this->rows;
    ReportedKey* reportedKeys=new ReportedKey[n];
    for(int i=0;i<this->columns;i++){
        for(int j=0;j<this->rows;j++){
            // reportedKeys[j*this->columns+i]=ReportedKey(
              // i,
              // j,
              // (byte*)keys[i][j].value,
              // keys[i][j].width,
              // keys[i][j].height,
              // keys[i][j].physicalX,
              // keys[i][j].physicalY,
              // keys[i][j].active
            // );
        }
    }
    byte header[]={(byte)'l',(byte)this->columns,(byte)this->rows};
    Serial.write(header,3);
    Serial.write((byte*)reportedKeys,n*sizeof(ReportedKey));
    Serial.print('@');
    delete reportedKeys;
}

void SystemVIKeyboard::setNormalKey() {
    //set normal key
}

void SystemVIKeyboard::setNormalKeyLayer() {
    //update normal key layer
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
}