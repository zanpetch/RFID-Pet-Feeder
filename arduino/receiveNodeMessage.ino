void receiveNodeMessage(){
  countNodeMessage = 0;
  while (arduinoSerial.available() > 0){
    countNodeMessage = arduinoSerial.readString().length();
  }
  if (countNodeMessage == 1){
    Serial.println("nodeMessage = add");
    nodeMessage = "add";
  }else if (countNodeMessage == 2){
    Serial.println("nodeMessage = cancel");
    nodeMessage = "cancel";
  }else if (countNodeMessage == 3){
    Serial.println("nodeMessage = eat");
    nodeMessage = "eat";
  }else if (countNodeMessage == 4){
    Serial.println("nodeMessage = limit");
    nodeMessage = "limit";
  }else if (countNodeMessage == 5){
    Serial.println("nodeMessage = denied");
    nodeMessage = "denied";
  }else if (countNodeMessage == 6){
    Serial.println("nodeMessage = server");
    nodeMessage = "server";
  }else if (countNodeMessage == 7){
    Serial.println("nodeMessage = complete");
    nodeMessage = "complete";
  }else{
    nodeMessage = "";
  }
}
