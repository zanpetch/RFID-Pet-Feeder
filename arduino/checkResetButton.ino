void checkResetButton() {
  int val = 0;
  val = analogRead(keypadPin);
  if (val >= 700 && val <= 800) {
    lcd.clear();    
    printLCD(0,"Restart!");
    printLCD(1,"................");

    digitalWrite(ledGateOpen, LOW);
    digitalWrite(ledGateClose, HIGH);
    myservo.attach(servoPin);
    myservo.write(180);
    delay(1000);
    myservo.detach();
    state = "normal";
    countNodeMessage = 0;
    countReceiveCheckFeed = 0;
    nodeMessage = "";
    servoOn = false;
    countServoOn = 0;
    eatWeight = 0;
    countGateOpen = 0;
    checkDifferent = false;
    loading = 0;
    arduinoSerial.print("reset");
    Serial.println("reset");
    lcd.clear();
  }
}
