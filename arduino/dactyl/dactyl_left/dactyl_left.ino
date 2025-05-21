#include"Arduino.h"
#include "Keyboard.h"
#include "Wire.h"
#define __DEBUG__

#define COLUMNS_NUMBER 7
#define ROWS_NUMBER 6
 
// int columns[]={3,2,0,4,5,11,14};
int columns[]={0,1,2,3,6,7,8};
int rows[]={9,10,11,12,15,14};

struct Key{
  char value[3];
  bool pressed;
  bool justChanged;
  char currentlyDown=0;
};

const byte thisAddress = 8; 
const byte otherAddress = 9;

Key keys[COLUMNS_NUMBER][ROWS_NUMBER];

Key keysRight[COLUMNS_NUMBER][ROWS_NUMBER];

void onMessage( int size){
  uint64_t state=0;
  Wire.readBytes((byte*)&state,8);
  for(int i=COLUMNS_NUMBER-1;i>=0;i--){
    for(int j=ROWS_NUMBER-1;j>=0;j--){
      bool oldValue=keysRight[i][j].pressed;
      keysRight[i][j].pressed = (state & 1)==1;
      if(oldValue!=keysRight[i][j].pressed){
        keysRight[i][j].justChanged=true;
      }
      state = state >> 1;
    }
  }
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

  for(int i=0;i<COLUMNS_NUMBER;i++){
    for(int j=0;j<ROWS_NUMBER;j++){
      keys[i][j].pressed=false;
      keys[i][j].justChanged=false;
      keysRight[i][j].pressed=false;
      keysRight[i][j].justChanged=false;
    }
  }
  #ifdef __DEBUG__
    Serial.begin(9600);
    Wire.setSDA(4);
    Wire.setSCL(5);
    Wire.begin(thisAddress);
    Wire.onReceive(onMessage);
  #else
    Wire.setSDA(4);
    Wire.setSCL(5);
    Wire.begin(thisAddress);
    Wire.onReceive(onMessage);
    Keyboard.begin();
  #endif
}

void loop() {
  for(int j=0;j<ROWS_NUMBER;j++){
    int rowPin=rows[j];
    digitalWrite(rowPin, 0);
    delayMicroseconds(500);
    for(int i=0;i<COLUMNS_NUMBER;i++){
      int columnPin=columns[i];
      bool newState=digitalRead(columnPin) == 0;
      if(newState!=keys[i][j].pressed)keys[i][j].justChanged=true;
      keys[i][j].pressed=newState;
    }
    digitalWrite(rowPin, 1);
  }

  // Key* key;
  // int layer = 0;
  // key=&keysRight[0][1];
  // if(key->pressed){
  //   key->justChanged=false;
  //   layer = 1;
  // }
  // key=&keysRight[0][2];
  // if(key->pressed){
  //   key->justChanged=false;
  //   layer = 2;
  // }

  for(int i=0;i<COLUMNS_NUMBER;i++){
    for(int j=0;j<ROWS_NUMBER;j++){
      if(keys[i][j].justChanged){
        keys[i][j].justChanged=false;
        if(keys[i][j].pressed){
          #ifdef __DEBUG__
            Serial.printf("press   col: %2d row: %2d pins: %2d %2d\n",i,j,columns[i],rows[j]);
          #else
            keys[i][j].currentlyDown=keys[i][j].value[layer];
            Keyboard.press(keys[i][j].value[layer]);
          #endif
        }else{
          #ifdef __DEBUG__
            Serial.printf("release col: %2d row: %2d pins: %2d %2d\n",i,j,columns[i],rows[j]);
          #else
            Keyboard.release(keys[i][j].currentlyDown);
          #endif
        }
      }
    }
  }

  for(int i=0;i<COLUMNS_NUMBER;i++){
    for(int j=0;j<ROWS_NUMBER;j++){
      if(keysRight[i][j].justChanged){
        keysRight[i][j].justChanged=false;
        if(keysRight[i][j].pressed){
          #ifdef __DEBUG__
            Serial.printf("press   col: %2d row: %2d pins: %2d %2d\n",i,j,columns[i],rows[j]);
          #else
            keysRight[i][j].currentlyDown=keysRight[i][j].value[layer];
            Keyboard.press(keysRight[i][j].value[layer]);
          #endif
        }else{
          #ifdef __DEBUG__
            Serial.printf("release col: %2d row: %2d pins: %2d %2d\n",i,j,columns[i],rows[j]);
          #else
            Keyboard.release(keysRight[i][j].currentlyDown);
          #endif
        }
      }
    }
  }
}




