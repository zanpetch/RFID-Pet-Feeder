void setState(){
  if (arduinoMessage == "normal"){
    state = "normal";
  }else if (arduinoMessage == "add"){
    state = "add";
  }else if (state == "normal" && arduinoMessage != "normal" && arduinoMessage != "add" && arduinoMessage != ""){
    state = "checkFeed";
  }else if (arduinoMessage == "feed"){
    state = "feed";
  }
}
