#define RREF      430.0  // Reference Resistor for MAX31865
#define RNOMINAL  100.0  // Nominal Resistance of the PT100


void set_temp(int addition) {
  temperature = DEFAULT_TEMPERATURE + addition;
}

void heat() {
  int curTemperature = max.temperature(RNOMINAL, RREF);
  Serial.print("Temperature:");
  Serial.println(curTemperature);

  if(curTemperature < temperature - 0.5) {
    digitalWrite(heatPin, HIGH);
  } else if(curTemperature > temperature + 0.5) {
    digitalWrite(heatPin, LOW);
  }
}

void stop_heat() {
  digitalWrite(heatPin, LOW);
}
