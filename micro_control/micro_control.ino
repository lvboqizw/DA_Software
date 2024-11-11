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
const int readyPin = 10;
int lastButtonState = HIGH;
int currentButtonState;  

void setup() {
  Serial.begin(9600);
  BTSerial.begin(115200);

  pinMode(btnPin, INPUT_PULLUP);
  pinMode(btnPin, OUTPUT);
  digitalWrite(readyPin, LOW);
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
  heater.setCur(curTemperature);
  sendEverySec(String(curTemperature)); 

  btRecv();

  heater.preHeat();

  if (heater.checkReady()) {
    digitalWrite(readyPin, HIGH);
  } else {
    digitalWrite(readyPin, LOW);
  }

  if (run) {
    if (heater.checkReady()) {
      unsigned long curMillis = millis();
      if (curMillis - markPoint < 8000) {
        heater.heat();
        ringI.vib();
      } else {
        run = false;
        ringI.stopVib();
      }
    } else {
      Serial.println("Not Ready");
    }
  }
}
