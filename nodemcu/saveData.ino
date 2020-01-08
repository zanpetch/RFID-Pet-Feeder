void saveData(String petKey, String eatType, String eatWeight){ 
  if (eatType == "empty"){
    eatType = "empty%20food";
  }else if (eatType == "dif"){
    eatType = "different%20pet";
  }else{
    eatType = "eat";
  }
  HTTPClient http;
  http.begin("http://192.168.1.35/feeder/addhistory.php?petKey="+petKey+"&type="+eatType+"&eatWeight="+eatWeight);
  http.setTimeout(10000);
  int httpCode = http.GET();
  Serial.print("httpCode : ");
  Serial.println(httpCode);
  if (httpCode > 0){
    Serial.println("add history complete");
  }
  http.end();
}
