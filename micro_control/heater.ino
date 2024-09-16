#define RREF      430.0  // Reference Resistor for MAX31865
#define RNOMINAL  100.0  // Nominal Resistance of the PT100

Adafruit_MAX31865 max = Adafruit_MAX31865(10);

double Setpoint, Input, Output;
double Kp = 2.0, Ki = 5.0, Kd = 1.0;

PID pid(&Input, &Output, &Setpoint, Kp, Ki, Kd, DIRECT);

void setHeater(int temperature) {
  Setpoint = temperature;
  max.begin(MAX31865_2WIRE);

  pid.SetMode(AUTOMATIC);
  pid.SetOutputLimits(0, 255);
}

void pidHeat(int heatPin, bool heat) {
  if (heat) {
    Input = max.temperature(RNOMINAL, RREF);
    // Serial.print("Temperature: ");
    Serial.println(Input);

    pid.Compute();
    analogWrite(heatPin, (int)Output);

    delay(1000);
  } else {
    analogWrite(heatPin, 0);
  }

}
