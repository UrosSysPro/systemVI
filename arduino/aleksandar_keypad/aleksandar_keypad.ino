#include "Arduino.h"
int rows[]={0,1,2};
int cols[]={21,20,10,7,6,5,3};

void setup() {
  for(int i = 0; i<3; i++){
    pinMode(rows[i], OUTPUT);
    digitalWrite(rows[i], HIGH);
  }
   for(int i = 0; i<7; i++){
    pinMode(cols[i], PULLUP);
  }

// the loop function runs over and over again forever
void loop() {
    for(int i = 0; i<3; i++){
      for(int j = 0; j<7; j++){
          
      }
    }
 
}