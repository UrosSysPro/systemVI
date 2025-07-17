#include<stdio.h>
#include<stdlib.h>
#include<string.h>
#include"freertos/FreeRTOS.h"
#include"freertos/event_groups.h"
#include"freertos/task.h"
#include"esp_flash.h"
#include"esp_bt.h"
#include"nvs_flash.h"

#include"lib/esp_hidd_prf_api.h"
#include"esp_bt_defs.h"
#include"esp_gap_ble_api.h"
#include"esp_bt_main.h"
#include"lib/hid_dev.h"
#include"main.cpp" 


void task_main(void *params){
	while(1){
		loop();
	}
}

extern "C" void app_main(void){
	setup();
	xTaskCreate(&task_main,"task_main", 2048, NULL, 5, NULL);
}
