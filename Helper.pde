void outputRigglerCount( ArrayList<Entity> arEl ) {
  int nOR = arEl.size();
  int nOH = 0;
  int nOAtD = 0;

  for( Entity ent: arEl ) {
    if( ent.isHungry() ) nOH ++;
    if( ent.isAboutToDie() ) nOAtD ++;
  }
  String sT = "Rigglers: " + Integer.toString( nOR ) + ", Hungry: " + Integer.toString( nOH ) + ", About to Die: " + Integer.toString( nOAtD ) + ", Food: " + Integer.toString( fooSys.size() );
  stroke( 255 );
  numberOfRigglers.setText(sT);
}

boolean isNumeric( String str ) {
  try{
    int in = Integer.parseInt( str );
  }
  catch( NumberFormatException nfe ) {
    return false;
  }
  
  return true;
}

void addMeFood( int hMF ) {
  for( int i = 0; i < hMF; i ++ ) {
    fooSys.addOneFood();
  }  
}

void riggleMySperm( int rIS ) {
  int nOSP = en.size();
  int nOES = maxSpermCount - nOSP;
  int nOSTBA;
  
  if( rIS < nOES ) nOSTBA = rIS;
  else nOSTBA = nOES;
  
  for( int i = 0; i < nOSTBA; i++ ) {
    addSperm();
  }
}