#include "heater.h"
#include <Arduino.h>

Heater::Heater(int p): pin(p), active(false) {
  pinMode(pin, OUTPUT);
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

void Heater::setTarget(float target) {
  targetTemperature = curTemperature + target;
}

void Heater::setCur(float cur) {
  curTemperature = cur;
}

void Heater::on() {
  active = true;
}

void Heater::off() {
  active = false;
}
