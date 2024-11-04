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
const int readyPin = 9;

unsigned long markPoint = 0;
unsigned int temperature = DEFAULT_TEMPERATURE;
unsigned int curTemperature = 0;
bool run = false;
bool ready = false;

void setup() {
  Serial.begin(9600);
  BTSerial.begin(115200);
  SPI.begin();
  max.begin(MAX31865_2WIRE);

  pinMode(heatPin, OUTPUT);
  digitalWrite(heatPin, LOW);

  pinMode(readyPin, OUTPUT);
  digitalWrite(readyPin, LOW);

  pinMode(ringI, OUTPUT);
  pinMode(ringII, OUTPUT);
  pinMode(ringIII, OUTPUT);
  digitalWrite(ringI, HIGH);
  digitalWrite(ringII, HIGH);
  digitalWrite(ringIII, HIGH);
}

void loop() {
  btRecv();
  if (run) {
    if (ready) {
      unsigned long curr = millis();
      if (curr - markPoint < 8000 &&
        markPoint != 0) {
          digitalWrite(readyPin, HIGH);
          vibration();
          heat();
        } else {
          stop_vibration();
          stop_heat();
          run = false;
          ready = false;
          digitalWrite(readyPin, LOW);
        }
    } else {
      preHeat();
    }
  }

}
