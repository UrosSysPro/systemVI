#include"Arduino.h"
#include"Keyboard.h"
#define COLUMNS_NUMBER 4
#define ROWS_NUMBER 5

// int columns[]={5,6,7,8,9};
// int rows[]={0,1,2,3};

int rows[]={2,3,4,5,6};
int columns[]={10,16,14,15};

class Key{
  public:
  char value;
  bool pressed;
  bool justChanged;
  virtual void onPress(){
    Serial.println("defaultkeypress");
  }
  virtual void onRelease(){
    Serial.println("defaultkeyrelease");
    Keyboard.releaseAll();
  }
};

class CopyKey:public Key{
  public:
  void onPress()override{
    Serial.println("copy");
    Keyboard.press(KEY_LEFT_CTRL);
    Keyboard.press('c');
    delay(100);
    Keyboard.releaseAll();
  }
};

class PasteKey:public Key{
  public:
  void onPress()override{
    Serial.println("paste");
    Keyboard.press(KEY_LEFT_CTRL);
    Keyboard.press('v');
    delay(100);
    Keyboard.releaseAll();
  }
};
class UndoKey:public Key{
  public:
  void onPress()override{
    Serial.println("undo");
        Keyboard.press(KEY_LEFT_CTRL);
    Keyboard.press('z');
    delay(100);
    Keyboard.releaseAll();
  }
};
class RedoKey:public Key{
  public:
  void onPress()override{
    Serial.println("Redo");
    Keyboard.press(KEY_LEFT_CTRL);
    Keyboard.press(KEY_LEFT_SHIFT);
    Keyboard.press('z');
    delay(100);
    Keyboard.releaseAll();
  }
};
class BSIKey:public Key{
  public:
  void onPress()override{
    Serial.println("BSI");
    Keyboard.press(']');
  }
};
class BSDKey:public Key{
  public:
  void onPress()override{
    Serial.println("BSD");
    Keyboard.press('[');
  }
};
class ZOOMKey:public Key{
  public:
  void onPress()override{
    Serial.println("zoom");
    Keyboard.press('z');
  }
};
class BrushKey:public Key{
  public:
  void onPress()override{
    Serial.println("brush");
  }
};
class EraserKey:public Key{
  public:
  void onPress()override{
    Serial.println("eraser");

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
      if(i==0 && j==3){
        keys[i][j]=new CopyKey();
        }
      else if(i==3 && j ==1){
        keys[i][j]=new BSIKey();
      }
      else if(i==3 && j ==2){
        keys[i][j]=new BSDKey();
      }
      else if(i==1 && j ==3){
        keys[i][j]=new PasteKey();
      }
      else if(i==0 && j ==1){
        keys[i][j]=new UndoKey();
      }
      else if(i==1 && j ==1){
        keys[i][j]=new RedoKey();
      }
      else if(i==0 && j ==2){
        keys[i][j]=new BrushKey();
      }
      else if(i==1 && j ==2){
        keys[i][j]=new EraserKey();
      }
      else if(i==3 && j==4){
        keys[i][j]=new ZOOMKey();
      }
      else{
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
        //Serial.println("undo");
        // Serial.println("key pressed");
        //Serial.print(columns[i]);
        //Serial.print(rows[j]);
        //Serial.println(" ");
        }else{
          keys[i][j]->onRelease();
        //  Serial.printf("key released %d %d\n",i,j);
        }
      }
    }
  }
}




