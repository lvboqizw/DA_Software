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
bool start = false;

const int btnPin = 9;
int lastButtonState = HIGH;
int currentButtonState;  

void setup() {
  Serial.begin(9600);
  BTSerial.begin(115200);

  pinMode(btnPin, INPUT_PULLUP);
}

void loop() {
  checkBtn();

  if (start) {
    processing();
  } else {
    run = false;
    heater.stopHeat();
    ringI.stopVib();
  }
}

void checkBtn() {
  currentButtonState = digitalRead(btnPin);
  if (currentButtonState == HIGH && lastButtonState == LOW) {
    start = !start;
  }
  lastButtonState = currentButtonState;
}

void processing() {
  int curTemperature = sensorT.getTemperature();
  String message = "Temperature:" + String(curTemperature);
  sendEverySec(message); 
  heater.setCur(curTemperature);

  btRecv();

  bool ready = heater.preHeat();

  if (ready) {
    if (run) {
      unsigned long curMillis = millis();
      if (curMillis - markPoint < 8000) {
        heater.heat();
        ringI.vib();
      } else {
        run = false;
        ready = false;
      }
    } else {
      heater.stopHeat();
      ringI.stopVib();
      markPoint = millis();
    }
  }
}
