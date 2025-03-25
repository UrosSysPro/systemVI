#include"lib/systemvi/Arduino.hpp"
#include"lib/systemvi/Keyboard.hpp"
#include<stdio.h>
#define COLUMNS_NUMBER 4
#define ROWS_NUMBER 5

// int columns[]={5,6,7,8,9};
// int rows[]={0,1,2,3};

int rows[]={5,6,9,7,8};
int columns[]={1,0,2,3};

class Key{
  public:
  char value;
  bool pressed;
  bool justChanged;
  virtual void onPress(){
  //  Serial.println("defaultkeypress");
  	printf("defaultkeypress");
  }
  virtual void onRelease(){
  	printf("defaultkeyrelease");

	  //  Serial.println("defaultkeyrelease");
  }
};

class CopyKey:public Key{
  public:
  void onPress()override{
  //  Serial.println("copy");
  }
};

class PasteKey:public Key{
  public:
  void onPress()override{
  //  Serial.println("paste");
  }
};
class UndoKey:public Key{
  public:
  void onPress()override{
  //  Serial.println("undo");
  }
};
class RedoKey:public Key{
  public:
  void onPress()override{
  //  Serial.println("Redo");
  }
};
class BSIKey:public Key{
  public:
  void onPress()override{
  //  Serial.println("BSI");
  }
};
class BSDKey:public Key{
  public:
  void onPress()override{
  //  Serial.println("BSD");
  }
};
class ZOOMKey:public Key{
  public:
  void onPress()override{
  //  Serial.println("zoom");
  }
};

//Key keys[COLUMNS_NUMBER][ROWS_NUMBER];
/*={
  {{' ',false,false},{' ',false,false},{' ',false,false},{' ',false,false},{' ',false,false}},
  {{' ',false,false},{' ',false,false},{' ',false,false},{' ',false,false},{' ',false,false}},
  {{' ',false,false},{' ',false,false},{' ',false,false},{' ',false,false},{' ',false,false}},
  {{' ',false,false},{' ',false,false},{' ',false,false},{' ',false,false},{' ',false,false}},
};*/

Key ***keys;

void setup() {

  keys=new Key**[COLUMNS_NUMBER];
  for(int i=0;i<COLUMNS_NUMBER;i++){
    keys[i]=new Key*[ROWS_NUMBER];
    for(int j=0;j<ROWS_NUMBER;j++){
      if(i==0 && j==2){
        keys[i][j]=new CopyKey();
      
      }else{
        keys[i][j]=new Key();
      }
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

//  Serial.begin(9600);
}

void loop() {
  for(int j=0;j<ROWS_NUMBER;j++){
    int rowPin=rows[j];
    digitalWrite(rowPin, 0);
    delayMicroseconds(1000);
    for(int i=0;i<COLUMNS_NUMBER;i++){
      int columnPin=columns[i];
      bool newState=digitalRead(columnPin) == 0;
      if(newState!=keys[i][j]->pressed)keys[i][j]->justChanged=true;
      keys[i][j]->pressed=digitalRead(columnPin) == 0;
    }
    digitalWrite(rowPin, 1);
  }


  for(int i=0;i<COLUMNS_NUMBER;i++){
    for(int j=0;j<ROWS_NUMBER;j++){
      if(keys[i][j]->justChanged){
        keys[i][j]->justChanged=false;
        if(keys[i][j]->pressed){
          keys[i][j]->onPress();
        //  Serial.printf("key pressed %d %d\n",i,j);
        }else{
          keys[i][j]->onRelease();
        //  Serial.printf("key released %d %d\n",i,j);
        }
      }
    }
  }
}




