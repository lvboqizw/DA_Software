float strength = 0;

void init_vib(int strength_int) {
  set_strength(strength_int);
}

void set_strength(int strength_int) {
  strength = (float)strength_int / 100;
}

void vibration() {
  OCR1A = ICR1 * strength;
}

void stop_vibration() {
  OCR1A = 0;
}

