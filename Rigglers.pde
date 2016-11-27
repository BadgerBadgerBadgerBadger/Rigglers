import g4p_controls.*;

import java.util.*;

ArrayList<Entity> en;
FoodSystem fooSys;

int rateOfFood;
int maxFood;
float foodRange;
float foodSize;
color foodShade;

boolean centerEmitter;
float radiusFromEmitter;

int xBound;
int yBound;

float restitution;
float mass;

float senseTypeProbability;
float geneticHit;

float birthSpeed;
float speedTypeSpeed;
float birthForce;

float birthSenseRange;
float senseTypeRange;

float birthSize;
color birthColor;
color strokeColor;

float femaleProbability;

int lifeSpan;

int maxSpermCount;
float separationCoeff;

void setup() {
  size( 700, 500 );
  background( 0 );
  frameRate( 30 );
  createGUI();
  
  en = new ArrayList<Entity>();
  
  centerEmitter = true;
  radiusFromEmitter = 100;
  
  xBound = width - 150;
  yBound = height;
  
  restitution = 0.7;
  mass = 0;
  
  senseTypeProbability = 0.5;
  geneticHit = 0.05;
  
  birthSpeed = 2;
  speedTypeSpeed = 5;
  birthForce = PI/180 * 1.5;
  
  birthSenseRange = 40;
  senseTypeRange = 100;
  
  strokeColor = 0;
  birthSize = 11;
  
  femaleProbability = 0.33;
  
  lifeSpan = 1800;
   
  maxSpermCount = 100;
  separationCoeff = PI/180 * 2;
  
  rateOfFood = 5;
  maxFood = 50;
  foodRange = height/2;
  foodSize = 5;
  foodShade = color( 0, 255, 0 );
  
  fooSys = new FoodSystem( maxFood, rateOfFood, foodRange, foodSize, foodShade, xBound, yBound );
}

void draw() {
  background( 0 );
  //fill( 0, 49 );
  //rect( 0, 0, width, height );
  outputRigglerCount(en);
  fooSys.display();
  
  Iterator edI = en.iterator();
  
  while( edI.hasNext() ) {
    Entity ens = (Entity) edI.next();
    
    if( ens.isDead() ) {
      edI.remove(); 
    }
    
    if( ens.isHungry() ) {
      steerToFood( ens );
      eatFood( ens );
    }
        
    //ens.steer( new PVector( mouseX, mouseY ) );
    ens.separate( en, separationCoeff );
    //ens.flock( en, 9 );
    ens.update();
    ens.display();
    //ens.wander( 60, 30, PI/2 );
    ens.dieOff();
    
    if( mouseX > ens.location.x - ens.size * 0.3 && mouseX < ens.location.x + ens.size * 0.3 && mouseY > ens.location.y - ens.size * 0.3 && mouseY < ens.location.y + ens.size * 0.3 ) {
      //ens.displayRange();  
    }
  }
    
}

void eatFood( Entity thEn ) {
  if( fooSys.hasFood() ) {
    Food theFood = fooSys.nearestFood( thEn.location );
    if( PVector.dist( theFood.foodLocation, thEn.location ) < thEn.size ) {
      fooSys.remove( theFood );
      thEn.fed();  
    }
  }  
}

void steerToFood( Entity thEn ) {
  if( fooSys.hasFood() ){
    Food toFood = fooSys.nearestFood( thEn.location );
    thEn.steer( toFood.foodLocation );    
  }
}

void addSperm() {
  PVector location;
  float bSR;
  float bS;
  
  if( centerEmitter ) {
    float rotation = random( 0, TWO_PI );
    float x = width/2 + random(radiusFromEmitter) * cos( rotation );
    float y = height/2 + random(radiusFromEmitter) * sin( rotation );
    location = new PVector( x, y );     
  }
  else {
    int x = (int) ( Math.floor( (int) random( 2 ) ) * width + radiusFromEmitter );
    int y = (int) ( Math.floor( (int) random( 2 ) ) * height + radiusFromEmitter );
    location = new PVector( x, y );
  }
  
  birthColor = color( random( 255 ), random( 255 ), random( 255 ) );
 
  if( random( 1 ) < senseTypeProbability ) {
    bSR = senseTypeRange;// * random( 1 - geneticHit );
    bS = birthSpeed;
  }
  else {
    bSR = birthSenseRange;// * random( 1 - geneticHit );
    bS = speedTypeSpeed;  
  }
  
  float bSZ = map( bS * bSR, 0, speedTypeSpeed * senseTypeRange, 0, birthSize );
  
  boolean gender;
  
  if( random( 1 ) < femaleProbability ) gender = true;
  else gender = false; 
    
  
  Entity ent = new Entity( location, birthSize, birthColor, strokeColor, mass, restitution, bS, birthForce, bSR, gender, lifeSpan, xBound, yBound );
  en.add( ent );
}

void mouseClicked() {
  //addSperm();  
}