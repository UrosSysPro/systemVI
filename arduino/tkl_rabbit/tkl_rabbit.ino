#include"Arduino.h"
#include "Keyboard.h"
#include<string>
// #define DEBUG

#define COLUMNS_NUMBER 17
#define ROWS_NUMBER 6
 
int columns[]={16,17,18,19,20,21,22,26,27,28,0,1,2,3,4,5,6};
int rows[]={7,8,9,10,11,12};

struct Key{
  bool pressed,justChanged;
  char value[4],currentlyDown;
};
class ReportedKey{
  public:
  byte x,y,value[4];
  ReportedKey(){}
  ReportedKey(byte x,byte y,byte* value){
    this->x=x;
    this->y=y;
    for(int i=0;i<4;i++){
      this->value[i]=value[i];
    }
  }
};

const byte thisAddress = 8; 
const byte otherAddress = 9;

Key keys[COLUMNS_NUMBER][ROWS_NUMBER];

void reportLayout(){
  int n=COLUMNS_NUMBER*ROWS_NUMBER;
  ReportedKey* reportedKeys=new ReportedKey[n];
  for(int j=0;j<ROWS_NUMBER;j++){
    for(int i=0;i<COLUMNS_NUMBER;i++){
      reportedKeys[j*COLUMNS_NUMBER+i]=ReportedKey(i,j,(byte*)keys[i][j].value);
    }
  }
  byte header[]={(byte)'l',(byte)COLUMNS_NUMBER,(byte)ROWS_NUMBER};
  Serial.write(header,3);
  Serial.write((byte*)reportedKeys,n*sizeof(ReportedKey));
  Serial.print('@');
  delete reportedKeys;
}
void setKey(){
  int x=Serial.read()-'0';
  int y=Serial.read()-'0';
  char value[4];
  for(int i=0;i<4;i++)value[i]=Serial.read();
  for(int i=0;i<4;i++)keys[x][y].value[i]=value[i];
  // Serial.printf("x: %d y: %d l0: %d l1: %d l2: %d l3: %d",x,y,value[0],value[1],value[2],value[3]);
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

  for(int i=0;i<COLUMNS_NUMBER;i++){
    for(int j=0;j<ROWS_NUMBER;j++){
      if(keys[i][j].justChanged){
        keys[i][j].justChanged=false;
        keys[i][j].pressed=false;
        for(int k=0;k<4;k++){
          keys[i][j].value[k]='\0';
        }
      }
    }
  }

//red 0///////////////////////////////////////////////////////////////
  keys[16][0].value[0]=KEY_ESC;
  keys[15][0].value[0]=KEY_F1;
  keys[14][0].value[0]=KEY_F2;
  keys[13][0].value[0]=KEY_F3;
  keys[12][0].value[0]=KEY_F4;
  keys[11][0].value[0]=KEY_F5;
  keys[10][0].value[0]=KEY_F6;
  keys[9][0].value[0]=KEY_F7;
  keys[8][0].value[0]=KEY_F8;
  keys[7][0].value[0]=KEY_F9;
  keys[6][0].value[0]=KEY_F10;
  keys[5][0].value[0]=KEY_F11;
  keys[4][0].value[0]=KEY_F12;
  keys[2][0].value[0]=KEY_PRINT_SCREEN;
  keys[1][0].value[0]=KEY_PRINT_SCREEN;
  keys[0][0].value[0]=KEY_SCROLL_LOCK;
// //red 1/////////////////////////////////////////////////////////////
  keys[16][1].value[0]='`';
  keys[15][1].value[0]='1';
  keys[14][1].value[0]='2';
  keys[12][1].value[0]='3';
  keys[13][1].value[0]='4';
  keys[11][1].value[0]='5';
  keys[10][1].value[0]='6';
  keys[9][1].value[0]='7';
  keys[8][1].value[0]='8';
  keys[7][1].value[0]='9';
  keys[6][1].value[0]='0';
  keys[5][1].value[0]='-';
  keys[4][1].value[0]='=';
  keys[3][1].value[0]=KEY_BACKSPACE;

  keys[2][1].value[0]=KEY_INSERT;
  keys[1][1].value[0]=KEY_HOME;
  keys[0][1].value[0]=KEY_PAGE_UP;
// //red 2/////////////////////////////////////////////////////////////
  keys[16][2].value[0]=KEY_TAB;
  keys[15][2].value[0]='q';
  keys[14][2].value[0]='w';
  keys[13][2].value[0]='e';
  keys[12][2].value[0]='r';
  keys[11][2].value[0]='t';
  keys[10][2].value[0]='y';
  keys[9][2].value[0]='u';
  keys[8][2].value[0]='i';
  keys[7][2].value[0]='o';
  keys[6][2].value[0]='p';
  keys[5][2].value[0]='[';
  keys[4][2].value[0]=']';

  keys[2][2].value[0]=KEY_DELETE;
  keys[1][2].value[0]=KEY_END;
  keys[0][2].value[0]=KEY_PAGE_DOWN;
// //red 3/////////////////////////////////////////////////////////////
  keys[16][3].value[0]=KEY_CAPS_LOCK;
  keys[15][3].value[0]='a';
  keys[14][3].value[0]='s';
  keys[13][3].value[0]='d';
  keys[12][3].value[0]='f';
  keys[11][3].value[0]='g';
  keys[10][3].value[0]='h';
  keys[9][3].value[0]='j';
  keys[8][3].value[0]='k';
  keys[7][3].value[0]='l';
  keys[6][3].value[0]=';';
  keys[5][3].value[0]='\'';
  keys[4][3].value[0]='\\';
  keys[3][3].value[0]='\n';

  keys[2][3].value[0]=KEY_DELETE;
  keys[1][3].value[0]=KEY_END;
  keys[0][3].value[0]=KEY_PAGE_DOWN;
// //red 4/////////////////////////////////////////////////////////////
  keys[16][4].value[0]=KEY_LEFT_SHIFT;
  keys[15][4].value[0]='/';
  keys[14][4].value[0]='z';
  keys[13][4].value[0]='x';
  keys[12][4].value[0]='c';
  keys[11][4].value[0]='v';
  keys[10][4].value[0]='b';
  keys[9][4].value[0]='n';
  keys[8][4].value[0]='m';
  keys[7][4].value[0]=',';
  keys[6][4].value[0]='.';
  keys[5][4].value[0]='/';
  keys[4][4].value[0]=KEY_LEFT_SHIFT;
  keys[1][4].value[0]=KEY_UP_ARROW;

// //red 5/////////////////////////////////////////////////////////////
  keys[16][5].value[0]=KEY_LEFT_CTRL;
  keys[15][5].value[0]=KEY_LEFT_GUI;
  keys[14][5].value[0]=KEY_LEFT_ALT;
 
  keys[10][5].value[0]=' ';
  
  keys[6][5].value[0]=KEY_RIGHT_CTRL;
  keys[5][5].value[0]=KEY_RIGHT_GUI;
  keys[4][5].value[0]=KEY_RIGHT_ALT;
  keys[3][5].value[0]=KEY_RIGHT_CTRL;

  keys[2][5].value[0]=KEY_LEFT_ARROW;
  keys[1][5].value[0]=KEY_DOWN_ARROW;
  keys[0][5].value[0]=KEY_RIGHT_ARROW;


  #ifdef DEBUG
    Serial.begin(9600);
  #else
    Keyboard.begin();
    Serial.begin(9600);
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

  if(Serial.available()>0){
    char cmd=Serial.read();
    if(cmd == 'r')reportLayout();
    if(cmd=='k')setKey();
  }

  int layer=0;
  if(keys[6][5].pressed){
    keys[6][5].justChanged=false;
    layer=1;
  }

  for(int i=0;i<COLUMNS_NUMBER;i++){
    for(int j=0;j<ROWS_NUMBER;j++){
      if(keys[i][j].justChanged){
        keys[i][j].justChanged=false;
        if(keys[i][j].pressed){
          Key* key=&keys[i][j];
          #ifdef DEBUG
            Serial.printf("pressed  %3d %3d\n",i,j);
          #else
            // Serial.printf("pressed  %3d %3d\n",i,j);
            for(int k=layer;k>=0;k--){
              char value=key->value[k];
              if(value){
                key->currentlyDown=value;
                Keyboard.press(value);
                break;
              }
            }
          #endif
        }else{
          #ifdef DEBUG
            Serial.printf("released %3d %3d\n",i,j);
          #else
            char value=keys[i][j].currentlyDown;
            if(value)Keyboard.release(value);
          #endif
        } 
      }
    }
  }
}




