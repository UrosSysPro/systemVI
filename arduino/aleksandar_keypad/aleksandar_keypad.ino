#include"Arduino.h"

#define COLUMNS_NUMBER 4
#define ROWS_NUMBER 5

// int columns[]={5,6,7,8,9};
// int rows[]={0,1,2,3};

int rows[]={5,6,7,8,9};
int columns[]={0,1,2,3};

struct Key{
  char value;
  bool pressed;
  bool justChanged;
};

Key keys[COLUMNS_NUMBER][ROWS_NUMBER]={
  {{' ',false,false},{' ',false,false},{' ',false,false},{' ',false,false},{' ',false,false}},
  {{' ',false,false},{' ',false,false},{' ',false,false},{' ',false,false},{' ',false,false}},
  {{' ',false,false},{' ',false,false},{' ',false,false},{' ',false,false},{' ',false,false}},
  {{' ',false,false},{' ',false,false},{' ',false,false},{' ',false,false},{' ',false,false}},
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

  Serial.begin(9600);
}

void loop() {
  for(int j=0;j<ROWS_NUMBER;j++){
    int rowPin=rows[j];
    digitalWrite(rowPin, 0);
    delayMicroseconds(1000);
    for(int i=0;i<COLUMNS_NUMBER;i++){
      int columnPin=columns[i];
      bool newState=digitalRead(columnPin) == 0;
      if(newState!=keys[i][j].pressed)keys[i][j].justChanged=true;
      keys[i][j].pressed=digitalRead(columnPin) == 0;
    }
    digitalWrite(rowPin, 1);
  }


  for(int i=0;i<COLUMNS_NUMBER;i++){
    for(int j=0;j<ROWS_NUMBER;j++){
      if(keys[i][j].justChanged){
        keys[i][j].justChanged=false;
        if(keys[i][j].pressed){
          Serial.printf("key pressed %d %d\n",i,j);
        }else{
          Serial.printf("key released %d %d\n",i,j);
        }
      }
    }
  }
}




