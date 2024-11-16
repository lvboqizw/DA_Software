#ifndef VIBRING_H
#define VIBRING_H
class VibRing {
  private:
    int pin;
    int nodes;
    bool active;

  public: 
    VibRing(int p);
    void setNodes(int n);
    int getNodes();
    void vib();
    void stopVib();
    void on();
    void off();
};

#endif
