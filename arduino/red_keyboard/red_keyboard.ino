#include "SystemVIKeyboard.h"
#include "Keyboard.h"

#define COLUMNS_NUMBER 12
#define ROWS_NUMBER 4

int columns[] = { 10,11,16,17,18,19,20,21,22,28,26,27 };
int rows[] = { 12,13,14,15 };

SystemVIKeyboard *keyboard;

void setup() {
  char name[]="red_keyboard";
  keyboard=new SystemVIKeyboard(
    name,
    COLUMNS_NUMBER,
    ROWS_NUMBER,
    columns,
    rows,
    true,
    COLUMNS_NUMBER,
    ROWS_NUMBER
  );
  
  //red 0/////////////////////////////////////////////////////////////
  keyboard->setNormalKeycap( 0,0,    (char[]){KEY_ESC           ,'\0','\0','\0'},    0,0,    0,0);
  keyboard->setNormalKeycap( 1,0,    (char[]){'q'               ,'\0','\0','\0'},    1,0,    0,0);
  keyboard->setNormalKeycap( 2,0,    (char[]){'w'               ,'\0','\0','\0'},    2,0,    0,0);
  keyboard->setNormalKeycap( 3,0,    (char[]){'e'               ,'\0','\0','\0'},    3,0,    0,0);
  keyboard->setNormalKeycap( 4,0,    (char[]){'r'               ,'\0','\0','\0'},    4,0,    0,0);
  keyboard->setNormalKeycap( 5,0,    (char[]){'t'               ,'\0','\0','\0'},    5,0,    0,0);
  keyboard->setNormalKeycap( 6,0,    (char[]){'y'               ,'\0','\0','\0'},    6,0,    0,0);
  keyboard->setNormalKeycap( 7,0,    (char[]){'u'               ,'\0','\0','\0'},    7,0,    0,0);
  keyboard->setNormalKeycap( 8,0,    (char[]){'i'               ,'\0','\0','\0'},    8,0,    0,0);
  keyboard->setNormalKeycap( 9,0,    (char[]){'o'               ,'\0','\0','\0'},    9,0,    0,0);
  keyboard->setNormalKeycap(10,0,    (char[]){'p'               ,'\0','\0','\0'},   10,0,    0,0);
  keyboard->setNormalKeycap(11,0,    (char[]){KEY_BACKSPACE     ,'\0','\0','\0'},   11,0,    0,0);
  //red 1/////////////////////////////////////////////////      //////////////
  keyboard->setNormalKeycap( 0,1,    (char[]){KEY_TAB           ,'\0','\0','\0'},    0,1,    0,0);
  keyboard->setNormalKeycap( 1,1,    (char[]){'a'               ,'\0','\0','\0'},    1,1,    0,0);
  keyboard->setNormalKeycap( 2,1,    (char[]){'s'               ,'\0','\0','\0'},    2,1,    0,0);
  keyboard->setNormalKeycap( 3,1,    (char[]){'d'               ,'\0','\0','\0'},    3,1,    0,0);
  keyboard->setNormalKeycap( 4,1,    (char[]){'f'               ,'\0','\0','\0'},    4,1,    0,0);
  keyboard->setNormalKeycap( 5,1,    (char[]){'g'               ,'\0','\0','\0'},    5,1,    0,0);
  keyboard->setNormalKeycap( 6,1,    (char[]){'h'               ,'\0','\0','\0'},    6,1,    0,0);
  keyboard->setNormalKeycap( 7,1,    (char[]){'j'               ,'\0','\0','\0'},    7,1,    0,0);
  keyboard->setNormalKeycap( 8,1,    (char[]){'k'               ,'\0','\0','\0'},    8,1,    0,0);
  keyboard->setNormalKeycap(10,1,    (char[]){'l'               ,'\0','\0','\0'},    9,1,    0,0);
  keyboard->setNormalKeycap(11,1,    (char[]){KEY_RETURN        ,'\0','\0','\0'},   10,1,    0,0);
  //red 2/////////////////////////////////////////////////////////////
  keyboard->setNormalKeycap( 0,2,    (char[]){KEY_LEFT_SHIFT    ,'\0','\0','\0'},    0,2,    0,0);
  keyboard->setNormalKeycap( 1,2,    (char[]){'z'               ,'\0','\0','\0'},    1,2,    0,0);
  keyboard->setNormalKeycap( 2,2,    (char[]){'x'               ,'\0','\0','\0'},    2,2,    0,0);
  keyboard->setNormalKeycap( 3,2,    (char[]){'c'               ,'\0','\0','\0'},    3,2,    0,0);
  keyboard->setNormalKeycap( 4,2,    (char[]){'v'               ,'\0','\0','\0'},    4,2,    0,0);
  keyboard->setNormalKeycap( 5,2,    (char[]){'b'               ,'\0','\0','\0'},    5,2,    0,0);
  keyboard->setNormalKeycap( 6,2,    (char[]){'n'               ,'\0','\0','\0'},    6,2,    0,0);
  keyboard->setNormalKeycap( 7,2,    (char[]){'m'               ,'\0','\0','\0'},    7,2,    0,0);
  keyboard->setNormalKeycap( 8,2,    (char[]){'>'               ,'\0','\0','\0'},    8,2,    0,0);
  keyboard->setNormalKeycap( 9,2,    (char[]){','               ,'\0','\0','\0'},    9,2,    0,0);
  keyboard->setNormalKeycap(11,2,    (char[]){'.'               ,'\0','\0','\0'},   10,2,    0,0);

  //red 3/////////////////////////////////////////////////////////////
  keyboard->setNormalKeycap( 0,3,    (char[]){KEY_LEFT_CTRL     ,'\0','\0','\0'},    0,3,    0,0);
  keyboard->setNormalKeycap( 1,3,    (char[]){KEY_LEFT_GUI      ,'\0','\0','\0'},    1,3,    0,0);
  keyboard->setNormalKeycap( 2,3,    (char[]){KEY_RIGHT_ALT     ,'\0','\0','\0'},    2,3,    0,0);
  keyboard->setNormalKeycap( 5,3,    (char[]){' '               ,'\0','\0','\0'},    3,3,    0,0);
  keyboard->setNormalKeycap(11,3,    (char[]){KEY_LEFT_ALT      ,'\0','\0','\0'},    4,3,    0,0);
}

void loop() {
  keyboard->update();
}
