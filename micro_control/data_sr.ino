String message = "";

void btRecv() {
  if (BTSerial.available()) {
    String recData = BTSerial.readStringUntil('\n');
    recData.trim();

    String recMessage = extractData(recData);
    if (calculateChecksum(recMessage) != extractChecksum(recData)) {
      Serial.print("Uncorrect message: ");
      Serial.println(recMessage);
      clearBTSerialBuffer();
      BTSerial.write("R");
      return;
    }
    
    message = recMessage;
    BTSerial.write("A");
  }

  if (message.length() != 0) {
    // Serial.println(message);
    trigger();
  }
}

void trigger() {
  String messagePartI = "";
  String messagePartII = "";

  sliptMessage(messagePartI, messagePartII);
  switch (message[0]) {
    case 'T':
      if (messagePartI.equals("N")) {
        activeHeater(messagePartII.toFloat());
      } else if (messagePartI.equals("F")) {
        deactiveHeater();
      }
      break;
    case 'M':
      int rings = messagePartI.toInt();
      activeRings(rings);
      int nodes = messagePartII.toInt();
      writeNodes(nodes);
      break;
    default:
      Serial.println("Wrong message");
  }
  message = "";
}

unsigned int calculateChecksum(const String &data) {
  unsigned int checksum = 0;
  for (int i = 0; i < data.length(); i ++) {
    checksum += data[i];
  }
  return checksum & 0xFF;
}

unsigned int extractChecksum(const String &receivedData) {
  String checksumHex = receivedData.substring(0, 2);
  unsigned int receivedChecksum = (unsigned int) strtol(checksumHex.c_str(), NULL, 16);
  return receivedChecksum;
}

String extractData(const String &receivedData) {
  return receivedData.substring(2);
}

void sendEverySec(String msg) {
  unsigned long curMillis = millis();
  if (curMillis - messageFlag >= 1000) {
    Serial.println("Temperature:" + msg);
    String tmp = "T" + msg;
    BTSerial.print(tmp);
    messageFlag = curMillis;
  }
}

void sliptMessage(String &partI, String &partII) {
  int firstSlash = message.indexOf('/');
  int secondSlash = message.indexOf('/', firstSlash + 1);

  partI = message.substring(firstSlash + 1, secondSlash);
  partII = message.substring(secondSlash + 1);
}

void activeRings(int rings) {
  if (rings & 0b001) {
    ringI.on();
  } else {
    ringI.off();
  }
}

void writeNodes(int nodes) {
  Serial.print("Write node: ");
  ringI.setNodes(nodes);
  Serial.println(ringI.getNodes());
}

void activeHeater(float target) {
  heater.on();
  heater.setTarget(target);
}

void deactiveHeater() {
  heater.off();
}

void clearBTSerialBuffer() {
    while (BTSerial.available()) {
        BTSerial.read();
    }
}
