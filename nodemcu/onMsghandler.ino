void onMsghandler(char *topic, uint8_t* msg, unsigned int msglen) 
{
  Serial.print("Android message --> ");
  char strState[msglen];
  for (int i = 0; i < msglen; i++) {
    strState[i] = (char)msg[i];
  }
  androidMessage = String(strState).substring(0, msglen);
  Serial.println(androidMessage);
  
  if (state == "normal" || state == "add"){
    if (androidMessage == "add"){
      nodeSerial.print("-"); //add
    }
    if (androidMessage == "cancel"){
      nodeSerial.print("--"); //cancel
    }
    if (androidMessage == "complete"){
      nodeSerial.print("-------"); //complete
    }
  }
}
