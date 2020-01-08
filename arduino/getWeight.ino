float getWeight(){
  float weight = 0;
  weight = scale.get_units(5);
  if ( weight > 0 ){
    return weight*1000;
  }
  return 0;
}
