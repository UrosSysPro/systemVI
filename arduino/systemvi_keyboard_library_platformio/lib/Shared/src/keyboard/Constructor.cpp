#include "SystemVIKeyboard.h"
#include <cstring>
#include "Arduino.h"
#include "keycaps/Keycap.h"

#ifdef ARDUINO_KEYBOARD
#include "Keyboard.h"
#endif

#ifdef ESP32_KEYBOARD

#endif



void SystemVIKeyboard::init(
    char* name,
    int columns,int rows,int* columnPins,int* rowPins,
    bool debugPrint,
    int reportedColumns,int reportedRows,
    int sdaPin,int sclPin, bool reportToI2C,bool readFromI2C,I2CRegion region
    ) {
    this->debugPrint=debugPrint;
    this->reportedColumns=reportedColumns;
    this->reportedRows=reportedRows;
    int length=(int)strlen(name);
    this->name=new char[length+1];
    for (int i=0;i<length;i++)this->name[i]=name[i];
    this->name[length]='\0';

    this->rows=rows;
    this->columns=columns;
    this->layerKeyPositionCount=0;
    this->snapTapPairCount=0;

    this->columnPins=new int[columns];
    for (int i=0;i<columns;i++)this->columnPins[i]=columnPins[i];

    this->rowPins=new int[rows];
    for (int i=0;i<rows;i++)this->rowPins[i]=rowPins[i];

    this->layerKeyPositions=new LayerKeyPosition[1];
    this->snapTapPairs=new SnapTapPair[1];

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

    this->printKeyEventsToSerial=false;

    Serial.begin(9600);
#ifdef ARDUINO_KEYBOARD
#warning "arudino keyboard started"
    Keyboard.begin();
#endif
}

SystemVIKeyboard::SystemVIKeyboard(char* name, int columns,int rows,int* columnPins,int* rowPins,bool debugPrint,int reportedColumns,int reportedRows,int sdaPin,int sclPin, bool reportToI2C,bool readFromI2C,I2CRegion region) {
    this->init(name,columns,rows,columnPins,rowPins,debugPrint,reportedColumns,reportedRows,sdaPin,sclPin,reportToI2C,readFromI2C,region);
}
SystemVIKeyboard::SystemVIKeyboard(char* name, int columns,int rows,int* columnPins,int* rowPins,bool debugPrint,int reportedColumns,int reportedRows) {
    this->init(name,columns,rows,columnPins,rowPins,debugPrint,reportedColumns,reportedRows,-1,-1,false,false,I2CRegion(-1,-1,-1,-1));
}

SystemVIKeyboard::SystemVIKeyboard(char* name, int columns,int rows,int* columnPins,int* rowPins) {
    this->init(name,columns,rows,columnPins,rowPins,false,columns,rows,-1,-1,false,false,I2CRegion(-1,-1,-1,-1));
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
