class FoodSystem {
  ArrayList<Food> fS;
  
  int maxFood;
  int genRate;
  float rangeRadius;
  
  float foodSize;
  color shade;

  FoodSystem( int maxFood, int genRate, float rangeRadius, float foodSize, color shade, int xBound, int yBound ) {
    this.maxFood = maxFood;
    this.genRate = genRate;
    this.rangeRadius = rangeRadius;
    this.foodSize = foodSize;
    this.shade = shade;
    
    fS = new ArrayList<Food>();
  }
  
  void display() {
    for( Food foo: fS ) {
      foo.display();
    }  
  }
  
  int size() {
    return fS.size();  
  }
  
  Food nearestFood( PVector something ) {
    Food theFood = new Food();
    float dist = width*3;
  
    for( Food f: fS ) {
      if( PVector.dist( something, f.foodLocation ) < dist ) {
        dist = PVector.dist( something, f.foodLocation );
        theFood = f;
      }
    }
    
    return theFood;
  }
  
  void clear() {
    fS.clear();  
  }
  
  void remove(Food f) {
    fS.remove( f );    
  }
  
  boolean hasFood() {
    if( fS.size() > 0 ) return true;
    else return false;  
  }
  
  void addOneFood() {
    float deg = random( TWO_PI );
    float rad = random( rangeRadius );
    
    PVector loc = new PVector( rad * cos( deg ), rad * sin( deg ) );
    loc.add( new PVector( xBound/2, yBound/2 ) );
    
    if( fS.size() < maxFood ) {
      fS.add( new Food( loc, foodSize, shade ) );
    }
  }
  
  void generate() {
    float deg = random( TWO_PI );
    float rad = random( rangeRadius );
    
    PVector loc = new PVector( rad * cos( deg ), rad * sin( deg ) );
    loc.add( new PVector( width/2, height/2 ) );
    
    int timeStep = 60/genRate;
    int inis = frameCount;
    if( fS.size() < maxFood ) {
      fS.add( new Food( loc, foodSize, shade ) );
      print( inis + " " + (inis/timeStep) + " " + (inis%timeStep) );
      println();
    }
  }
}