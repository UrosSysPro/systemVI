#ifndef __ARDUINO__
#define __ARDUINO__

enum pin_mode_t{
	INPUT,
	OUTPUT,
	INPUT_PULLUP,
	INPUT_PULLDOWN,
	ANALOG_INPUT
};

void pinMode(int pin, pin_mode_t mode);
int digitalRead(int pin);
void digitalWrite(int pin, int value);
int analogRead(int pin);
void delay(int millis);
#endif
