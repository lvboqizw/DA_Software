#include <MsTimer2.h>
#include <Adafruit_MAX31865.h>
#include <PID_v1.h>
#include <SoftwareSerial.h>

#define DEFAULT_TEMPERATURE 43
#define DEFAULT_FREQUENCY 177
#define DEFAULT_TEST 1
#define DEFAULT_STRENGTH 5

SoftwareSerial BTSerial (2, 3); // RX, TX
Adafruit_MAX31865 max = Adafruit_MAX31865(6);

const int heatPin = 10;
const int vibPin = 9;

bool ifVib = false;
bool ifHeat = false;
bool ifTimer = false;

int temperature;
int frequency;
int test;

unsigned long preMillis = 0;
unsigned long duration = 500;
unsigned long markPoint = 0;

void setup() {
  Serial.begin(9600);
  BTSerial.begin(115200);
  
  pinMode(heatPin, OUTPUT);
  digitalWrite(heatPin, LOW);

  pinMode(vibPin, OUTPUT);
  digitalWrite(vibPin, LOW);

  init_frq(DEFAULT_FREQUENCY);
  init_heat(DEFAULT_TEMPERATURE);
  init_vib(DEFAULT_STRENGTH);
}

void loop() {
  unsigned long curr = millis();
  if (curr - markPoint < 7000 && markPoint != 0) {
    vibration();
  } else {
    stop_vibration();
  }

  // if (ifHeat) {
  //   if (millis() - preMillis >= duration) {
  //     heat();
  //     preMillis = millis();
  //   }
  // } else {
  //   stop_heat();
  // }

  btRecv();
}

void analogInput() {
  // int sensorValue = analogRead(A0);
  // // // 将模拟输入值转换为电压
  // float voltage = sensorValue * (5.0 / 1023.0);
  // // 打印电压值到串行监视器

  int sensorTem = analogRead(A1);
  float temp = sensorTem * (5.0 / 1023.0);

  // Serial.print("Voltage:");
  // Serial.println(voltage);
  Serial.print("PID:");
  Serial.println(temp);
  // 每100毫秒读取一次
  // delay(100);
}
