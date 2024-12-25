#ifndef HEATER_H
#define HEATER_H

#define DEFAULT_TEMPERATURE 39

class Heater {
  private:
    int pin;
    float targetTemperature;
    float curTemperature;
    int active;
    bool ready;

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
