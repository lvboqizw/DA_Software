#include <SPI.h>
#include <SoftwareSerial.h>
#include "sensorT.h"
#include "heater.h"
#include "vibRing.h"

SensorT sensorT(2);
Heater heater(5);
VibRing ringI(6);
SoftwareSerial BTSerial (4, 3); // RX, TX

unsigned long messageFlag = 0;
unsigned long markPoint = 0;

bool run = false;
bool ready = false;

const int readyPin = 9;

void setup() {
  Serial.begin(9600);
  BTSerial.begin(115200);

  pinMode(readyPin, OUTPUT);
  digitalWrite(readyPin, LOW);
}

void loop() {
  int curTemperature = sensorT.getTemperature();
  String message = "Temperature:" + String(curTemperature);
  sendEverySec(message); 
  heater.setCur(curTemperature);

  btRecv();

  if (run) {
    if (ready) {
      unsigned long curMillis = millis();
      if (curMillis - markPoint < 8000) {
        heater.heat();
        ringI.vib();
        digitalWrite(readyPin, HIGH);
      } else {
        run = false;
        ready = false;
        digitalWrite(readyPin, LOW);
      }
    } else {
      ready = heater.preHeat();
      markPoint = millis();
    }
  } else {
    heater.stopHeat();
    ringI.stopVib();
  }
}
