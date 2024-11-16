String type;
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
    // message = "";
    trigger();
  }
}

void trigger() {
  type = message[0];
  type.trim();
  switch (type[0]) {
    case 'M':
      sliptMessage();
      break;
    case 'T':
      Serial.print("SetTemeprature: ");
      Serial.println(message[2]);
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

void sliptMessage() {
  int firstSlash = message.indexOf('/');
  int secondSlash = message.indexOf('/', firstSlash + 1);

  int rings = message.substring(firstSlash + 1, secondSlash).toInt();
  activeRings(rings);
  int nodes = message.substring(secondSlash + 1).toInt();
  writeNodes(nodes);
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

void clearBTSerialBuffer() {
    while (BTSerial.available()) {
        BTSerial.read();
    }
}
