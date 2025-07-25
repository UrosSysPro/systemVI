#include"SystemVIKeyboard.h"
#include"Keyboard.h"

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
  },true,4,6);

  keyboard->setNormalKeycap(0,0,(char[]){KEY_MUTE,                '\0','\0','\0'},     0,0,     0,0);
  keyboard->setNormalKeycap(0,1,(char[]){KEY_VOLUME_INCREMENT,    '\0','\0','\0'},     1,0,     0,0);
  keyboard->setNormalKeycap(0,2,(char[]){KEY_VOLUME_DECREMENT,    '\0','\0','\0'},     2,0,     0,0);
  keyboard->setNormalKeycap(0,3,(char[]){KEY_PRINT_SCREEN,        '\0','\0','\0'},     3,0,     0,0);

  keyboard->setNormalKeycap(1,0,(char[]){KEY_NUM_LOCK,            '\0','\0','\0'},     0,1,     0,0);
  keyboard->setNormalKeycap(1,1,(char[]){KEY_KP_SLASH,            '\0','\0','\0'},     1,1,     0,0);
  keyboard->setNormalKeycap(1,2,(char[]){KEY_KP_ASTERISK,         '\0','\0','\0'},     2,1,     0,0);
  keyboard->setNormalKeycap(1,3,(char[]){KEY_KP_MINUS,            '\0','\0','\0'},     3,1,     0,0);

  keyboard->setNormalKeycap(2,0,(char[]){KEY_KP_7,                '\0','\0','\0'},     0,2,     0,0);
  keyboard->setNormalKeycap(2,1,(char[]){KEY_KP_8,                '\0','\0','\0'},     1,2,     0,0);
  keyboard->setNormalKeycap(2,2,(char[]){KEY_KP_9,                '\0','\0','\0'},     2,2,     0,0);
  keyboard->setNormalKeycap(2,3,(char[]){KEY_KP_PLUS,             '\0','\0','\0'},     3,2,     0,1);

  keyboard->setNormalKeycap(3,0,(char[]){KEY_KP_4,                '\0','\0','\0'},     0,3,     0,0);
  keyboard->setNormalKeycap(3,1,(char[]){KEY_KP_5,                '\0','\0','\0'},     1,3,     0,0);
  keyboard->setNormalKeycap(3,2,(char[]){KEY_KP_6,                '\0','\0','\0'},     2,3,     0,0);

  keyboard->setNormalKeycap(4,0,(char[]){KEY_KP_1,                '\0','\0','\0'},     0,4,     0,0);
  keyboard->setNormalKeycap(4,1,(char[]){KEY_KP_2,                '\0','\0','\0'},     1,4,     0,0);
  keyboard->setNormalKeycap(4,2,(char[]){KEY_KP_3,                '\0','\0','\0'},     2,4,     0,0);
  keyboard->setNormalKeycap(4,3,(char[]){KEY_KP_ENTER,            '\0','\0','\0'},     3,4,     0,1);
  
  keyboard->setNormalKeycap(5,1,(char[]){KEY_KP_0,                '\0','\0','\0'},     1,5,     4,0);
  keyboard->setNormalKeycap(5,2,(char[]){KEY_KP_DOT,              '\0','\0','\0'},     2,5,     0,0);

  keyboard->loadFromFlash();
}

void loop() {
  // put your main code here, to run repeatedly:
  keyboard->update();
}
