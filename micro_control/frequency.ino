void init_frq(int frequency) {
  TCCR1A = 0;
  TCCR1B = 0;
  TCNT1 = 0;

  // Set WGM mode as 14
  TCCR1A |= (1 << WGM11);
  TCCR1B |= (1 << WGM13) | (1 << WGM12);

  // Clear OC1A/OC1B on Compare Match
  TCCR1A |= (1 << COM1A1) | (1 << COM1B1);

  // Prescaler: 1024
  TCCR1B |= (1 << CS12) | (1 << CS10);

  // TOP = fclock / (prescaler * frq) - 1
  set_freq(frequency);
  OCR1A = 0;  // PIN9-Vib
  OCR1B = 0;  // PIN10-Heat
}

void set_freq(int frequency) {
  float tmp = 16000000.0 / 1024.0;
  ICR1 = tmp / frequency - 1;
}