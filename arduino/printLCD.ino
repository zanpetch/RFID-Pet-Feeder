void printLCD(int row,char* message){
  int length_message = strlen(message);
  lcd.setCursor(((16-length_message)/2),row);
  lcd.print(message);
}
