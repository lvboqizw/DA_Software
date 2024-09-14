#include <SoftwareSerial.h>

#define MAX_TOKENS 4
SoftwareSerial BTSerial (2, 3); // RX, TX

const int recPin = 8;
const int heatPin = 6;
const int vibPin = 5;

unsigned long preSigMillis = 0;
const unsigned long highLowTime = 2820;
bool signalState = LOW;
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
  // put your main code here, to run repeatedly:
  // preSigMillis = micros();
  if (vib) {
    vib_f();
  } else {
    signalState = LOW;
    digitalWrite(vibPin, signalState);
  }

  if (heat) {
    digitalWrite(heatPin, HIGH);
  } else {
    digitalWrite(heatPin, LOW);
  }

  if (rec) {
    digitalWrite(recPin, HIGH);
  } else {
    digitalWrite(recPin, LOW);
  }

  btRecv();
}

void vib_f() {
  unsigned long currentMillis = micros();
  if (currentMillis - preSigMillis >= highLowTime) {
    preSigMillis = currentMillis;
    signalState = !signalState;
    digitalWrite(vibPin, signalState);
  }
}

void btRecv() {
  if (BTSerial.available()) {
    String recData = BTSerial.readStringUntil('\n');
    recData.trim();

    Serial.print("Received: ");
    Serial.println(recData);

    String tokens[MAX_TOKENS];
    int tokenCount = split(recData, tokens);

    if (tokens[0] == "D") {
      if (tokenCount != 4) {
        Serial.println("Split Failed");
        return;
      }
      temperature = tokens[1].toInt();
      frequency = tokens[2].toInt();
      test = tokens[3].toInt();
    } else if (tokens[0] == "H") {
      heat = !heat;
    } else if (tokens[0] == "V") {
      vib = !vib;
    }
  }
}

int split(String recData, String token[]) {
  int tokenCount = 0;
  int startIndex = 0;

  for (int i = 0; i < recData.length(); i ++) {
    if (recData.charAt(i) == ',') {
      token[tokenCount++] = recData.substring(startIndex, i);
      startIndex = i + 1;
      if (tokenCount >= MAX_TOKENS) {
        break;
      }
    }
  }
  token[tokenCount ++] = recData.substring(startIndex);
  return tokenCount;
}
