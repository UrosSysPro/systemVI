#include"Arduino.h"
#include "Keyboard.h"
#include<string>
// #define DEBUG

#define COLUMNS_NUMBER 17
#define ROWS_NUMBER 6
 
int columns[]={16,17,18,19,20,21,22,26,27,28,0,1,2,3,4,5,6};
int rows[]={7,8,9,10,11,12};

bool printKeyEventsToSerial=false;

class Key{
  public:
  Key(){
    this->pressed=false;
    this->justChanged=false;
    this->active=false;
    this->currentlyDown='\0';
    for(int i=0;i<4;i++)this->value[i]='\0';
    this->width=0;
    this->height=0;
    this->offsetX=0;
    this->offsetY=0;
    this->rotation=0;
    this->physicalX=0;
    this->physicalY=0;
  }
  bool pressed,justChanged,active;
  char value[4],currentlyDown;
  int width,height,offsetX,offsetY,physicalX,physicalY;
  float rotation;
  void setLayer0SizePosition(char layer0,int width,int height,int physicalX,int physicalY){
    this->value[0]=layer0;
    this->width=width;
    this->height=height;
    this->physicalX=physicalX;
    this->physicalY=physicalY;
    this->active=true;
  }
  void setLayer0Position(char layer0,int physicalX,int physicalY){
    this->value[0]=layer0;
    this->physicalX=physicalX;
    this->physicalY=physicalY;
    this->active=true;
  }
};

class ReportedKey{
  public:
  byte x,y,value[4],width,height,physicalX,physicalY,active;
  ReportedKey(){}
  ReportedKey(byte x,byte y,byte* value,int width,int height,int px,int py,bool active){
    this->x=x;
    this->y=y;
    for(int i=0;i<4;i++){
      this->value[i]=value[i];
    }
    this->width=width;
    this->height=height;
    this->physicalX=px;
    this->physicalY=py;
    this->active=active;
  }
};

Key keys[COLUMNS_NUMBER][ROWS_NUMBER];

void reportLayout(){
  int n=COLUMNS_NUMBER*ROWS_NUMBER;
  ReportedKey* reportedKeys=new ReportedKey[n];
  for(int i=0;i<COLUMNS_NUMBER;i++){
    for(int j=0;j<ROWS_NUMBER;j++){
      reportedKeys[j*COLUMNS_NUMBER+i]=ReportedKey(
        i,
        j,
        (byte*)keys[i][j].value,
        keys[i][j].width,
        keys[i][j].height,
        keys[i][j].physicalX,
        keys[i][j].physicalY,
        keys[i][j].active
      );
    }
  }
  byte header[]={(byte)'l',(byte)COLUMNS_NUMBER,(byte)ROWS_NUMBER};
  Serial.write(header,3);
  Serial.write((byte*)reportedKeys,n*sizeof(ReportedKey));
  Serial.print('@');
  delete reportedKeys;
}

void setKey(){
  int x=Serial.read();
  int y=Serial.read();
  char value[4];
  for(int i=0;i<4;i++)value[i]=Serial.read();
  for(int i=0;i<4;i++)keys[x][y].value[i]=value[i];
  // Serial.printf("x: %d y: %d l0: %d l1: %d l2: %d l3: %d",x,y,value[0],value[1],value[2],value[3]);
}

void setKeyOnLayer(){
  int x=Serial.read();
  int y=Serial.read();
  int layer=Serial.read();
  char value=Serial.read();
  keys[x][y].value[layer]=value;
  // Serial.printf("x: %d y: %d l0: %d l1: %d l2: %d l3: %d",x,y,value[0],value[1],value[2],value[3]);
}

void printKeyPressToSerial(int x,int y){
  byte message[4];
  message[0]=(byte)'p';
  message[1]=x;
  message[2]=y;
  message[3]='@';
  Serial.write(message,4);
}

void printKeyReleaseToSerial(int x,int y){
  byte message[4];
  message[0]=(byte)'r';
  message[1]=x;
  message[2]=y;
  message[3]='@';
  Serial.write(message,4);
}

void printName(){
  char message[]="ntkl_rabbit@";
  Serial.write((byte*)message,12);
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

//keycapSizes
//0 1u
//1 1.25u
//2 1.5u
//3 1.75u
//4 2u
//5 2.25u
//6 2.75u
//7 6.25u

//red 0///////////////////////////////////////////////////////////////
  keys[16][0].setLayer0Position(KEY_ESC, 0,0); 
  keys[15][0].setLayer0Position(KEY_F1, 1,0);
  keys[14][0].setLayer0Position(KEY_F2, 2,0);
  keys[13][0].setLayer0Position(KEY_F3, 3,0);
  keys[12][0].setLayer0Position(KEY_F4, 4,0);
  keys[11][0].setLayer0Position(KEY_F5, 5,0);
  keys[10][0].setLayer0Position(KEY_F6, 6,0);
  keys[9][0].setLayer0Position(KEY_F7, 7,0);
  keys[8][0].setLayer0Position(KEY_F8, 8,0);
  keys[7][0].setLayer0Position(KEY_F9, 9,0);
  keys[6][0].setLayer0Position(KEY_F10, 10,0);
  keys[5][0].setLayer0Position(KEY_F11, 11,0);
  keys[4][0].setLayer0Position(KEY_F12, 12,0);

  keys[2][0].setLayer0Position(KEY_PRINT_SCREEN, 13,0);
  keys[1][0].setLayer0Position(KEY_PRINT_SCREEN, 14,0);
  keys[0][0].setLayer0Position(KEY_SCROLL_LOCK, 15,0);
// //red 1/////////////////////////////////////////////////////////////
  keys[16][1].setLayer0Position('`', 0,1);
  keys[15][1].setLayer0Position('1', 1,1);
  keys[14][1].setLayer0Position('2', 2,1);
  keys[12][1].setLayer0Position('3', 3,1);
  keys[13][1].setLayer0Position('4', 4,1);
  keys[11][1].setLayer0Position('5', 5,1);
  keys[10][1].setLayer0Position('6', 6,1);
  keys[9][1].setLayer0Position('7', 7,1);
  keys[8][1].setLayer0Position('8', 8,1);
  keys[7][1].setLayer0Position('9', 9,1);
  keys[6][1].setLayer0Position('0', 10,1);
  keys[5][1].setLayer0Position('-', 11,1);
  keys[4][1].setLayer0Position('=', 12,1);
  keys[3][1].setLayer0SizePosition(KEY_BACKSPACE,4,0, 13,1);

  keys[2][1].setLayer0Position(KEY_INSERT, 13,1);
  keys[1][1].setLayer0Position(KEY_HOME, 14,1);
  keys[0][1].setLayer0Position(KEY_PAGE_UP, 15,1);
// //red 2/////////////////////////////////////////////////////////////
  keys[16][2].setLayer0SizePosition(KEY_TAB, 2, 0, 0, 2);
  keys[15][2].setLayer0Position('q', 1,2);
  keys[14][2].setLayer0Position('w', 2,2);
  keys[13][2].setLayer0Position('e', 3,2);
  keys[12][2].setLayer0Position('r', 4,2);
  keys[11][2].setLayer0Position('t', 5,2);
  keys[10][2].setLayer0Position('y', 6,2);
  keys[9][2].setLayer0Position('u', 7,2);
  keys[8][2].setLayer0Position('i', 8,2);
  keys[7][2].setLayer0Position('o', 9,2);
  keys[6][2].setLayer0Position('p', 10,2);
  keys[5][2].setLayer0Position('[', 11,2);
  keys[4][2].setLayer0Position(']', 12,2);

  keys[2][2].setLayer0Position(KEY_DELETE, 14,2);
  keys[1][2].setLayer0Position(KEY_END, 15,2);
  keys[0][2].setLayer0Position(KEY_PAGE_DOWN, 16,2);
// //red 3/////////////////////////////////////////////////////////////
  keys[16][3].setLayer0SizePosition(KEY_CAPS_LOCK,3,0,0,3);
  keys[15][3].setLayer0Position('a', 1,3);
  keys[14][3].setLayer0Position('s', 2,3);
  keys[13][3].setLayer0Position('d', 3,3);
  keys[12][3].setLayer0Position('f', 4,3);
  keys[11][3].setLayer0Position('g', 5,3);
  keys[10][3].setLayer0Position('h', 6,3);
  keys[9][3].setLayer0Position('j', 7,3);
  keys[8][3].setLayer0Position('k', 8,3);
  keys[7][3].setLayer0Position('l', 9,3);
  keys[6][3].setLayer0Position(';', 10,3);
  keys[5][3].setLayer0Position('\'', 11,3);
  keys[4][3].setLayer0SizePosition('\\',2,0, 13,2);
  keys[3][3].setLayer0SizePosition('\n',5,0, 12,3);

  keys[2][3].setLayer0Position(KEY_DELETE, 13,3);
  keys[1][3].setLayer0Position(KEY_END, 14,3);
  keys[0][3].setLayer0Position(KEY_PAGE_DOWN, 15,3);
// //red 4/////////////////////////////////////////////////////////////
  keys[16][4].setLayer0SizePosition(KEY_LEFT_SHIFT,1,0, 0,4);
  keys[15][4].setLayer0Position('/', 1,4); 
  keys[14][4].setLayer0Position('z', 2,4);
  keys[13][4].setLayer0Position('x', 3,4); 
  keys[12][4].setLayer0Position('c', 4,4); 
  keys[11][4].setLayer0Position('v', 5,4); 
  keys[10][4].setLayer0Position('b', 6,4); 
  keys[9][4].setLayer0Position('n', 7,4); 
  keys[8][4].setLayer0Position('m', 8,4); 
  keys[7][4].setLayer0Position(',', 9,4); 
  keys[6][4].setLayer0Position('.', 10,4); 
  keys[5][4].setLayer0Position('/', 11,4); 
  keys[4][4].setLayer0SizePosition(KEY_RIGHT_SHIFT,6,0, 12,4); 
  keys[1][4].setLayer0Position(KEY_UP_ARROW, 13,4); 

// //red 5/////////////////////////////////////////////////////////////
  keys[16][5].setLayer0SizePosition(KEY_LEFT_CTRL,1,0, 0,5);
  keys[15][5].setLayer0SizePosition(KEY_LEFT_GUI,1,0, 1,5);
  keys[14][5].setLayer0SizePosition(KEY_LEFT_ALT,1,0, 2,5);
 
  keys[10][5].setLayer0SizePosition(' ',7,0, 3,5);
  
  keys[6][5].setLayer0SizePosition(KEY_RIGHT_CTRL,1,0, 4,5);
  keys[5][5].setLayer0SizePosition(KEY_RIGHT_GUI,1,0, 5,5);
  keys[4][5].setLayer0SizePosition(KEY_RIGHT_ALT,1,0, 6,5);
  keys[3][5].setLayer0SizePosition(KEY_RIGHT_CTRL,1,0, 7,5);

  keys[2][5].setLayer0Position(KEY_LEFT_ARROW, 8,5);
  keys[1][5].setLayer0Position(KEY_DOWN_ARROW, 9,5);
  keys[0][5].setLayer0Position(KEY_RIGHT_ARROW, 10,5);


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
    if(cmd=='l')setKeyOnLayer();
    if(cmd=='e')printKeyEventsToSerial=true;
    if(cmd=='d')printKeyEventsToSerial=false;
    if(cmd=='n')printName();
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
                if(printKeyEventsToSerial)printKeyPressToSerial(i, j);
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
            if(value){
              if(printKeyEventsToSerial)printKeyReleaseToSerial(i, j);
              Keyboard.release(value);
            }
          #endif
        } 
      }
    }
  }
}