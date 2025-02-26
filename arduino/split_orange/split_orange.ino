#include"Arduino.h"
#include "Wire.h"
#define COLUMNS_NUMBER 7
#define ROWS_NUMBER 3

int columns[]={15,22,13,12,11,10,9};
int rows[]={8,7,6};

struct Key{
  char value;
  bool pressed;//=false;
  bool justChanged;//=false;
};

const byte thisAddress = 9;
const byte otherAddress = 8;


Key keys[COLUMNS_NUMBER][ROWS_NUMBER]={
  {{' ',false,false},{'a',false,false},{'a',false,false}},
  {{'t',false,false},{'g',false,false},{'b',false,false}},
  {{'r',false,false},{'f',false,false},{'v',false,false}},
  {{'e',false,false},{'d',false,false},{'c',false,false}},
  {{'w',false,false},{'s',false,false},{'x',false,false}},
  {{'q',false,false},{'a',false,false},{'z',false,false}},
  {{'a',false,false},{'a',false,false},{'a',false,false}}
};

void setup() {
  pinMode(25, OUTPUT);
  for(int i=0;i<COLUMNS_NUMBER;i++){
    int pin=columns[i];
    pinMode(pin, OUTPUT);
    digitalWrite(pin, 1);
  }
  for(int i=0;i<ROWS_NUMBER;i++){
    int pin = rows[i];
    pinMode(pin, INPUT_PULLUP);
  }
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
  Wire.begin(thisAddress);
  Wire.onRequest(onRequest);
  // Keyboard.begin();
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
          Serial.print("key pressed  ");
          Serial.print(i);
          Serial.print(" ");
          Serial.print(j);
          Serial.println("");
          // Keyboard.press(keys[i][j].value);
        }else{
          Serial.print("key released ");
          Serial.print(i);
          Serial.print(" ");
          Serial.print(j);
          Serial.println("");
          // Keyboard.release(keys[i][j].value);
        }
      }
    }
  }
}

void onRequest(){
  Wire.write("hello from orange");
}