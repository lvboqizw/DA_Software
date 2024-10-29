#define MAX_TOKENS 4

String message_tmp = "M/012/45";
String type;
int rings;
int nodes;

void btRecv() {
  if (BTSerial.available()) {
    String recData = BTSerial.readStringUntil('\n');
    recData.trim();

    String message = extractData(recData);
    if (calculateChecksum(message) != extractChecksum(recData)) {
      BTSerial.read();
      return;
    }
    
    Serial.println("Message: " + message);
    BTSerial.write("ACK");

    type = message[0];
    Serial.println("Type: " + type);
    type.trim();
    switch (type[0]) {
      case 'M':
        sliptMessage(message);
        Serial.print("ring: ");
        Serial.println(rings);
        Serial.print("Nodes: ");
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
        set_strength(number);
        break;
      default:
        Serial.println("Wrong message");
    }
  }
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

void sliptMessage(const String &message) {
  int firstSlash = message.indexOf('/');
  int secondSlash = message.indexOf('/', firstSlash + 1);

  rings = message.substring(firstSlash + 1, secondSlash).toInt();
  nodes = message.substring(secondSlash + 1).toInt();
}