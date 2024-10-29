#include <SPI.h>
#include <Adafruit_MAX31865.h>
#include <SoftwareSerial.h>

#define DEFAULT_TEMPERATURE 40
#define DEFAULT_FREQUENCY 177
#define DEFAULT_STRENGTH 5

SoftwareSerial BTSerial (4, 3); // RX, TX
Adafruit_MAX31865 max = Adafruit_MAX31865(2);

const int heatPin = 5;
const int ringI = 6;
const int ringII = 7;
const int ringIII = 8;

unsigned long markPoint = 0;
unsigned int temperature = DEFAULT_TEMPERATURE;

void setup() {
  Serial.begin(9600);
  BTSerial.begin(115200);
  SPI.begin();
  max.begin(MAX31865_2WIRE);

  pinMode(heatPin, OUTPUT);
  digitalWrite(heatPin, LOW);

  pinMode(ringI, OUTPUT);
  pinMode(ringII, OUTPUT);
  pinMode(ringIII, OUTPUT);
  digitalWrite(ringI, HIGH);
  digitalWrite(ringII, HIGH);
  digitalWrite(ringIII, HIGH);
}

void loop() {
  unsigned long curr = millis();
  // if (curr - markPoint > 3000 &&
  //   curr - markPoint < 8000 &&
  //   markPoint != 0) {
  //     vibration_test(1, 1);
  //   } else {
  //     stop_vibration();
  //   }

  if (curr - markPoint < 4000 && markPoint != 0) {
    digitalWrite(heatPin, HIGH);
  } else {
    digitalWrite(heatPin, LOW);
  }

  // if (curr - markPoint < 8000 && markPoint != 0) {
  //   heat();
  // } else {
  //   stop_heat();
  // }

  btRecv();
}
