String getID() {
  String id = "";
  byte readCard[4];
  if (! mfrc522.PICC_IsNewCardPresent()) {
    return id;
  }
  if (! mfrc522.PICC_ReadCardSerial()) {
    return id;
  }
  for ( uint8_t i = 0; i < 4; i++) {
    readCard[i] = mfrc522.uid.uidByte[i];
    id.concat(String(mfrc522.uid.uidByte[i], HEX));
  }
  id.toUpperCase();
  mfrc522.PICC_HaltA();
  return id;
}
