/* 
 * RFID Pet Feeder by Sanpetch Nakhwan
 * 
 * Arduino Serial to NodeMCU 2->D2 3->D3 VIN GND
 * 
 * MFRC522        *HX711     *SERVO    *LCD       *BUTTON
 * VCC : 3.3      *VCC : 5   *VCC : 5  *VCC : 5   *VCC : 5
 * RST Pin : 9    *DOUT : 4  *out : 6  *SDA : A4  *OUT : A0
 * SDA Pin : 10   *CLK : 5             *SCL : A5
 * MOSI Pin : 11
 * MISO Pin : 12
 * SCK Pin : 13
 * 
 */
 
#include <SoftwareSerial.h>
#include <SPI.h>
#include <MFRC522.h>
#include <HX711.h>
#include <Servo.h>
#include <Wire.h>
#include <LCD.h>
#include <LiquidCrystal_I2C.h>

#define RST_PIN 9
#define SDA_PIN 10
#define servoPin 6
#define keypadPin A0
#define ledGateClose 7
#define ledGateOpen 8
#define DOUT 4        
#define CLK  5   
#define calibration_factor 511700   
#define zero_factor 147300
#define I2C_ADDR 0x27 
#define BACKLIGHT_PIN 3

SoftwareSerial arduinoSerial(3, 2);
MFRC522 mfrc522(SDA_PIN, RST_PIN);
LiquidCrystal_I2C lcd(I2C_ADDR,2,1,0,4,5,6,7);
HX711 scale(DOUT, CLK);
Servo myservo;

String state = "normal";
String tagID = "";
int countNodeMessage = 0;
int countReceiveCheckFeed = 0;
String nodeMessage = "";
int realWeight = 0;
boolean servoOn = false;
int countServoOn = 0;
int nowWeight = 0;
int eatWeight = 0;
String nowTag = "";
int countGateOpen = 0;
boolean checkDifferent = false;
int loading = 0;

void setup() {
  Serial.begin(9600);
  arduinoSerial.begin(115200);
  SPI.begin();
  mfrc522.PCD_Init();
  pinMode(ledGateClose, OUTPUT);
  pinMode(ledGateOpen, OUTPUT);
  digitalWrite(ledGateClose, HIGH);
  pinMode(keypadPin, INPUT_PULLUP);
  lcd.begin (16,2);
  lcd.setBacklightPin(BACKLIGHT_PIN,POSITIVE);
  lcd.setBacklight(HIGH);
  lcd.home ();
  myservo.attach(servoPin);
  myservo.write(180);
  delay(1000);
  myservo.detach();
  scale.set_scale(calibration_factor);
  scale.set_offset(zero_factor);
  scale.tare();
}


void loop() {
  receiveNodeMessage();
  checkResetButton();
  
  if (state == "normal"){
    printLCD(0,"RFID");
    printLCD(1,"PET FEEDER");
    if (nodeMessage == "add"){
      arduinoSerial.print("add");
      state = "add tag";
      lcd.clear();
    }

    tagID = getID();
    if (tagID != ""){
      arduinoSerial.print(tagID);
      Serial.print("tagID = ");
      Serial.println(tagID);
      state = "checkFeed";
      lcd.clear();
    }
  }else if(state == "add tag"){
    printLCD(0,"Add Pet Mode");
    printLCD(1,"Waiting tag scan");
    if (nodeMessage == "cancel"){
      arduinoSerial.print("normal");
      state = "normal";
      lcd.clear();
      printLCD(0,"Add Pet Mode");
      printLCD(1,"cancel!");
      delay(1000);
      lcd.clear();
    }
    if (nodeMessage == "complete"){
      arduinoSerial.print("normal");
      state = "normal";
      lcd.clear();
      printLCD(0,"Add Pet Mode");
      printLCD(1,"completed");
      delay(1000);
      lcd.clear();
    }
    tagID = getID();
    if (tagID != ""){
      arduinoSerial.print(tagID);
      Serial.print("tagID : ");
      Serial.println(tagID);
    }
  }else if(state == "checkFeed"){
    printLCD(0,"Feed Mode");
    if (loading != 16){
      lcd.setCursor(loading,1);
      lcd.print("-");
      loading += 1;
    }else{
      lcd.clear();
      loading = 0;
    }
    
    if (countReceiveCheckFeed == 49){
      loading = 0;
      state = "normal";
      countReceiveCheckFeed = 0;
      lcd.clear();
      printLCD(0,"Feed Mode");
      printLCD(1,"Message Error!");
      delay(1000);
      lcd.clear();
    }
    if (nodeMessage == "denied"){
      arduinoSerial.print("normal");
      loading = 0;
      state = "normal";
      countReceiveCheckFeed = 0;
      lcd.clear();
      printLCD(0,"Feed Mode");
      printLCD(1,"Access Denied!");
      delay(1000);
      lcd.clear();
    }else if(nodeMessage == "limit"){
      arduinoSerial.print("normal");
      loading = 0;
      state = "normal";
      countReceiveCheckFeed = 0;
      lcd.clear();
      printLCD(0,"Feed Mode");
      printLCD(1,"Feed Max Limit");
      delay(1000);
      lcd.clear();
    }else if(nodeMessage == "eat"){
      arduinoSerial.print("feed");
      loading = 0;
      state = "feed";
      lcd.clear();
      printLCD(0,"Feed Mode");
      countReceiveCheckFeed = 0;
    }else if(nodeMessage == "server"){
      arduinoSerial.print("normal");
      loading = 0;
      state = "normal";
      countReceiveCheckFeed = 0;
      lcd.clear();
      printLCD(0,"Feed Mode");
      printLCD(1,"Server Error!");
      delay(500);
      lcd.clear();
    }
    countReceiveCheckFeed += 1;
  }else if(state == "feed"){
      printLCD(1,"Food is coming");
      realWeight = (int) getWeight();
      Serial.print("realWeight = ");
      Serial.println(realWeight);
      if (realWeight < 20 && countServoOn != 10){
        if (servoOn == false){
          servoOn = true;
          myservo.attach(servoPin);
          myservo.write(30);
          delay(500);
          myservo.detach();
        }
        countServoOn += 1;
      }else if(realWeight < 20 && countServoOn == 10){
        countServoOn = 0;
        servoOn = false;
        myservo.attach(servoPin);
        myservo.write(180);
        delay(1000);
        myservo.detach();
        lcd.clear();
        printLCD(0,"Feed Mode");
        printLCD(1,"Empty Food");
        delay(1000);
        lcd.clear();
        arduinoSerial.print("empty");
        state = "normal";
      }else{
        countServoOn = 0;
        servoOn = false;
        myservo.attach(servoPin);
        myservo.write(180);
        delay(1000);
        myservo.detach();
        lcd.clear();
        state = "gate open";
      }
  }else if (state == "gate open"){
    digitalWrite(ledGateOpen, HIGH);
    digitalWrite(ledGateClose, LOW);
    if (checkDifferent != true){
      printLCD(0,"Feed Mode");
      printLCD(1,"Gate Open");
    }
    nowWeight = (int) getWeight();
    if (realWeight-eatWeight > 1 && countGateOpen != 20 && checkDifferent == false){
      Serial.print("countGateOpen = ");
      Serial.println(countGateOpen);
      if (nowWeight <= realWeight && nowWeight >= 0){
        eatWeight = realWeight - nowWeight;
        Serial.print(" eatWeight = ");
        Serial.println(eatWeight);
      }
      
      nowTag = getID();
      if (nowTag == tagID){
        Serial.println("same pet scan");
        countGateOpen = 0;
      }else if(nowTag != tagID && nowTag != ""){
        Serial.println("different pet scan");
        arduinoSerial.print("dif");
        checkDifferent = true;
        lcd.clear();
        printLCD(0,"Feed Mode");
        printLCD(1,"Different Pet");
        delay(1000);
      }
      countGateOpen += 1;
    }else{
      digitalWrite(ledGateOpen, LOW);
      digitalWrite(ledGateClose, HIGH);
      arduinoSerial.print(eatWeight);
      Serial.println("loop eat");
      lcd.clear();
      printLCD(0,"Feed Mode");
      printLCD(1,"Gate Close");
      delay(1000);
      lcd.clear();
      state = "normal";
      countGateOpen = 0;
      eatWeight = 0;
      checkDifferent = false;
    }
  }
  delay(250);
}
