#include"Arduino.h"
#include "Keyboard.h"
#include "Wire.h"
// #define __DEBUG__

#define COLUMNS_NUMBER 7
#define ROWS_NUMBER 3
 
// int columns[]={3,2,0,4,5,11,14};
int columns[]={14,11,5,4,0,2,3};
int rows[]={27,13,21};

struct Key{
  char value[3];
  bool pressed;
  bool justChanged;
  char currentlyDown=0;
  unsigned long lastKeyPress=0;
};

const byte thisAddress = 8; 
const byte otherAddress = 9;

Key keys[COLUMNS_NUMBER][ROWS_NUMBER]={
  {{{' ',' ',' '},false,false},{{KEY_LEFT_SHIFT,KEY_LEFT_SHIFT,KEY_LEFT_SHIFT},false,false},{{'a','a','a'},false,false}},
  {{{'t','5',' '},false,false},{{'g',' ',' '},false,false},{{'b',' ',' '},false,false}},
  {{{'r','4',' '},false,false},{{'f',' ',' '},false,false},{{'v',' ',' '},false,false}},
  {{{'e','3',' '},false,false},{{'d',' ',' '},false,false},{{'c',' ',' '},false,false}},
  {{{'w','2',' '},false,false},{{'s',' ',' '},false,false},{{'x',' ',' '},false,false}},
  {{{'q','1',' '},false,false},{{'a',' ',' '},false,false},{{'z',' ',' '},false,false}},
  {{{KEY_ESC,KEY_ESC,KEY_ESC},false,false},{{KEY_TAB,KEY_TAB,KEY_TAB},false,false},{{KEY_LEFT_CTRL,KEY_LEFT_CTRL,KEY_LEFT_CTRL},false,false}}
};

Key keysRight[COLUMNS_NUMBER][ROWS_NUMBER]={
  {{{KEY_BACKSPACE,KEY_BACKSPACE,KEY_BACKSPACE},false,false},{{' ',' ',' '},false,false},{{' ',' ',' '},false,false}},
  {{{'y','6',' '} ,false,false},{{'h'       ,KEY_LEFT_ARROW ,' ' },false,false},{{'n',' ',' '},false,false}},
  {{{'u','7','{'} ,false,false},{{'j'       ,KEY_DOWN_ARROW ,'[' },false,false},{{'m',' ','('},false,false}},
  {{{'i','8','}'} ,false,false},{{'k'       ,KEY_UP_ARROW   ,']' },false,false},{{',',' ',')'},false,false}},
  {{{'o','9','-'} ,false,false},{{'l'       ,KEY_RIGHT_ARROW,'\''},false,false},{{'.',' ',' '},false,false}},
  {{{'p','0','+'} ,false,false},{{';'       ,' '            ,'`' },false,false},{{'/',' ',' '},false,false}},
  {{{'\\','=',' '} ,false,false},{{KEY_RETURN,' '            ,' ' },false,false},{{'z',' ',' '},false,false}}
};

void onMessage( int size){
  unsigned int state=0;
  Wire.readBytes((byte*)&state,4);
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
  #ifdef __DEBUG__
    Serial.begin(9600);
    Wire.setSDA(8);
    Wire.setSCL(9);
    Wire.begin(thisAddress);
    Wire.onReceive(onMessage);
  #else
    Wire.setSDA(8);
    Wire.setSCL(9);
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
      // unsigned long currentTime=millis();
      // if(newState==true){
      //   if(currentTime-keys[i][j].lastKeyPress<10){
      //     //click detected in span of 10ms
      //     newState=false;
      //   }else{
      //     //click detedted well after 10ms
      //     keys[i][j].lastKeyPress=currentTime;
      //   }
      // }
      if(newState!=keys[i][j].pressed)keys[i][j].justChanged=true;
      keys[i][j].pressed=digitalRead(columnPin) == 0;
    }
    digitalWrite(rowPin, 1);
  }

  Key* key;
  int layer = 0;
  key=&keysRight[0][1];
  if(key->pressed){
    key->justChanged=false;
    layer = 1;
  }
  key=&keysRight[0][2];
  if(key->pressed){
    key->justChanged=false;
    layer = 2;
  }

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
  delay(10);
}




