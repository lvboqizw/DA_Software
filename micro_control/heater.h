 #ifndef HEATER_H
#define HEATER_H

#define DEFAULT_TEMPERATURE 39

class Heater {
  private:
    int pin;
    int targetTemperature;
    int curTemperature;
    int active;
    bool ready;

  public:
    Heater(int p);
    void heat();
    void stopHeat();
    void preHeat();
    bool checkReady();
    void setTarget(int target);
    void setCur(int cur);
    void on();
    void off();
};

#endif
