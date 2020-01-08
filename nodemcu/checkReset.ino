void checkReset() {
  if (arduinoMessage == "reset") {
    Serial.println("arduinoMessage == reset");
    state = "normal";
    androidMessage = "";
    arduinoMessage = "";
    serverMessage = "";
    petKey = "";
    eatType = "eat";
  }
}
