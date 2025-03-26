#ifndef __KEYBOARD__
#define __KEYBOARD__
#include"freertos/FreeRTOS.h"
class Keyboard{
	public:
	static void press(uint8_t key);
	static void release(uint8_t key);
	static void init();
	static bool isConnected();
};

#endif
