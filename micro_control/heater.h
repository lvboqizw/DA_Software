#ifndef HEATER_H
#define HEATER_H

#include <PID_v1.h>

#define BASE_TEMPERATURE 30

class Heater {
  private:
    int pin;
    double targetTemperature;
    double curTemperature;
    double output;
    int active;
    bool ready;

    double Kp = 20.0, Ki = 50.0, Kd = 10.0;
    PID myPID;
    const double onThreshold = 60.0;
    const double offThreshold = 40.0;

  public:
    Heater(int p);
    void heat();
    void stopHeat();
    void setTarget(float target);
    void setCur(float cur);
    void on();
    void off();
};

#endif
