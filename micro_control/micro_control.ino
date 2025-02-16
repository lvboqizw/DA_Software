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

bool start = false;
bool heat = false;

const int btnPin = 9;
const int readyPin = 10;
int lastButtonState = HIGH;
int currentButtonState;  

void setup() {
  Serial.begin(9600);
  BTSerial.begin(115200);

  pinMode(btnPin, INPUT_PULLUP);
  // pinMode(btnPin, OUTPUT);
  digitalWrite(readyPin, LOW);
}

void loop() {
  checkBtn();

  if (start) {
    digitalWrite(readyPin, HIGH);
    running();
  } else {
    heater.stopHeat();
    ringI.stopVib();
    digitalWrite(readyPin, LOW);
  }
}

void checkBtn() {
  currentButtonState = digitalRead(btnPin);
  if (currentButtonState == HIGH && lastButtonState == LOW) {
    start = !start;
  }
  lastButtonState = currentButtonState;
}

void running() {
  float curTemperature = sensorT.getTemperature();
  heater.setCur(curTemperature);
  sendEverySec(String((int)curTemperature));

  btRecv();

  heater.heat();
  ringI.vib();
}
