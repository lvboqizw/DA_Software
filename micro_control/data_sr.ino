
String message_tmp = "M/012/45";
String type;
int rings;
int nodes;
String message = "";

void btRecv() {
  if (BTSerial.available()) {
    String recData = BTSerial.readStringUntil('\n');
    recData.trim();

    String recMessage = extractData(recData);
    if (calculateChecksum(recMessage) != extractChecksum(recData)) {
      Serial.println("Uncorrect message");
      BTSerial.write("R");
      return;
    }
    
    message = recMessage;
    Serial.println("Message: " + message);
    BTSerial.write("A");
  }

  if (message.length() != 0) {
    trigger();
    delay(1000);
  }
}

void trigger() {
  type = message[0];
  Serial.println("Type: " + type);
  type.trim();
  switch (type[0]) {
    case 'M':
      sliptMessage();
      Serial.print("ring: ");
      Serial.print(rings);
      Serial.print(" Nodes: ");
      Serial.println(nodes);
      markPoint = millis();
      break;
    case 'T':
      Serial.print("Temeprature: ");
      Serial.println(message[2]);
      break;
    case 'V':
      String numStr = message.substring(2);
      int number = numStr.toInt();
      Serial.print("Vibration: ");
      Serial.println(number);
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

void sliptMessage() {
  int firstSlash = message.indexOf('/');
  int secondSlash = message.indexOf('/', firstSlash + 1);

  rings = message.substring(firstSlash + 1, secondSlash).toInt();
  nodes = message.substring(secondSlash + 1).toInt();
}