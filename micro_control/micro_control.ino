#include <Adafruit_MAX31865.h>
#include <PID_v1_bc.h>
#include <SoftwareSerial.h>

SoftwareSerial BTSerial (2, 3); // RX, TX

const int recPin = 8;
const int heatPin = 6;
const int vibPin = 5;

bool vib = false;
bool heat = false;
bool rec = false;

int temperature;
int frequency;
int test;

void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);
  BTSerial.begin(115200);

  pinMode(recPin, OUTPUT);
  pinMode(heatPin, OUTPUT);
  pinMode(vibPin, OUTPUT);

  digitalWrite(recPin, LOW);
  digitalWrite(heatPin, LOW);
  digitalWrite(vibPin, LOW);
}

void loop() {

  vibration(vibPin, vib);
  pidHeat(heatPin, heat);

  // if (heat) {
  //   digitalWrite(heatPin, HIGH);
  // } else {
  //   digitalWrite(heatPin, LOW);
  // }

  if (rec) {
    digitalWrite(recPin, HIGH);
  } else {
    digitalWrite(recPin, LOW);
  }

  btRecv();
}
