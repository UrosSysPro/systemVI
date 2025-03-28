#include"Bluetooth.hpp"
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

#define GATTS_TABLE_TAG "GATTS_TABLE_DEMO"

#define PROFILE_NUM                 1
#define PROFILE_APP_IDX             0
#define ESP_APP_ID                  0x55
#define SAMPLE_DEVICE_NAME          "ESP_GATTS_DEMO"
#define SVC_INST_ID                 0

/* The max length of characteristic value. When the GATT client performs a write or prepare write operation,
*  the data length must be less than GATTS_DEMO_CHAR_VAL_LEN_MAX.
*/
#define GATTS_DEMO_CHAR_VAL_LEN_MAX 500
#define PREPARE_BUF_MAX_SIZE        1024
#define CHAR_DECLARATION_SIZE       (sizeof(uint8_t))

#define ADV_CONFIG_FLAG             (1 << 0)
#define SCAN_RSP_CONFIG_FLAG        (1 << 1)

void initFlash(){
	esp_err_t ret;
		
	ret=nvs_flash_init();
	if(ret==ESP_ERR_NVS_NO_FREE_PAGES || ret == ESP_ERR_NVS_NEW_VERSION_FOUND){
		nvs_flash_erase();
		ret=nvs_flash_init();
	}
}
void enableBleController(){
	esp_err_t ret;
	esp_bt_controller_mem_release(ESP_BT_MODE_CLASSIC_BT);
	esp_bt_controller_config_t bt_config=BT_CONTROLLER_INIT_CONFIG_DEFAULT();
	ret=esp_bt_controller_init(&bt_config);
	if(ret){
		printf("initialize controller failed\n");
		exit(1);
	}	
	ret=esp_bt_controller_enable(ESP_BT_MODE_BLE);
	if(ret){
		printf("enable ble controller failed\n");
		exit(1);
	}
}
void enableBluedroid(){
	esp_err_t ret;
	ret=esp_bluedroid_init();
	if(ret){
		printf("init bluedroid failed\n");
		exit(1);
	}
	ret=esp_bluedroid_enable();
	if(ret){
		printf("enable bluedroid failedi\n");
		exit(1);
	}	
}
void enableBleSecurity(){
	esp_ble_auth_req_t auth_req=ESP_LE_AUTH_BOND;
	esp_ble_io_cap_t iocap=ESP_IO_CAP_NONE;
	uint8_t key_size=16;
	uint8_t init_key = ESP_BLE_ENC_KEY_MASK|ESP_BLE_ID_KEY_MASK;
	uint8_t rsp_key = ESP_BLE_ENC_KEY_MASK|ESP_BLE_ID_KEY_MASK;
	esp_ble_gap_set_security_param(ESP_BLE_SM_AUTHEN_REQ_MODE,&auth_req,sizeof(uint8_t));
	esp_ble_gap_set_security_param(ESP_BLE_SM_IOCAP_MODE,&iocap,sizeof(uint8_t));
	esp_ble_gap_set_security_param(ESP_BLE_SM_MAX_KEY_SIZE,&key_size,sizeof(uint8_t));
	esp_ble_gap_set_security_param(ESP_BLE_SM_MAX_KEY_SIZE,&init_key,sizeof(uint8_t));
	esp_ble_gap_set_security_param(ESP_BLE_SM_MAX_KEY_SIZE,&rsp_key,sizeof(uint8_t));
}
void Bluetooth::init(){
	initFlash();	
	enableBleController();
	enableBluedroid();
}
void Bluetooth::enableSecurity(){
	enableBleSecurity();
}

void registerProfile(int app_id,esp_gatts_cb_t gattsCallback,esp_gap_ble_cb_t gapCallback,int mtu){
	esp_err_t ret = esp_ble_gatts_register_callback(gattsCallback);
    if (ret){
        ESP_LOGE(GATTS_TABLE_TAG, "gatts register error, error code = %x", ret);
        return;
    }

    ret = esp_ble_gap_register_callback(gapCallback);
    if (ret){
        ESP_LOGE(GATTS_TABLE_TAG, "gap register error, error code = %x", ret);
        return;
    }

    ret = esp_ble_gatts_app_register(app_id);
    if (ret){
        ESP_LOGE(GATTS_TABLE_TAG, "gatts app register error, error code = %x", ret);
        return;
    }

    esp_err_t local_mtu_ret = esp_ble_gatt_set_local_mtu(mtu);
    if (local_mtu_ret){
        ESP_LOGE(GATTS_TABLE_TAG, "set local  MTU failed, error code = %x", local_mtu_ret);
    }

}


BluetoothProfile Bluetooth::createProfile(BluetoothProfileConfig config){
	registerProfile(config.id,config.gattsCallback,config.gapCallback,config.mtu);
	return BluetoothProfile();
}
