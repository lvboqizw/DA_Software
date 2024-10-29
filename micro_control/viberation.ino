int strength = 0;

void init_vib(int strength_int) {
  strength = strength_int;
}

void set_strength(int strength_int) {
  strength = strength_int;
}

void vibration() {
  digitalWrite(latchPin, LOW);
  if (rings & 0b100) {
    shiftOut(dataPin, clockPin, MSBFIRST, nodes);
  } else {
    shiftOut(dataPin, clockPin, MSBFIRST, 0);
  }
  if (rings & 0b010) {
    shiftOut(dataPin, clockPin, MSBFIRST, nodes);
  } else {
    shiftOut(dataPin, clockPin, MSBFIRST, 0);
  }
  if (rings & 0b001) {
    shiftOut(dataPin, clockPin, MSBFIRST, nodes);
  } else {
    shiftOut(dataPin, clockPin, MSBFIRST, 0);
  }
  digitalWrite(latchPin, HIGH);
}

void stop_vibration() {
  digitalWrite(latchPin, LOW);
  shiftOut(dataPin, clockPin, MSBFIRST, 0x0);
  shiftOut(dataPin, clockPin, MSBFIRST, 0x0);
  shiftOut(dataPin, clockPin, MSBFIRST, 0x0);
  digitalWrite(latchPin, HIGH);
}

void vibration_test(int ringT, int nodeT) {
  digitalWrite(latchPin, LOW);
  Serial.println("Vib test");
  if (ringT & 0b100) {
    Serial.print("Ring 1--, Node ");
    Serial.println(nodeT);
  } else {
    Serial.println("Not ring 1--");
  }
  if (ringT & 0b010) {
    Serial.print("Ring -1-, Node ");
    Serial.println(nodeT);
  } else {
    Serial.println("Not ring -1-");
  }
  if (ringT & 0b001) {
    Serial.print("Ring --1, Node ");
    Serial.println(nodeT);
  } else {
    Serial.println("Not ring --1");
  }
  digitalWrite(latchPin, LOW);
}



