unsigned long highLowTime = 2820;
unsigned long preSigMillis = 0;
bool signalState = LOW;

void setFrequency(int frequency) {
  highLowTime = 500000 / frequency;  // (1000000 / frequecy) / 2
}

void vibration(int vibPin, bool vib) {
  if (vib) {
    unsigned long currentMillis = micros();
    if (currentMillis - preSigMillis >= highLowTime) {
      preSigMillis = currentMillis;
      signalState = !signalState;
      digitalWrite(vibPin, signalState);
    }
  } else {
    signalState = LOW;
    digitalWrite(vibPin, signalState);
  }
}

