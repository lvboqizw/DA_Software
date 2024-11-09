#ifndef SENSORT_H
#define SENSORT_H

#include <Adafruit_MAX31865.h>
#include <Arduino.h>

#define RREF      430.0  // Reference Resistor for MAX31865
#define RNOMINAL  100.0  // Nominal Resistance of the PT100

class SensorT {
  private:
    Adafruit_MAX31865 myMax;

  public:
    SensorT(int p);
    int getTemperature();

};

#endif
