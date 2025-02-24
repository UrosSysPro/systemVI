#include"Arduino.h"
#define COLUMNS_NUMBER 7
#define ROWS_NUMBER 3

#include <NimBLEDevice.h>
#include "HIDKeyboardTypes.h"  // Custom HID Report Descriptor

NimBLEServer *pServer;
NimBLEHIDDevice *hid;
NimBLECharacteristic *input;
bool connected = false;

int columns[]={21,20,10,7,6,5,3};
int rows[]={0,1,2};

struct Key{
  char value;
  bool pressed;//=false;
  bool justChanged;//=false;
};

// const byte thisAddress = 9;
// const byte otherAddress = 8;

// bool hasNewMessage=false;
// String message;

Key keys[COLUMNS_NUMBER][ROWS_NUMBER]={
  {{' ',false,false},{'a',false,false},{'a',false,false}},
  {{'t',false,false},{'g',false,false},{'b',false,false}},
  {{'r',false,false},{'f',false,false},{'v',false,false}},
  {{'e',false,false},{'d',false,false},{'c',false,false}},
  {{'w',false,false},{'s',false,false},{'x',false,false}},
  {{'q',false,false},{'a',false,false},{'z',false,false}},
  {{'a',false,false},{'a',false,false},{'a',false,false}}
};

void setup() {
  for(int i=0;i<COLUMNS_NUMBER;i++){
    int pin=columns[i];
    pinMode(pin, OUTPUT);
    digitalWrite(pin, 1);
  }
  for(int i=0;i<ROWS_NUMBER;i++){
    int pin = rows[i];
    pinMode(pin, INPUT_PULLUP);
  }
  for(int i=0;i<COLUMNS_NUMBER;i++){
    int pin=columns[i];
    pinMode(pin, INPUT_PULLUP);

  }
  for(int i=0;i<ROWS_NUMBER;i++){
    int pin = rows[i];
    pinMode(pin, OUTPUT);
    digitalWrite(pin, 1);
  }

  Serial.begin(9600);
  // Wire.begin(thisAddress);
  // Wire.setClock(10000);
  // Wire.onReceive(onReceived);
  // Wire.onRequest(onRequest);
  // Keyboard.begin();
 NimBLEDevice::init("ESP32C3-HID");

  pServer = NimBLEDevice::createServer();
  pServer->setCallbacks(new ServerCallbacks());

  hid = new NimBLEHIDDevice(pServer);
  input = hid->inputReport(0); 

  hid->manufacturer()->setValue("ESP32C3");
  hid->pnp(0x02, 0x1234, 0x5678, 0x0110);
  hid->hidInfo(0x00, 0x02);
  hid->reportMap((uint8_t*)hidReportDescriptor, sizeof(hidReportDescriptor));
  hid->startServices();

  NimBLEAdvertising *advertising = NimBLEDevice::getAdvertising();
  advertising->addServiceUUID(hid->hidService()->getUUID());
  advertising->setScanResponse(true);
  advertising->start();
}

void loop() {
  for(int j=0;j<ROWS_NUMBER;j++){
    int rowPin=rows[j];
    digitalWrite(rowPin, 0);
    delayMicroseconds(10);
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
          Serial.print("key pressed  ");
          Serial.print(i);
          Serial.print(" ");
          Serial.print(j);
          Serial.println("");
          // Keyboard.press(keys[i][j].value);
        }else{
          Serial.print("key released ");
          Serial.print(i);
          Serial.print(" ");
          Serial.print(j);
          Serial.println("");
          // Keyboard.release(keys[i][j].value);
        }
      }
    }
  }
  // if(hasNewMessage){
  //   Serial.print("message from i2c: ");
  //   Serial.println(message);
  //   hasNewMessage=false;
  // }
}

// void onRequest(){
//   String response = "h";
//   char buffer[18];  // 17 chars + null terminator
//   response.toCharArray(buffer, sizeof(buffer));  // Convert to C-string
//   Wire.write((byte*)buffer, strlen(buffer) + 1);  // Send with null-terminator
// }
// void onReceived(int size){
//   message="";
//   while(Wire.available()){
//     message+=String(Wire.read());
//   }
//   hasNewMessage=true;
// }