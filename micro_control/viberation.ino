void vibration() {
  if (rings & 0b100) {
    digitalWrite(ringIII, LOW);
    SPI.transfer(nodes);
    digitalWrite(ringIII, HIGH);
  }
  if (rings & 0b010) {
    digitalWrite(ringII, LOW);
    SPI.transfer(nodes);
    digitalWrite(ringII, HIGH);
  }
  if (rings & 0b001) {
    digitalWrite(ringI, LOW);
    SPI.transfer(nodes);
    digitalWrite(ringI, HIGH);
  }
}

void stop_vibration() {
  digitalWrite(ringI, LOW);
  SPI.transfer(0);
  digitalWrite(ringI, HIGH);
  digitalWrite(ringII, LOW);
  SPI.transfer(0);
  digitalWrite(ringII, HIGH);
  digitalWrite(ringIII, LOW);
  SPI.transfer(0);
  digitalWrite(ringIII, HIGH);
}

void vibration_test(int ringT, int nodeT) {
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
}
