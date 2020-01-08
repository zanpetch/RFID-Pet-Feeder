#include <SoftwareSerial.h>
#include <SPI.h>
#include <MicroGear.h>
#include <ESP8266HTTPClient.h>
#include <ArduinoJson.h>

#define APPID   "PetFeederIOT"
#define KEY     "nTYiK7XTAbM3ING"
#define SECRET  "Z9uiVWwaDtAM8oWC6FEPqvy7L"
#define ALIAS   "NodeMcu"

#include <ESP8266WiFi.h>
const char* ssid     = "Homewifi_NK2";
const char* password = "012345nk";

SoftwareSerial nodeSerial(D2, D3); //D2 D3
WiFiClient client;
MicroGear microgear(client);

String state = "normal";
String androidMessage = "";
String arduinoMessage = "";
String serverMessage = "";
String petKey = "";
String eatType = "eat";

void setup() {
  Serial.begin(9600);
  nodeSerial.begin(115200);

  microgear.on(MESSAGE,onMsghandler);
  microgear.on(CONNECTED,onConnected);

  WiFi.begin(ssid, password);
  while (WiFi.status() != WL_CONNECTED){
    delay(250);
    Serial.print(".");
  }
  Serial.println("WiFi connected");
  Serial.println("IP address: ");
  Serial.println(WiFi.localIP());

  microgear.init(KEY,SECRET,ALIAS);
  microgear.connect(APPID);
}

void loop() {
  if (microgear.connected()){
    microgear.loop();
    //Serial.println("Connecting to NETPIE...");
    receiveFromArduino();
    checkReset();
    setState();
    Serial.print("state = ");
    Serial.println(state);
    if (state == "add"){
      if (arduinoMessage != "add" && arduinoMessage != ""){
        microgear.publish("/NodemcuSendTag",arduinoMessage);
      }
    }else if (state == "checkFeed"){
      if (arduinoMessage != "checkfeed" && arduinoMessage != ""){
        checkFeed(arduinoMessage);
      }
    }else if (state == "feed"){
      if (arduinoMessage != "feed" && arduinoMessage != ""){
        if (arduinoMessage == "empty"){
          saveData(petKey,"empty","0");
          petKey = "";
          state = "normal";
        }else if (arduinoMessage == "dif"){
          Serial.println("arduinoMessage == dif");
          eatType = "dif";
        }else{
          Serial.print("petKey : ");
          Serial.println(petKey);
          Serial.print("eatType : ");
          Serial.println(eatType);
          if (arduinoMessage.length() <= 2){
            saveData(petKey,eatType,arduinoMessage);
          }
          petKey = "";
          eatType = "eat";
          state = "normal";
        }
      }
    }
  }else{
    Serial.println("connection lost, reconnect...");
    microgear.connect(APPID);
    state = "normal";
    androidMessage = "";
    arduinoMessage = "";
    serverMessage = "";
    petKey = "";
    eatType = "eat";
  }
//  delay(250);
}
