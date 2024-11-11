#include "heater.h"
#include <Arduino.h>

Heater::Heater(int p) {
  pin = p;
  pinMode(pin, OUTPUT);
  targetTemperature = DEFAULT_TEMPERATURE;
}

void Heater::heat() {
  if(curTemperature < targetTemperature - 1.5) {
    digitalWrite(pin, HIGH);
  } else if(curTemperature > targetTemperature - 0.5) {
    digitalWrite(pin, LOW);
  }
}

void Heater::stopHeat() {
  digitalWrite(pin, LOW);
  ready = false;
}

void Heater::preHeat() {
  Heater::heat();
  if(curTemperature > targetTemperature - 2.5) {
    ready = true;
  }
}

void Heater::setTarget(int target) {
  targetTemperature = target;
}

void Heater::setCur(int cur) {
  curTemperature = cur;
}

bool Heater::checkReady() {
  return ready;
}
