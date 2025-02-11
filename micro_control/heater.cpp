#include "heater.h"
#include <Arduino.h>


Heater::Heater(int p): pin(p), active(false),
  myPID(&curTemperature, &output, &targetTemperature, Kp, Ki, Kd, DIRECT) {
  pinMode(pin, OUTPUT);
  myPID.SetMode(AUTOMATIC);
  myPID.SetOutputLimits(0, 100);
}

void Heater::heat() {
  if (active) {
    if(output >= onThreshold) {
      digitalWrite(pin, HIGH);
    } else if(curTemperature <= offThreshold ) {
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
  // targetTemperature = curTemperature + target;
  targetTemperature = BASE_TEMPERATURE + target;
}

void Heater::setCur(float cur) {
  curTemperature = cur;
  myPID.Compute();
}

void Heater::on() {
  active = true;
}

void Heater::off() {
  active = false;
}
