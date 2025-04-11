#include"lib/systemvi/Arduino.hpp"
#include"lib/systemvi/Keyboard.hpp"
#include"lib/systemvi/Bluetooth.hpp"
#include<stdio.h>
#include"lib/hid_dev.h"
//#define DEBUG
#define COLUMNS_NUMBER 7
#define ROWS_NUMBER 3

int rows[]={3,2,0};
int columns[]={4,20,10,9,8,7,1};

struct Key{
	uint8_t l0,l1,l2;
	bool pressed,justChanged;
};

Key keys[COLUMNS_NUMBER][ROWS_NUMBER];

void setup() {
	for(int i=0;i<COLUMNS_NUMBER;i++){
		for(int j=0;j<ROWS_NUMBER;j++){
			keys[i][j].pressed=false;
			keys[i][j].justChanged=false;
			keys[i][j].l0='\0';
			keys[i][j].l1='\0';
			keys[i][j].l2='\0';
		}
	}
	keys[0][0].l0=HID_KEY_ESCAPE;
	keys[1][0].l0=HID_KEY_Q;
	keys[2][0].l0=HID_KEY_W;
	keys[3][0].l0=HID_KEY_E;
	keys[4][0].l0=HID_KEY_R;
	keys[5][0].l0=HID_KEY_T;
	keys[6][0].l0=HID_KEY_SPACEBAR;

	keys[0][1].l0=HID_KEY_TAB;
	keys[1][1].l0=HID_KEY_A;
	keys[2][1].l0=HID_KEY_S;
	keys[3][1].l0=HID_KEY_D;
	keys[4][1].l0=HID_KEY_F;
	keys[5][1].l0=HID_KEY_G;
	keys[6][1].l0=HID_KEY_A;

	keys[0][2].l0=HID_KEY_LEFT_CTRL;
	keys[1][2].l0=HID_KEY_Z;
	keys[2][2].l0=HID_KEY_X;
	keys[3][2].l0=HID_KEY_C;
	keys[4][2].l0=HID_KEY_V;
	keys[5][2].l0=HID_KEY_B;
	keys[6][2].l0=HID_KEY_A;

	for(int i=0;i<COLUMNS_NUMBER;i++){
		printf("pin %d input pullup\n",columns[i]);
		int pin=columns[i];
		pinMode(pin, INPUT_PULLUP);
	}
	for(int i=0;i<ROWS_NUMBER;i++){
		printf("pin %d output\n",rows[i]);
		int pin = rows[i];
		pinMode(pin, OUTPUT);
		digitalWrite(pin, 1);
	}
	Bluetooth::init();
	Keyboard::init();
	Bluetooth::enableSecurity();
}

void loop() {
	for(int j=0;j<ROWS_NUMBER;j++){
		int rowPin=rows[j];
		digitalWrite(rowPin, 0);
		delay(10);
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
#ifdef DEBUG
					printf("key pressed  %d %d\n",i,j);
#else
					Keyboard::press(keys[i][j].l0);
#endif
				}else{
#ifdef DEBUG
					printf("key released %d %d\n",i,j);
#else
					Keyboard::release(keys[i][j].l0);
#endif
				}
			}
		}
	}
}
