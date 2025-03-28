#ifndef __BLUETOOTH__
#define __BLUETOOTH__
#include "freertos/FreeRTOS.h"
#include "freertos/task.h"
#include "freertos/event_groups.h"
#include "esp_system.h"
#include "esp_log.h"
#include "nvs_flash.h"
#include "esp_bt.h"

#include "esp_gap_ble_api.h"
#include "esp_gatts_api.h"
#include "esp_bt_main.h"
#include "esp_gatt_common_api.h"
class BluetoothProfile{

};

class BluetoothProfileConfig{
	public:
	int id,mtu;
	esp_gatts_cb_t gattsCallback;
	esp_gap_ble_cb_t gapCallback;
};

class Bluetooth{
	public:
	static BluetoothProfile createProfile(BluetoothProfileConfig config);
	static void init();
	static void enableSecurity();
};
#endif
