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
}

bool Heater::preHeat() {
  if (curTemperature < targetTemperature - 2) {
    digitalWrite(pin, HIGH);
    return false;
  } else {
    return true;
  }
}

void Heater::setTarget(int target) {
  targetTemperature = target;
}

void Heater::setCur(int cur) {
  curTemperature = cur;
}

bool Heater::getReady() {
  if (curTemperature < targetTemperature - 2) {
    return false;
  } else {
    return true;
  }
}
