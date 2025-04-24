#include"Arduino.h"
#include "Wire.h"
// #define __DEBUG__

#define COLUMNS_NUMBER 7
#define ROWS_NUMBER 3

int columns[]={9,10,11,12,13,22,15};
int rows[]={6,7,8};

struct Key{
  bool pressed;
};

const byte thisAddress = 9;
const byte otherAddress = 8;


Key keys[COLUMNS_NUMBER][ROWS_NUMBER]={
  {{false},{false},{false}},
  {{false},{false},{false}},
  {{false},{false},{false}},
  {{false},{false},{false}},
  {{false},{false},{false}},
  {{false},{false},{false}},
  {{false},{false},{false}}
};

void setup() {
  for(int i=0;i<COLUMNS_NUMBER;i++){
    int pin=columns[i];
    pinMode(pin, INPUT_PULLUP);
  }
  for(int i=0;i<ROWS_NUMBER;i++){
    int pin = rows[i];
    pinMode(pin, OUTPUT);
    digitalWrite(pin, 1);
  }

  #ifdef __DEBUG__
    Serial.begin(9600);
  #else
    Wire.begin();
  #endif
}

void loop() {
  for(int j=0;j<ROWS_NUMBER;j++){
    int rowPin=rows[j];
    digitalWrite(rowPin, 0);
    delayMicroseconds(1000);
    for(int i=0;i<COLUMNS_NUMBER;i++){
      int columnPin=columns[i];
      keys[i][j].pressed=digitalRead(columnPin) == 0;
    }
    digitalWrite(rowPin, 1);
  }

  #ifdef __DEBUG__
    for(int i=0;i<COLUMNS_NUMBER;i++){
      for(int j=0;j<ROWS_NUMBER;j++){
          if(keys[i][j].pressed){
            Serial.printf("press   col: %2d row: %2d pins: %2d %2d\n",i,j,columns[i],rows[j]);
        }
      }
    }
  #endif

  unsigned int state=0;
  for(int i=0;i<COLUMNS_NUMBER;i++){
    for(int j=0;j<ROWS_NUMBER;j++){
      state = state << 1;
      state = state | (int)keys[i][j].pressed;
    }
  }
  Wire.beginTransmission(otherAddress);
  Wire.write((byte*)&state,4);
  Wire.endTransmission();
}

// void onRequest(){
//   unsigned int state=0;
//   for(int i=0;i<COLUMNS_NUMBER;i++){
//     for(int j=0;j<ROWS_NUMBER;j++){
//       state = state << 1;
//       state = state | (int)keys[i][j].pressed;
//     }
//   }
//   Wire.write((byte*)&state,4);
// }



