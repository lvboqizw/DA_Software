#include "vibRing.h"
#include <SPI.h>
#include <Arduino.h>


VibRing::VibRing(int p) {
  pin = p;
  pinMode(pin, OUTPUT);
  digitalWrite(pin, HIGH);
  SPI.begin();
  active = false;
}

void VibRing::setNodes(int n) {
  nodes = n;
}

int VibRing::getNodes() {
  return nodes;
}

void VibRing::vib() {
  digitalWrite(pin, LOW);
  SPI.transfer(nodes);
  digitalWrite(pin, HIGH);
}

void VibRing::stopVib() {
  digitalWrite(pin, LOW);
  nodes = 0;
  SPI.transfer(0);
  digitalWrite(pin, HIGH);
}

void VibRing::on() {
  active = true;
}
void VibRing::off() {
  active = false;
}
