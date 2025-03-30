#include"Arduino.hpp"
#include"driver/gpio.h"
#include"freertos/FreeRTOS.h"
#include"freertos/task.h"
#include"freertos/timers.h"
gpio_num_t gpio_pins[]={
	GPIO_NUM_0,
	GPIO_NUM_1,
	GPIO_NUM_2,
	GPIO_NUM_3,
	GPIO_NUM_4,
	GPIO_NUM_5,
	GPIO_NUM_6,
	GPIO_NUM_7,
	GPIO_NUM_8,
	GPIO_NUM_9,
	GPIO_NUM_10,
	GPIO_NUM_11,
	GPIO_NUM_12,
	GPIO_NUM_13,
	GPIO_NUM_14,
	GPIO_NUM_15,
	GPIO_NUM_16,
	GPIO_NUM_17,
	GPIO_NUM_18,
	GPIO_NUM_19,
	GPIO_NUM_20,
	GPIO_NUM_21,
};

void pinMode(int pin, pin_mode_t mode){
	if(mode==INPUT){
		gpio_set_direction(gpio_pins[pin],GPIO_MODE_INPUT);
	}

	if(mode==INPUT_PULLDOWN){
		gpio_set_direction(gpio_pins[pin],GPIO_MODE_INPUT);
		gpio_pulldown_en(gpio_pins[pin]);
	}

	if(mode==INPUT_PULLUP){
		gpio_set_direction(gpio_pins[pin],GPIO_MODE_INPUT);
		gpio_pullup_en(gpio_pins[pin]);
	}

	if(mode==OUTPUT){
		gpio_set_direction(gpio_pins[pin],GPIO_MODE_OUTPUT);
	}
}
void digitalWrite(int pin, int value){
	gpio_set_level(gpio_pins[pin],value);	
}

int digitalRead(int pin){
	return gpio_get_level(gpio_pins[pin]);
}

int analogRead(int pin){
	return digitalRead(pin);
}

void delay(int millis){
	vTaskDelay(millis/portTICK_PERIOD_MS);
}
