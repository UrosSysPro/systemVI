#include"Arduino.h"
#include "Keyboard.h"
#include "Wire.h"

#define COLUMNS_NUMBER 7
#define ROWS_NUMBER 3

int columns[]={18,19,20,21,22,26,27};
int rows[]={1,0,2};

struct Key{
  char value;
  bool pressed;//=false;
  bool justChanged;//=false;
};

const byte thisAddress = 8; 
const byte otherAddress = 9;

Key keys[COLUMNS_NUMBER][ROWS_NUMBER]={
  {{' ',false,false},{KEY_LEFT_SHIFT,false,false},{'a',false,false}},
  {{'t',false,false},{'g',false,false},{'b',false,false}},
  {{'r',false,false},{'f',false,false},{'v',false,false}},
  {{'e',false,false},{'d',false,false},{'c',false,false}},
  {{'w',false,false},{'s',false,false},{'x',false,false}},
  {{'q',false,false},{'a',false,false},{'z',false,false}},
  {{KEY_ESC,false,false},{KEY_TAB,false,false},{KEY_LEFT_CTRL,false,false}}
};

Key keysRight[COLUMNS_NUMBER][ROWS_NUMBER]={
  {{' ',false,false},{KEY_RIGHT_ALT,false,false},{KEY_LEFT_GUI,false,false}},
  {{'y',false,false},{'h',false,false},{'n',false,false}},
  {{'u',false,false},{'j',false,false},{'m',false,false}},
  {{'i',false,false},{'k',false,false},{',',false,false}},
  {{'o',false,false},{'l',false,false},{'.',false,false}},
  {{'p',false,false},{';',false,false},{'/',false,false}},
  {{KEY_BACKSPACE,false,false},{KEY_RETURN,false,false},{'z',false,false}}
};

void checkForConnectedI2CDevices(){
  byte count = 0;
  for (byte i = 1; i < 120; i++) {
    if(i==thisAddress)continue;
    Wire.beginTransmission (i);
    if (Wire.endTransmission () == 0){
      Serial.print ("Found address: ");
      Serial.print (i, DEC);
      Serial.print (" (0x");
      Serial.print (i, HEX);
      Serial.println (")");
      count++;
      delay (10);
    }
  }
  Serial.println ("Done.");
  Serial.print ("Found ");
  Serial.print (count, DEC);
  Serial.println (" device(s).");
}


void requestMessage(){
  Wire.requestFrom(otherAddress, 4,true); 
  unsigned int state=0;
  Wire.readBytes((byte*)&state,4);
  // Serial.println(state);
  for(int i=COLUMNS_NUMBER-1;i>=0;i--){
    for(int j=ROWS_NUMBER-1;j>=0;j--){
      // state = state << 1;
      // state = state | (int)keys[i][j].pressed;
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

  Wire.begin();
  Keyboard.begin();
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
  
  // Key* key;
  // key=&keys[0][1];
  // if(key->justChanged){
  //   if(key->pressed)requestMessage();
  //   key->justChanged=false;
  // }
  // key=&keys[0][2];
  // if(key->justChanged){
  //   if(key->pressed)checkForConnectedI2CDevices();
  //   key->justChanged=false;
  // }

  for(int i=0;i<COLUMNS_NUMBER;i++){
    for(int j=0;j<ROWS_NUMBER;j++){
      if(keys[i][j].justChanged){
        keys[i][j].justChanged=false;
        if(keys[i][j].pressed){
          // Serial.print("key pressed  ");
          // Serial.print(i);
          // Serial.print(" ");
          // Serial.print(j);
          // Serial.println("");
          Keyboard.press(keys[i][j].value);
        }else{
          // Serial.print("key released ");
          // Serial.print(i);
          // Serial.print(" ");
          // Serial.print(j);
          // Serial.println("");
          Keyboard.release(keys[i][j].value);
        }
      }
    }
  }

  requestMessage();

  for(int i=0;i<COLUMNS_NUMBER;i++){
    for(int j=0;j<ROWS_NUMBER;j++){
      if(keysRight[i][j].justChanged){
        keysRight[i][j].justChanged=false;
        if(keysRight[i][j].pressed){
          // Serial.print("key pressed  ");
          // Serial.print(6+i);
          // Serial.print(" ");
          // Serial.print(j);
          // Serial.println("");
          Keyboard.press(keysRight[i][j].value);
        }else{
          // Serial.print("key released ");
          // Serial.print(6+i);
          // Serial.print(" ");
          // Serial.print(j);
          // Serial.println(keysRight[i][j].value);
          Keyboard.release(keysRight[i][j].value);
        }
      }
    }
  }
}




