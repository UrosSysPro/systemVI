#include"Arduino.h"
#include "Keyboard.h"
// #define DEBUG 

#define COLUMNS_NUMBER 14
#define ROWS_NUMBER 5
 
int columns[]={0,1,2,3,4,5,6,7,8,9,10,11,12,13};
int rows[]={14,15,16,17,18};

struct Key{
  bool pressed,justChanged;
  char value;
};

const byte thisAddress = 8; 
const byte otherAddress = 9;

Key keys[COLUMNS_NUMBER][ROWS_NUMBER];

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

  for(int i=0;i<COLUMNS_NUMBER;i++){
    for(int j=0;j<ROWS_NUMBER;j++){
      if(keys[i][j].justChanged){
        keys[i][j].justChanged=false;
        keys[i][j].pressed=false;
        keys[i][j].value=' ';
      }
    }
  }

//red 0///////////////////////////////////////////////////////////////
  keys[13][0].value=KEY_ESC;
  keys[12][0].value='1';
  keys[11][0].value='2';
  keys[10][0].value='3';
  keys[9][0].value='4';
  keys[8][0].value='5';
  keys[7][0].value='6';
  keys[6][0].value='7';
  keys[5][0].value='8';
  keys[4][0].value='9';
  keys[3][0].value='0';
  keys[2][0].value='-';
  keys[1][0].value='=';
  keys[0][0].value=KEY_BACKSPACE;
//red 1/////////////////////////////////////////////////////////////
  keys[13][1].value=KEY_TAB;
  keys[12][1].value='q';
  keys[11][1].value='w';
  keys[10][1].value='e';
  keys[9][1].value='r';
  keys[8][1].value='t';
  keys[7][1].value='y';
  keys[6][1].value='u';
  keys[5][1].value='i';
  keys[4][1].value='o';
  keys[3][1].value='p';
  keys[2][1].value='[';
  keys[1][1].value=']';
  keys[0][1].value='\\';
//red 2/////////////////////////////////////////////////////////////
  keys[13][2].value=KEY_CAPS_LOCK;
  keys[12][2].value='a';
  keys[11][2].value='s';
  keys[10][2].value='d';
  keys[9][2].value='f';
  keys[8][2].value='g';
  keys[7][2].value='h';
  keys[6][2].value='j';
  keys[5][2].value='k';
  keys[4][2].value='l';
  keys[3][2].value=';';
  keys[2][2].value='\'';
  keys[0][2].value=KEY_RETURN;
//red 3/////////////////////////////////////////////////////////////
  keys[13][3].value=KEY_LEFT_SHIFT;
  keys[12][3].value='z';
  keys[11][3].value='x';
  keys[10][3].value='c';
  keys[9][3].value='v';
  keys[8][3].value='b';
  keys[7][3].value='n';
  keys[6][3].value='m';
  keys[5][3].value=',';
  keys[4][3].value='.';
  keys[3][3].value='/';
  keys[1][3].value=KEY_RIGHT_SHIFT;
//red 4/////////////////////////////////////////////////////////////
  keys[13][4].value=KEY_LEFT_CTRL;
  keys[12][4].value=KEY_LEFT_GUI;
  keys[11][4].value=KEY_LEFT_ALT;
  keys[8][4].value=' ';
  keys[4][4].value=KEY_RIGHT_ALT;
  keys[3][4].value=' ';
  keys[2][4].value=KEY_MENU;
  keys[1][4].value=KEY_RIGHT_CTRL;

  #ifdef DEBUG
    Serial.begin(9600);
  #else
    Keyboard.begin();
  #endif
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
      keys[i][j].pressed=newState;
    }
    digitalWrite(rowPin, 1);
  }

  for(int i=0;i<COLUMNS_NUMBER;i++){
    for(int j=0;j<ROWS_NUMBER;j++){
      if(keys[i][j].justChanged){
        keys[i][j].justChanged=false;
        if(keys[i][j].pressed){
          #ifdef DEBUG
            Serial.printf("pressed  %3d %3d\n",i,j);
          #else
            Keyboard.press(keys[i][j].value);
          #endif
        }else{
          #ifdef DEBUG
            Serial.printf("released %3d %3d\n",i,j);
          #else
            Keyboard.release(keys[i][j].value);
          #endif
        } 
      }
    }
  }
}




