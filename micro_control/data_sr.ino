#define MAX_TOKENS 4

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
      set_freq(frequency);
      set_temp(temperature);
    } else if (tokens[0] == "H") {
      ifHeat = !ifHeat;
    } else if (tokens[0] == "V") {
      ifVib = !ifVib;
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