#include"Arduino.h"
#include "Wire.h"
#define __DEBUG__

#define COLUMNS_NUMBER 7
#define ROWS_NUMBER 6

int columns[]={0,1,2,3,28,27,26};
int rows[]={6,7,8,9,17,15};

struct Key{
  bool pressed,justChanged;
};

const byte thisAddress = 9;
const byte otherAddress = 8;


Key keys[COLUMNS_NUMBER][ROWS_NUMBER];

void sendCurrentState(){
  uint64_t state=0;
  for(int i=0;i<COLUMNS_NUMBER;i++){
    for(int j=0;j<ROWS_NUMBER;j++){
      state = state << 1;
      state = state | (uint64_t)keys[i][j].pressed;
    }
  }
  Wire.beginTransmission(otherAddress);
  Wire.write((byte*)&state,8);
  Wire.endTransmission();
}

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

  pinMode(25,OUTPUT);
  digitalWrite(25,1);

  delay(1000);

  #ifdef __DEBUG__
    Serial.begin(9600);
    Wire.setSDA(4);
    Wire.setSCL(5);
    Wire.begin();
  #else
    Wire.setSDA(4);
    Wire.setSCL(5);
    Wire.begin();
  #endif
}

void loop() {
  for(int j=0;j<ROWS_NUMBER;j++){
    int rowPin=rows[j];
    digitalWrite(rowPin, 0);
    delayMicroseconds(500);
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

  sendCurrentState();
}



