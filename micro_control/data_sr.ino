#define MAX_TOKENS 4

void btRecv() {
  if (BTSerial.available()) {
    String recData = BTSerial.readStringUntil('\n');
    recData.trim();

    Serial.print("Received: ");
    Serial.println(recData);

    String message = extractData(recData);
    if (calculateChecksum(message) != extractChecksum(recData)) {
      return;
    }

    BTSerial.write("ACK");

    String type = message.substring(0, 1);
    String numStr = message.substring(2);
    int number = numStr.toInt();
    switch (type[0]) {
      case 'T':
        Serial.print("Temeprature: ");
        Serial.println(message[2]);
        break;
      case 'V':
        Serial.print("Vibration: ");
        Serial.println(number);
        set_strength(number);
        break;
      case 'M':
        Serial.print("Mode: ");
        Serial.println(number);
        markPoint = millis();
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