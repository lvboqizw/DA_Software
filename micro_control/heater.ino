#define RREF      430.0  // Reference Resistor for MAX31865
#define RNOMINAL  100.0  // Nominal Resistance of the PT100

double Setpoint = 40, Input, Output;
double Kp = 5.0, Ki = 5.0, Kd = 3.0;

PID pid(&Input, &Output, &Setpoint, Kp, Ki, Kd, DIRECT);

void init_heat(int temperature) {
  max.begin(MAX31865_2WIRE);
  //pid.SetMode(AUTOMATIC);
  set_temp(temperature);
  //pid.SetOutputLimits(0, ICR1);
}

void set_temp(int temperature) {
  Setpoint = temperature;
}

void heat() {
  Input = max.temperature(RNOMINAL, RREF);
  Serial.print("Temperature:");
  Serial.println(Input);

  if(Input < Setpoint - 0.5) {
    digitalWrite(heatPin, HIGH);
  } else if(Input > Setpoint + 0.5) {
    digitalWrite(heatPin, LOW);
  }
}

void stop_heat() {
  digitalWrite(heatPin, LOW);
}
