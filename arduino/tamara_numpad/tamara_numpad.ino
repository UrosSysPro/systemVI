#include"SystemVIKeyboard.h"

SystemVIKeyboard* keyboard;

void setup() {
  // put your setup code here, to run once:
  char name[]="numpad";
  keyboard=new SystemVIKeyboard(name,6,4,
  (int[]){
    7,8,9,10,11,13
  },
  (int[]){
    3,4,5,6
  },true);

  keyboard->setNormalKeycap(0,0,(char[]){'1','\0','\0','\0'},     0,0,     0,0);
  keyboard->setNormalKeycap(0,1,(char[]){'2','\0','\0','\0'},     0,1,     0,0);
  keyboard->setNormalKeycap(0,2,(char[]){'3','\0','\0','\0'},     0,2,     0,0);
  keyboard->setNormalKeycap(0,3,(char[]){'4','\0','\0','\0'},     0,3,     0,0);

  keyboard->setNormalKeycap(1,0,(char[]){'a','\0','\0','\0'},     1,0,     0,0);
  keyboard->setNormalKeycap(1,1,(char[]){'s','\0','\0','\0'},     1,1,     0,0);
  keyboard->setNormalKeycap(1,2,(char[]){'d','\0','\0','\0'},     1,2,     0,0);
  keyboard->setNormalKeycap(1,3,(char[]){'f','\0','\0','\0'},     1,3,     0,0);

  keyboard->setNormalKeycap(2,0,(char[]){'q','\0','\0','\0'},     2,0,     0,0);
  keyboard->setNormalKeycap(2,1,(char[]){'w','\0','\0','\0'},     2,1,     0,0);
  keyboard->setNormalKeycap(2,2,(char[]){'e','\0','\0','\0'},     2,2,     0,0);
  keyboard->setNormalKeycap(2,3,(char[]){'r','\0','\0','\0'},     2,3,     0,1);

  keyboard->setNormalKeycap(3,0,(char[]){'z','\0','\0','\0'},     3,0,     0,0);
  keyboard->setNormalKeycap(3,1,(char[]){'x','\0','\0','\0'},     3,1,     0,0);
  keyboard->setNormalKeycap(3,2,(char[]){'c','\0','\0','\0'},     3,2,     0,0);

  keyboard->setNormalKeycap(4,0,(char[]){'h','\0','\0','\0'},     4,0,     0,0);
  keyboard->setNormalKeycap(4,1,(char[]){'j','\0','\0','\0'},     4,1,     0,0);
  keyboard->setNormalKeycap(4,2,(char[]){'k','\0','\0','\0'},     4,2,     0,0);
  keyboard->setNormalKeycap(4,3,(char[]){'l','\0','\0','\0'},     4,3,     0,1);
  
  keyboard->setNormalKeycap(5,1,(char[]){'m','\0','\0','\0'},     5,1,     3,0);
  keyboard->setNormalKeycap(5,2,(char[]){',','\0','\0','\0'},     5,2,     0,0);
}

void loop() {
  // put your main code here, to run repeatedly:
  keyboard->update();
}
