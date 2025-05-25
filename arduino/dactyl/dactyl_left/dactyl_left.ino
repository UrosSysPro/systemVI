#include"Arduino.h"
#include "Keyboard.h"
#include "Wire.h"
// #define __DEBUG__

#define COLUMNS_NUMBER 7
#define ROWS_NUMBER 6
 
// int columns[]={3,2,0,4,5,11,14};
int columns[]={0,1,2,3,6,7,8};
int rows[]={9,10,11,12,15,14};

class LayerKey{
  public:
  int x,y;
  LayerKey(int x,int y){
    this->x=x;
    this->y=y;
  }
};

LayerKey layerKeys[]={LayerKey(3,5),LayerKey(4,5)};

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
      keys[i][j].currentlyDown='\0';
      keys[i][j].value[0]='\0';
      keys[i][j].value[1]='\0';
      keys[i][j].value[2]='\0';

      keysRight[i][j].pressed=false;
      keysRight[i][j].justChanged=false;
      keysRight[i][j].currentlyDown='\0';
      keysRight[i][j].value[0]='\0';
      keysRight[i][j].value[1]='\0';
      keysRight[i][j].value[2]='\0';
    }
  }

  //left side
  //row 0
  keys[6][0].value[0]=KEY_ESC;
  keys[5][0].value[0]='1';
  keys[5][0].value[1]=KEY_F1;
  keys[4][0].value[0]='2';
  keys[4][0].value[1]=KEY_F2;
  keys[3][0].value[0]='3';
  keys[3][0].value[1]=KEY_F3;
  keys[2][0].value[0]='4';
  keys[2][0].value[1]=KEY_F4;
  keys[1][0].value[0]='5';  
  keys[1][0].value[1]=KEY_F5;
  keys[0][0].value[0]='6';
  keys[0][0].value[1]=KEY_F6;
  //row 1
  keys[6][1].value[0]='`';
  keys[5][1].value[0]='q';
  keys[4][1].value[0]='w';
  keys[3][1].value[0]='e';
  keys[2][1].value[0]='r';
  keys[1][1].value[0]='t';
  //row 2
  keys[6][2].value[0]=KEY_TAB;
  keys[5][2].value[0]='a';
  keys[4][2].value[0]='s';
  keys[3][2].value[0]='d';
  keys[2][2].value[0]='f';
  keys[1][2].value[0]='g';
  keys[0][2].value[0]='`';
  //row 3
  keys[5][3].value[0]='z';
  keys[4][3].value[0]='x';
  keys[3][3].value[0]='c';
  keys[2][3].value[0]='v';
  keys[1][3].value[0]='b';
  //row 4
  keys[5][4].value[0]='\'';
  keys[4][4].value[0]='-';
  keys[3][4].value[0]='=';

  //thumb
  keys[0][4].value[0]=' ';
  keys[3][5].value[0]=KEY_LEFT_CTRL;
  keys[6][5].value[0]=KEY_LEFT_GUI;
  keys[0][3].value[0]=KEY_LEFT_SHIFT;
  keys[1][5].value[0]=KEY_LEFT_SHIFT;

  //right side
  //row 0
  keysRight[0][0].value[0]='7';
  keysRight[0][0].value[1]=KEY_F7;
  keysRight[1][0].value[0]='8';
  keysRight[1][0].value[1]=KEY_F8;
  keysRight[2][0].value[0]='9';
  keysRight[2][0].value[1]=KEY_F9;
  keysRight[3][0].value[0]='0';
  keysRight[3][0].value[1]=KEY_F10;
  keysRight[4][0].value[0]='-';
  keysRight[4][0].value[1]=KEY_F11;
  keysRight[5][0].value[0]='=';
  keysRight[5][0].value[1]=KEY_F12;
  keysRight[6][0].value[0]=KEY_BACKSPACE;
  //row 1 
  keysRight[1][1].value[0]='y';
  keysRight[2][1].value[0]='u';
  keysRight[3][1].value[0]='i';
  keysRight[4][1].value[0]='o';
  keysRight[5][1].value[0]='p';
  keysRight[6][1].value[0]='\\';
  //row 2 
  keysRight[0][2].value[0]=KEY_DELETE;
  keysRight[1][2].value[0]='h';
  keysRight[1][2].value[1]=KEY_LEFT_ARROW;
  keysRight[2][2].value[0]='j';
  keysRight[2][2].value[1]=KEY_DOWN_ARROW;
  keysRight[3][2].value[0]='k';
  keysRight[3][2].value[1]=KEY_UP_ARROW;
  keysRight[4][2].value[0]='l';
  keysRight[4][2].value[1]=KEY_RIGHT_ARROW;
  keysRight[5][2].value[0]=';';
  keysRight[6][2].value[0]='\n';
  //row 3 
  keysRight[1][3].value[0]='n';
  keysRight[2][3].value[0]='m';
  keysRight[3][3].value[0]=',';
  keysRight[4][3].value[0]='.';
  keysRight[5][3].value[0]='/';
  //row 4 
  keysRight[3][4].value[0]='[';
  keysRight[4][4].value[0]=']';
  keysRight[5][4].value[0]='\\';

  //thumb
  keysRight[2][4].value[0]=KEY_BACKSPACE;
  keysRight[1][5].value[0]=KEY_RIGHT_SHIFT;

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

  Key* key;
  int layer = 0;
  key=&keysRight[3][5];
  if(key->pressed){
    key->justChanged=false;
    layer = 1;
  }
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
            for(int k=layer;k>=0;k--){
              char c=keys[i][j].value[k];
              if(c){
                keys[i][j].currentlyDown=c;
                Keyboard.press(c);
                break;
              }
            }
          #endif
        }else{
          #ifdef __DEBUG__
            Serial.printf("release col: %2d row: %2d pins: %2d %2d\n",i,j,columns[i],rows[j]);
          #else
            if(keys[i][j].currentlyDown)Keyboard.release(keys[i][j].currentlyDown);
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
            for(int k=layer;k>=0;k--){
              char c=keysRight[i][j].value[k];
              if(c){
                keysRight[i][j].currentlyDown=c;
                Keyboard.press(c);
                break;
              }
            }
          #endif
        }else{
          #ifdef __DEBUG__
            Serial.printf("release col: %2d row: %2d pins: %2d %2d\n",i,j,columns[i],rows[j]);
          #else
            if(keysRight[i][j].currentlyDown)Keyboard.release(keysRight[i][j].currentlyDown);
          #endif
        }
      }
    }
  }
}




