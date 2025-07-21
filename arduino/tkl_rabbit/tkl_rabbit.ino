#include "SystemVIKeyboard.h"
#include "Keyboard.h"

#define COLUMNS_NUMBER 17
#define ROWS_NUMBER 6
 
int columns[]={16,17,18,19,20,21,22,26,27,28,0,1,2,3,4,5,6};
int rows[]={7,8,9,10,11,12};

SystemVIKeyboard* keyboard;

void setup() {
  char name[]="tkl_rabbit";
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
  keyboard->setNormalKeycap(16,0,  (char[]){KEY_ESC,            '\0','\0','\0'},      0,0,      0,0,    0,2); 
  keyboard->setNormalKeycap(15,0,  (char[]){KEY_F1,             '\0','\0','\0'},      1,0,      0,0,    4,0);
  keyboard->setNormalKeycap(14,0,  (char[]){KEY_F2,             '\0','\0','\0'},      2,0,      0,0);
  keyboard->setNormalKeycap(13,0,  (char[]){KEY_F3,             '\0','\0','\0'},      3,0,      0,0);
  keyboard->setNormalKeycap(12,0,  (char[]){KEY_F4,             '\0','\0','\0'},      4,0,      0,0);
  keyboard->setNormalKeycap(11,0,  (char[]){KEY_F5,             '\0','\0','\0'},      5,0,      0,0,    2,0);
  keyboard->setNormalKeycap(10,0,  (char[]){KEY_F6,             '\0','\0','\0'},      6,0,      0,0);
  keyboard->setNormalKeycap(9 ,0,  (char[]){KEY_F7,             '\0','\0','\0'},      7,0,      0,0);
  keyboard->setNormalKeycap(8 ,0,  (char[]){KEY_F8,             '\0','\0','\0'},      8,0,      0,0);
  keyboard->setNormalKeycap(7 ,0,  (char[]){KEY_F9,             '\0','\0','\0'},      9,0,      0,0,    2,0);
  keyboard->setNormalKeycap(6 ,0,  (char[]){KEY_F10,            '\0','\0','\0'},     10,0,      0,0);
  keyboard->setNormalKeycap(5 ,0,  (char[]){KEY_F11,            '\0','\0','\0'},     11,0,      0,0);
  keyboard->setNormalKeycap(4 ,0,  (char[]){KEY_F12,            '\0','\0','\0'},     12,0,      0,0);

  keyboard->setNormalKeycap(2,0,   (char[]){KEY_PRINT_SCREEN,   '\0','\0','\0'},     13,0,      0,0,    1,0);
  keyboard->setNormalKeycap(1,0,   (char[]){KEY_PRINT_SCREEN,   '\0','\0','\0'},     14,0,      0,0);
  keyboard->setNormalKeycap(0,0,   (char[]){KEY_SCROLL_LOCK ,   '\0','\0','\0'},     15,0,      0,0);
// //red 1/////////////////////////////////////////////////////////////
  keyboard->setNormalKeycap(16,1,  (char[]){'`',                '\0','\0','\0'},      0,1,      0,0);
  keyboard->setNormalKeycap(15,1,  (char[]){'1',                '\0','\0','\0'},      1,1,      0,0);
  keyboard->setNormalKeycap(14,1,  (char[]){'2',                '\0','\0','\0'},      2,1,      0,0);
  keyboard->setNormalKeycap(12,1,  (char[]){'3',                '\0','\0','\0'},      3,1,      0,0);
  keyboard->setNormalKeycap(13,1,  (char[]){'4',                '\0','\0','\0'},      4,1,      0,0);
  keyboard->setNormalKeycap(11,1,  (char[]){'5',                '\0','\0','\0'},      5,1,      0,0);
  keyboard->setNormalKeycap(10,1,  (char[]){'6',                '\0','\0','\0'},      6,1,      0,0);
  keyboard->setNormalKeycap( 9,1,  (char[]){'7',                '\0','\0','\0'},      7,1,      0,0);
  keyboard->setNormalKeycap( 8,1,  (char[]){'8',                '\0','\0','\0'},      8,1,      0,0);
  keyboard->setNormalKeycap( 7,1,  (char[]){'9',                '\0','\0','\0'},      9,1,      0,0);
  keyboard->setNormalKeycap( 6,1,  (char[]){'0',                '\0','\0','\0'},     10,1,      0,0);
  keyboard->setNormalKeycap( 5,1,  (char[]){'-',                '\0','\0','\0'},     11,1,      0,0);
  keyboard->setNormalKeycap( 4,1,  (char[]){'=',                '\0','\0','\0'},     12,1,      0,0);

  keyboard->setNormalKeycap( 3,1,  (char[]){KEY_BACKSPACE,      '\0','\0','\0'},     13,1,      4,0);
  keyboard->setNormalKeycap( 2,1,  (char[]){KEY_INSERT,         '\0','\0','\0'},     14,1,      0,0,    1,0);
  keyboard->setNormalKeycap( 1,1,  (char[]){KEY_HOME,           '\0','\0','\0'},     15,1,      0,0);
  keyboard->setNormalKeycap( 0,1,  (char[]){KEY_PAGE_UP,        '\0','\0','\0'},     16,1,      0,0);
// //red 2/////////////////////////////////////////////////////////////
  keyboard->setNormalKeycap(16,2,  (char[]){KEY_TAB,            '\0','\0','\0'},      0,2,      2,0);
  keyboard->setNormalKeycap(15,2,  (char[]){'q',                '\0','\0','\0'},      1,2,      0,0);
  keyboard->setNormalKeycap(14,2,  (char[]){'w',                '\0','\0','\0'},      2,2,      0,0);
  keyboard->setNormalKeycap(13,2,  (char[]){'e',                '\0','\0','\0'},      3,2,      0,0);
  keyboard->setNormalKeycap(12,2,  (char[]){'r',                '\0','\0','\0'},      4,2,      0,0);
  keyboard->setNormalKeycap(11,2,  (char[]){'t',                '\0','\0','\0'},      5,2,      0,0);
  keyboard->setNormalKeycap(10,2,  (char[]){'y',                '\0','\0','\0'},      6,2,      0,0);
  keyboard->setNormalKeycap( 9,2,  (char[]){'u',                '\0','\0','\0'},      7,2,      0,0);
  keyboard->setNormalKeycap( 8,2,  (char[]){'i',                '\0','\0','\0'},      8,2,      0,0);
  keyboard->setNormalKeycap( 7,2,  (char[]){'o',                '\0','\0','\0'},      9,2,      0,0);
  keyboard->setNormalKeycap( 6,2,  (char[]){'p',                '\0','\0','\0'},     10,2,      0,0);
  keyboard->setNormalKeycap( 5,2,  (char[]){'[',                '\0','\0','\0'},     11,2,      0,0);
  keyboard->setNormalKeycap( 4,2,  (char[]){']',                '\0','\0','\0'},     12,2,      0,0);

  keyboard->setNormalKeycap( 2,2,  (char[]){KEY_DELETE,         '\0','\0','\0'},     14,2,      0,0,    1,0);
  keyboard->setNormalKeycap( 1,2,  (char[]){KEY_END,            '\0','\0','\0'},     15,2,      0,0);
  keyboard->setNormalKeycap( 0,2,  (char[]){KEY_PAGE_DOWN,      '\0','\0','\0'},     16,2,      0,0);
// //red 3/////////////////////////////////////////////////////////////
  keyboard->setNormalKeycap(16,3,  (char[]){KEY_CAPS_LOCK,      '\0','\0','\0'},      0,3,      3,0);
  keyboard->setNormalKeycap(15,3,  (char[]){'a',                '\0','\0','\0'},      1,3,      0,0);
  keyboard->setNormalKeycap(14,3,  (char[]){'s',                '\0','\0','\0'},      2,3,      0,0);
  keyboard->setNormalKeycap(13,3,  (char[]){'d',                '\0','\0','\0'},      3,3,      0,0);
  keyboard->setNormalKeycap(12,3,  (char[]){'f',                '\0','\0','\0'},      4,3,      0,0);
  keyboard->setNormalKeycap(11,3,  (char[]){'g',                '\0','\0','\0'},      5,3,      0,0);
  keyboard->setNormalKeycap(10,3,  (char[]){'h',                '\0','\0','\0'},      6,3,      0,0);
  keyboard->setNormalKeycap( 9,3,  (char[]){'j',                '\0','\0','\0'},      7,3,      0,0);
  keyboard->setNormalKeycap( 8,3,  (char[]){'k',                '\0','\0','\0'},      8,3,      0,0);
  keyboard->setNormalKeycap( 7,3,  (char[]){'l',                '\0','\0','\0'},      9,3,      0,0);
  keyboard->setNormalKeycap( 6,3,  (char[]){';',                '\0','\0','\0'},     10,3,      0,0);
  keyboard->setNormalKeycap( 5,3,  (char[]){'\'',               '\0','\0','\0'},     11,3,      0,0);
  keyboard->setNormalKeycap( 4,3,  (char[]){'\\',               '\0','\0','\0'},     13,2,      2,0);
  keyboard->setNormalKeycap( 3,3,  (char[]){'\n',               '\0','\0','\0'},     13,3,      5,0);

  keyboard->setNormalKeycap( 1,3,  (char[]){KEY_END,            '\0','\0','\0'},     14,3,      4,0,    1,0);
  keyboard->setNormalKeycap( 0,3,  (char[]){KEY_PAGE_DOWN,      '\0','\0','\0'},     15,3,      0,0);
// //red 4/////////////////////////////////////////////////////////////
  keyboard->setNormalKeycap(16,4,  (char[]){KEY_LEFT_SHIFT,     '\0','\0','\0'},      0,4,      1,0);
  keyboard->setNormalKeycap(15,4,  (char[]){'/',                '\0','\0','\0'},      1,4,      0,0); 
  keyboard->setNormalKeycap(14,4,  (char[]){'z',                '\0','\0','\0'},      2,4,      0,0);
  keyboard->setNormalKeycap(13,4,  (char[]){'x',                '\0','\0','\0'},      3,4,      0,0); 
  keyboard->setNormalKeycap(12,4,  (char[]){'c',                '\0','\0','\0'},      4,4,      0,0); 
  keyboard->setNormalKeycap(11,4,  (char[]){'v',                '\0','\0','\0'},      5,4,      0,0); 
  keyboard->setNormalKeycap(10,4,  (char[]){'b',                '\0','\0','\0'},      6,4,      0,0); 
  keyboard->setNormalKeycap( 9,4,  (char[]){'n',                '\0','\0','\0'},      7,4,      0,0); 
  keyboard->setNormalKeycap( 8,4,  (char[]){'m',                '\0','\0','\0'},      8,4,      0,0); 
  keyboard->setNormalKeycap( 7,4,  (char[]){',',                '\0','\0','\0'},      9,4,      0,0); 
  keyboard->setNormalKeycap( 6,4,  (char[]){'.',                '\0','\0','\0'},     10,4,      0,0); 
  keyboard->setNormalKeycap( 5,4,  (char[]){'/',                '\0','\0','\0'},     11,4,      0,0); 
  keyboard->setNormalKeycap( 4,4,  (char[]){KEY_RIGHT_SHIFT,    '\0','\0','\0'},     12,4,      6,0);
  keyboard->setNormalKeycap( 1,4,  (char[]){KEY_UP_ARROW,       '\0','\0','\0'},     13,4,      0,0,     5,0); 

// //red 5/////////////////////////////////////////////////////////////
  keyboard->setNormalKeycap(16,5,  (char[]){KEY_LEFT_CTRL,      '\0','\0','\0'},      0,5,      1,0);
  keyboard->setNormalKeycap(15,5,  (char[]){KEY_LEFT_GUI,       '\0','\0','\0'},      1,5,      1,0);
  keyboard->setNormalKeycap(14,5,  (char[]){KEY_LEFT_ALT,       '\0','\0','\0'},      2,5,      1,0);
  keyboard->setNormalKeycap(10,5,  (char[]){' ',                '\0','\0','\0'},      3,5,      7,0);
  keyboard->setNormalKeycap( 6,5,  (char[]){KEY_RIGHT_CTRL,     '\0','\0','\0'},      4,5,      1,0);
  keyboard->setNormalKeycap( 5,5,  (char[]){KEY_RIGHT_GUI,      '\0','\0','\0'},      5,5,      1,0);
  keyboard->setNormalKeycap( 4,5,  (char[]){KEY_RIGHT_ALT,      '\0','\0','\0'},      6,5,      1,0);
  keyboard->setNormalKeycap( 3,5,  (char[]){KEY_RIGHT_CTRL,     '\0','\0','\0'},      7,5,      1,0);
  keyboard->setNormalKeycap( 2,5,  (char[]){KEY_LEFT_ARROW,     '\0','\0','\0'},      8,5,      0,0,     1,0);
  keyboard->setNormalKeycap( 1,5,  (char[]){KEY_DOWN_ARROW,     '\0','\0','\0'},      9,5,      0,0);
  keyboard->setNormalKeycap( 0,5,  (char[]){KEY_RIGHT_ARROW,    '\0','\0','\0'},     10,5,      0,0);
}

void loop() {
  keyboard->update();
}