#include"Arduino.hpp"
#include"driver/gpio.h"
void pinMode(int pin, pin_mode_t mode){
	if(mode==INPUT){
		gpio_set_direction(GPIO_NUM_1,GPIO_MODE_INPUT);
	}
}
void digitalWrite(int pin, int value){

}

int digitalRead(int pin){
	return 0;
}

int analogRead(int pin){
	return 0;
}
