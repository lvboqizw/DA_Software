#include "heater.h"
#include <Arduino.h>

Heater::Heater(int p) {
  pin = p;
  pinMode(pin, OUTPUT);
  targetTemperature = DEFAULT_TEMPERATURE;
  active = false;
}

void Heater::heat() {
  if (active) {
    if(curTemperature < targetTemperature - 1.5) {
      digitalWrite(pin, HIGH);
    } else if(curTemperature > targetTemperature - 0.5) {
      digitalWrite(pin, LOW);
    }
  } else {
    digitalWrite(pin, LOW);
  }
}

void Heater::stopHeat() {
  digitalWrite(pin, LOW);
  active = false;
  ready = false;
}

void Heater::preHeat() {
  Heater::heat();
  if(curTemperature > targetTemperature - 2.5) {
    ready = true;
  }
}

void Heater::setTarget(int target) {
  targetTemperature = curTemperature + target;
}

void Heater::setCur(int cur) {
  curTemperature = cur;
}

void Heater::on() {
  active = true;
}

void Heater::off() {
  active = false;
}

bool Heater::checkReady() {
  return ready;
}
