int strength = 0;

void init_vib(int strength_int) {
  strength = strength_int;
}

void set_strength(int strength_int) {
  strength = strength_int;
}

void vibration() {
  float base = ICR1 / 10;
  OCR1A = (int)(base * strength);
}

void stop_vibration() {
  OCR1A = 0;
}

