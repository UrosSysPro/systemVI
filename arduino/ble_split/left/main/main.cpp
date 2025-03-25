#include"lib/systemvi/Arduino.hpp"
#include"lib/systemvi/Keyboard.hpp"
#include<stdio.h>
#define COLUMNS_NUMBER 7
#define ROWS_NUMBER 3

int rows[]={9,10,20};
int columns[]={0,1,2,3,4,5,6};

struct Key{
  char l0,l1,l2;
  bool pressed,justChanged;
};

Key keys[COLUMNS_NUMBER][ROWS_NUMBER];

void setup() {
  for(int i=0;i<COLUMNS_NUMBER;i++){
    for(int j=0;j<ROWS_NUMBER;j++){
		keys[i][j].pressed=false;
		keys[i][j].justChanged=false;
    }
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
}

void loop() {
  for(int j=0;j<ROWS_NUMBER;j++){
    int rowPin=rows[j];
    digitalWrite(rowPin, 0);
	delay(1);
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
        	printf("key pressed %d %d\n",i,j);
        }else{
        	printf("key released %d %d\n",i,j);
        }
      }
    }
  }
}




