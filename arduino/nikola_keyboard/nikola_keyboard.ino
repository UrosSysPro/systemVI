#include "Arduino.h"
#include "Keyboard.h"
//#define DEBUG

#define COLUMNS_NUMBER 12
#define ROWS_NUMBER 4
#define LAYERS 3

// int columns[] = { 0, 1, 2, 3, 4, 5, 6, 8, 7, 9, 10, 11 };
// int rows[] = { 12, 13, 14, 15 };
int columns[] = { 10,11,16,17,18,19,20,21,22,28,26,27 };
int rows[] = { 12,13,14,15 };

struct Key {
  bool pressed, justChanged;         
  char value[3],currentlyDown;
};

Key keys[COLUMNS_NUMBER][ROWS_NUMBER];

void setup() {
  for (int i = 0; i < COLUMNS_NUMBER; i++) {
    int pin = columns[i];
    pinMode(pin, OUTPUT);
    digitalWrite(pin, 1);
  }
  for (int i = 0; i < ROWS_NUMBER; i++) {
    int pin = rows[i];
    pinMode(pin, INPUT_PULLUP);
  }
  for (int i = 0; i < COLUMNS_NUMBER; i++) {
    int pin = columns[i];
    pinMode(pin, INPUT_PULLUP);
  }
  for (int i = 0; i < ROWS_NUMBER; i++) {
    int pin = rows[i];
    pinMode(pin, OUTPUT);
    digitalWrite(pin, 1);
  }

  for (int i = 0; i < COLUMNS_NUMBER; i++) {
    for (int j = 0; j < ROWS_NUMBER; j++) {
      if (keys[i][j].justChanged) {
        keys[i][j].justChanged = false;
        keys[i][j].pressed = false;
        for(int k=0;k<LAYERS;k++){
          keys[i][j].value[k]='\0';
        }
      }
    }
  }

  //red 0///////////////////////////////////////////////////////////////
  keys[0][1].value[0] = KEY_TAB;
  keys[1][1].value[0] = 'a';
  keys[2][1].value[0] = 's';
  keys[3][1].value[0] = 'd';
  keys[4][1].value[0] = 'f';
  keys[5][1].value[0] = 'g';
  keys[6][1].value[0] = 'h';
  keys[7][1].value[0] = 'j';
  keys[8][1].value[0] = 'k';
  keys[10][1].value[0] = 'l';
  keys[11][1].value[0] = '\n';
  //red 1/////////////////////////////////////////////////////////////
  keys[0][0].value[0] = KEY_ESC;
  keys[1][0].value[0] = 'q';
  keys[2][0].value[0] = 'w';
  keys[3][0].value[0] = 'e';
  keys[4][0].value[0] = 'r';
  keys[5][0].value[0] = 't';
  keys[6][0].value[0] = 'y';
  keys[7][0].value[0] = 'u';
  keys[8][0].value[0] = 'i';
  keys[9][0].value[0] = 'o';
  keys[10][0].value[0] = 'p';
  keys[11][0].value[0] = KEY_BACKSPACE;
  //red 2/////////////////////////////////////////////////////////////
  keys[0][2].value[0] = KEY_LEFT_SHIFT;
  keys[1][2].value[0] = 'z';
  keys[2][2].value[0] = 'x';
  keys[3][2].value[0] = 'c';
  keys[4][2].value[0] = 'v';
  keys[5][2].value[0] = 'b';
  keys[6][2].value[0] = 'n';
  keys[7][2].value[0] = 'm';
  keys[8][2].value[0] = '>';
  keys[9][2].value[0] = ',';
  keys[11][2].value[0] = '.';

  //red 3/////////////////////////////////////////////////////////////
  keys[0][3].value[0] = KEY_LEFT_CTRL;
  keys[1][3].value[0] = KEY_LEFT_GUI;
  keys[2][3].value[0] = KEY_RIGHT_ALT;
  keys[5][3].value[0] = ' ';
 // keys[10][3].value[0] = 'a';
  keys[11][3].value[0] = KEY_LEFT_ALT;

  //red 0 layer 1/////////////////////////////////////////////////////
  keys[0][0].value[1] = KEY_ESC;
  keys[1][0].value[1] = '1';
  keys[2][0].value[1] = '2';
  keys[3][0].value[1] = '3';
  keys[4][0].value[1] = '4';
  keys[5][0].value[1] = '5';
  keys[6][0].value[1] = '6';
  keys[7][0].value[1] = '7';
  keys[8][0].value[1] = '8';
  keys[9][0].value[1] = '9';
  keys[10][0].value[1] = '0';
  keys[11][0].value[1] = KEY_DELETE;

  //red 1 layer 1//////////////////////////////////////////////////////
  keys[8][1].value[1] = '(';
  keys[10][1].value[1] = ')';
  keys[6][1].value[1] = '[';
  keys[7][1].value[1] = ']';
  keys[5][1].value[1] = '{';
  keys[6][1].value[1] = '}';
    //red 2 layer 1////////////////////
  keys[8][2].value[1] = '<';
  keys[10][2].value[1] = ';';
  keys[11][2].value[1] = ':';



#ifdef DEBUG
  Serial.begin(9600);
#else
  Keyboard.begin();
#endif
}

void loop() {
  for (int j = 0; j < ROWS_NUMBER; j++) {
    int rowPin = rows[j];
    digitalWrite(rowPin, 0);
    delayMicroseconds(1000);
    for (int i = 0; i < COLUMNS_NUMBER; i++) {
      int columnPin = columns[i];
      bool newState = digitalRead(columnPin) == 0;
      if (newState != keys[i][j].pressed) keys[i][j].justChanged = true;
      keys[i][j].pressed = newState;
    }
    digitalWrite(rowPin, 1);
  }

  int layer=0;
  if(keys[10][3].pressed){
    layer=1;
    keys[10][3].justChanged=false;
  }

  // if(keys[3][4].pressed){
  //   keys[3][4].justChanged=false;
  //   fn=true;
  // }

  for (int i = 0; i < COLUMNS_NUMBER; i++) {
    for (int j = 0; j < ROWS_NUMBER; j++) {
      if (keys[i][j].justChanged) {
        keys[i][j].justChanged = false;
        if (keys[i][j].pressed) {
#ifdef DEBUG
          Serial.printf("pressed  %3d %3d   col: %3d row: %3d\n", i, j,columns[i], rows[j]);
#else
          for(int k=layer;k>=0;k--){
            if(keys[i][j].value[k]!='\0'){
              Keyboard.press(keys[i][j].value[k]);
              keys[i][j].currentlyDown=keys[i][j].value[k];
              break;
            }
          }
          #endif
        } else {
#ifdef DEBUG
          Serial.printf("released %3d %3d   col: %3d row: %3d\n", i, j,columns[i], rows[j]);
#else
          Keyboard.release(keys[i][j].currentlyDown);
#endif
        }
      }
    }
  }
}
