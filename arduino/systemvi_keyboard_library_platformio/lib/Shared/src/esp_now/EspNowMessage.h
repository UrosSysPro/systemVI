#ifndef __ESP_NOW_MESSAGE
#define __ESP_NOW_MESSAGE

#include <WiFi.h>

struct EspNowMessage{
    int size;
    uint8_t data[255];
    uint8_t senderMacAddress[6];
};

#endif
