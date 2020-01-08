void checkFeed(String tagFromArduino) {
  HTTPClient http;
  http.begin("http://192.168.1.35/feeder/getpetdatabase.php?tagID=" + tagFromArduino);
  http.setTimeout(10000);
  int httpCode = http.GET();
  Serial.print("httpCode : ");
  Serial.println(httpCode);
  if (httpCode > 0) {
    const size_t bufferSize = JSON_OBJECT_SIZE(2) + JSON_OBJECT_SIZE(3) + JSON_OBJECT_SIZE(5) + JSON_OBJECT_SIZE(8) + 370;
    DynamicJsonDocument jsonBuffer(bufferSize);
    deserializeJson(jsonBuffer, http.getString());
    const char* dataKey = jsonBuffer["dataKey"];
    const char* message = jsonBuffer["message"];
    petKey = dataKey;
    serverMessage = message;
    Serial.print("petKey : ");
    Serial.println(petKey);
    Serial.print("serverMessage : ");
    Serial.println(serverMessage);
    if (serverMessage == "denied"){
      nodeSerial.print("-----"); //denied
    }else if(serverMessage == "limit"){
      nodeSerial.print("----"); //limit
    }else{
      nodeSerial.print("---"); //eat
    }
  }else{
    nodeSerial.print("------"); //server
  }
  http.end();
}
