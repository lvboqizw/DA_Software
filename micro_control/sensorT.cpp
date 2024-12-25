#include "sensorT.h"

SensorT::SensorT(int p): myMax(p) {
  myMax.begin(MAX31865_2WIRE);
}

float SensorT::getTemperature() {
  return myMax.temperature(RNOMINAL, RREF)-4;
}
