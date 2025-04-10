#include"Arduino.h"
#include "Keyboard.h"
//#define DEBUG 

#define COLUMNS_NUMBER 12
#define ROWS_NUMBER 4
 
int columns[]={0,1,2,3,4,5,6,8,7,9,10,11};
int rows[]={12,13,14,15};

struct Key{
  bool pressed,justChanged;
  char value,fn;
};

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
        keys[i][j].value='\0';
        keys[i][j].fn='\0';
      }
    }
  }

//red 0///////////////////////////////////////////////////////////////
  keys[0][1].value='`';
  keys[1][1].value='q';
  keys[2][1].value='w';
  keys[3][1].value='e';
  keys[4][1].value='r';
  keys[5][1].value='t';
  keys[6][1].value='y';
  keys[7][1].value='u';
  keys[8][1].value='i';
  keys[9][1].value='o';
  keys[10][1].value='p';
  keys[11][1].value=KEY_BACKSPACE;
//red 1/////////////////////////////////////////////////////////////
  keys[0][0].value=KEY_TAB;
  keys[1][0].value='a';
  keys[2][0].value='s';
  keys[3][0].value='d';
  keys[4][0].value='f';
  keys[5][0].value='g';
  keys[6][0].value='h';
  keys[7][0].value='j';
  keys[8][0].value='k';
  keys[9][0].value='l';
  keys[11][0].value=KEY_RETURN;
//red 2/////////////////////////////////////////////////////////////
  keys[0][2].value=KEY_LEFT_SHIFT;
  keys[2][2].value='z';
  keys[3][2].value='x';
  keys[4][2].value='c';
  keys[5][2].value='v';
  keys[6][2].value='b';
  keys[7][2].value='n';
  keys[8][2].value='m';
  keys[9][2].value=',';
  keys[10][2].value=KEY_RIGHT_SHIFT;
  keys[11][2].value='a';
//red 3/////////////////////////////////////////////////////////////
  keys[0][3].value=KEY_LEFT_CTRL;
  keys[2][3].value='a';
  keys[3][3].value='a';
  keys[6][3].value=' ';
  keys[10][3].value=KEY_RIGHT_ALT;
  keys[11][3].value=KEY_RIGHT_GUI;


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

  bool fn=false;
  if(keys[3][4].pressed){
    keys[3][4].justChanged=false;
    fn=true;
  }

  for(int i=0;i<COLUMNS_NUMBER;i++){
    for(int j=0;j<ROWS_NUMBER;j++){
      if(keys[i][j].justChanged){
        keys[i][j].justChanged=false;
        if(keys[i][j].pressed){
          #ifdef DEBUG
            Serial.printf("pressed  %3d %3d\n",columns[i],rows[j]);
            Serial.printf("pressed  %3d %3d\n",i,j);
          #else
          if(fn&&keys[i][j].fn){
            Keyboard.press(keys[i][j].fn);
          }else{
            Keyboard.press(keys[i][j].value);
          }
          #endif
        }else{
          #ifdef DEBUG
            Serial.printf("released %3d %3d\n",columns[i],rows[j]);
            Serial.printf("released %3d %3d\n",i,j);
          #else
          if(fn&&keys[i][j].fn){
            Keyboard.release(keys[i][j].fn);
          }else{
            Keyboard.release(keys[i][j].value);
          }
          #endif
        } 
      }
    }
  }
}




