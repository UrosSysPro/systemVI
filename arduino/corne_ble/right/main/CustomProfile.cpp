#include "CustomProfile.hpp"
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
#include "esp_bt_device.h"
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


static void gap_event_handler(esp_gap_ble_cb_event_t event, esp_ble_gap_cb_param_t *param)
{
	/*
	   switch (event) {
#ifdef CONFIG_SET_RAW_ADV_DATA
case ESP_GAP_BLE_ADV_DATA_RAW_SET_COMPLETE_EVT:
adv_config_done &= (~ADV_CONFIG_FLAG);
if (adv_config_done == 0){
esp_ble_gap_start_advertising(&adv_params);
}
break;
case ESP_GAP_BLE_SCAN_RSP_DATA_RAW_SET_COMPLETE_EVT:
adv_config_done &= (~SCAN_RSP_CONFIG_FLAG);
if (adv_config_done == 0){
esp_ble_gap_start_advertising(&adv_params);
}
break;
#else
case ESP_GAP_BLE_ADV_DATA_SET_COMPLETE_EVT:
adv_config_done &= (~ADV_CONFIG_FLAG);
if (adv_config_done == 0){
esp_ble_gap_start_advertising(&adv_params);
}
break;
case ESP_GAP_BLE_SCAN_RSP_DATA_SET_COMPLETE_EVT:
adv_config_done &= (~SCAN_RSP_CONFIG_FLAG);
if (adv_config_done == 0){
esp_ble_gap_start_advertising(&adv_params);
}
break;
#endif
case ESP_GAP_BLE_ADV_START_COMPLETE_EVT:
// advertising start complete event to indicate advertising start successfully or failed 
if (param->adv_start_cmpl.status != ESP_BT_STATUS_SUCCESS) {
ESP_LOGE(GATTS_TABLE_TAG, "advertising start failed");
}else{
ESP_LOGI(GATTS_TABLE_TAG, "advertising start successfully");
}
break;
case ESP_GAP_BLE_ADV_STOP_COMPLETE_EVT:
if (param->adv_stop_cmpl.status != ESP_BT_STATUS_SUCCESS) {
ESP_LOGE(GATTS_TABLE_TAG, "Advertising stop failed");
}
else {
ESP_LOGI(GATTS_TABLE_TAG, "Stop adv successfully");
}
break;
case ESP_GAP_BLE_UPDATE_CONN_PARAMS_EVT:
ESP_LOGI(GATTS_TABLE_TAG, "update connection params status = %d, conn_int = %d, latency = %d, timeout = %d",
param->update_conn_params.status,
param->update_conn_params.conn_int,
param->update_conn_params.latency,
param->update_conn_params.timeout);
break;
default:
break;
}
*/
}


static void gatts_event_handler(esp_gatts_cb_event_t event, esp_gatt_if_t gatts_if, esp_ble_gatts_cb_param_t *param)
{

	/* If event is register event, store the gatts_if for each profile */
	/*
	   if (event == ESP_GATTS_REG_EVT) {
	   if (param->reg.status == ESP_GATT_OK) {
	   heart_rate_profile_tab[PROFILE_APP_IDX].gatts_if = gatts_if;
	   } else {
	   ESP_LOGE(GATTS_TABLE_TAG, "reg app failed, app_id %04x, status %d",
	   param->reg.app_id,
	   param->reg.status);
	   return;
	   }
	   }
	   do {
	   int idx;
	   for (idx = 0; idx < PROFILE_NUM; idx++) {
	// ESP_GATT_IF_NONE, not specify a certain gatt_if, need to call every profile cb function 
	if (gatts_if == ESP_GATT_IF_NONE || gatts_if == heart_rate_profile_tab[idx].gatts_if) {
	if (heart_rate_profile_tab[idx].gatts_cb) {
	heart_rate_profile_tab[idx].gatts_cb(event, gatts_if, param);
	}
	}
	}
	} while (0);
	*/
}
