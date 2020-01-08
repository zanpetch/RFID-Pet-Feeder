void receiveFromArduino(){
  arduinoMessage = "";
  while (nodeSerial.available() > 0){
    arduinoMessage = nodeSerial.readString();
  }
}
