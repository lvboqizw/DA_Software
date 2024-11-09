#ifndef HEATER_H
#define HEATER_H

#define DEFAULT_TEMPERATURE 40

class Heater {
  private:
    int pin;
    int targetTemperature;
    int curTemperature;

  public:
    Heater(int p);
    void heat();
    void stopHeat();
    bool preHeat();
    void setTarget(int target);
    void setCur(int cur);
};

#endif
