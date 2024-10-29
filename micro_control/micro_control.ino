#include <MsTimer2.h>
#include <Adafruit_MAX31865.h>
#include <PID_v1.h>
#include <SoftwareSerial.h>

#define DEFAULT_TEMPERATURE 40
#define DEFAULT_FREQUENCY 177
#define DEFAULT_STRENGTH 5

SoftwareSerial BTSerial (3, 2); // RX, TX
Adafruit_MAX31865 max = Adafruit_MAX31865(10);

const int heatPin = 5;
const int dataPin = 6;
const int latchPin = 7;
const int clockPin = 8;

unsigned long markPoint = 0;

bool oneRound = true;

void setup() {
  Serial.begin(9600);
  BTSerial.begin(115200);

  pinMode(heatPin, OUTPUT);
  digitalWrite(heatPin, LOW);

  pinMode(dataPin, OUTPUT);
  pinMode(latchPin, OUTPUT);
  pinMode(clockPin, OUTPUT);

  digitalWrite(latchPin, HIGH);

  // init_frq(DEFAULT_FREQUENCY);
  init_heat(DEFAULT_TEMPERATURE);
  init_vib(DEFAULT_STRENGTH);

  markPoint = millis();
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
  
  if (oneRound) {
    vibration_test(3, 1);
    oneRound = false;
  }

  if (curr - markPoint < 8000 && markPoint != 0) {
    heat();
  } else {
    stop_heat();
  }

  btRecv();
}

void analogInput() {
  int sensorTem = analogRead(A1);
  float temp = sensorTem * (5.0 / 1023.0);

  Serial.print("Voltage:");
  Serial.println(temp);
  // 每100毫秒读取一次
  delay(100);
}
