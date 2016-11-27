class Food {
  PVector foodLocation;
  float size;
  color shade;
  
  Food(){
  }
 
  Food( PVector foodLocation, float size, color shade ){
    this.foodLocation = foodLocation.get();
    this.size = size;
    this.shade = shade; 
  }
  
  void display() {
    pushMatrix();
      translate( foodLocation.x, foodLocation.y );
      rectMode( CENTER );
      noStroke();
      fill( shade );
      rect( 0, 0, size, size );
   popMatrix();  
  }
}