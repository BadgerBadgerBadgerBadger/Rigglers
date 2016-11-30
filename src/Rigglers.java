import g4p_controls.*;
import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringJoiner;

public class Rigglers extends PApplet {

    private ArrayList<Entity> en;
    private FoodSystem fooSys;

    private int rateOfFood;
    private int maxFood;
    private float foodRange;
    private float foodSize;
    private int foodShade;

    private boolean centerEmitter;
    private float radiusFromEmitter;

    private int xBound;
    private int yBound;

    private float restitution;
    private float mass;

    private float senseTypeProbability;
    private float geneticHit;

    private float birthSpeed;
    private float speedTypeSpeed;
    private float birthForce;

    private float birthSenseRange;
    private float senseTypeRange;

    private float birthSize;
    private int birthColor;
    private int strokeColor;

    private float femaleProbability;

    private int lifeSpan;

    private int maxSpermCount;
    private float separationCoeff;

    public void settings() {

        fullScreen();
    }

    public void setup() {

        background(0);
        createGUI();

        en = new ArrayList<Entity>();

        centerEmitter = true;
        radiusFromEmitter = 100;

        xBound = width - 150;
        yBound = height;

        restitution = 0.7f;
        mass = 0;

        senseTypeProbability = 0.5f;
        geneticHit = 0.05f;

        birthSpeed = 2;
        speedTypeSpeed = 5;
        birthForce = PI / 180 * 1.5f;

        birthSenseRange = 40;
        senseTypeRange = 100;

        strokeColor = 0;
        birthSize = 11;

        femaleProbability = 0.33f;

        lifeSpan = 1800;

        maxSpermCount = 100;
        separationCoeff = PI / 180 * 2;

        rateOfFood = 5;
        maxFood = 50;
        foodRange = height / 2;
        foodSize = 5;
        foodShade = color(0, 255, 0);

        fooSys = new FoodSystem(this, maxFood, rateOfFood, foodRange, foodSize, foodShade, xBound, yBound);
    }

    public void draw() {
        background(0);
        //fill( 0, 49 );
        //rect( 0, 0, width, height );
        outputRigglerCount(en);
        fooSys.display();

        Iterator edI = en.iterator();

        while (edI.hasNext()) {
            Entity ens = (Entity) edI.next();

            if (ens.isDead()) {
                edI.remove();
            }

            if (ens.isHungry()) {
                steerToFood(ens);
                eatFood(ens);
            }

            //ens.steer( new PVector( mouseX, mouseY ) );
            ens.separate(en, separationCoeff);
            //ens.flock( en, 9 );
            ens.update();
            ens.display();
            //ens.wander( 60, 30, PI/2 );
            ens.dieOff();

            if (mouseX > ens.getLocation().x - ens.getSize() * 0.3 && mouseX < ens.getLocation().x + ens.getSize() * 0.3 && mouseY > ens.getLocation().y - ens.getSize() * 0.3 && mouseY < ens.getLocation().y + ens.getSize() * 0.3) {
                //ens.displayRange();  
            }
        }

    }

    void eatFood(Entity thEn) {
        if (fooSys.hasFood()) {
            Food theFood = fooSys.nearestFood(thEn.getLocation());
            if (PVector.dist(theFood.foodLocation, thEn.getLocation()) < thEn.getSize()) {
                fooSys.remove(theFood);
                thEn.fed();
            }
        }
    }

    void steerToFood(Entity thEn) {
        if (fooSys.hasFood()) {
            Food toFood = fooSys.nearestFood(thEn.getLocation());
            thEn.steer(toFood.foodLocation);
        }
    }

    void addSperm() {
        PVector location;
        float bSR;
        float bS;

        if (centerEmitter) {
            float rotation = random(0, TWO_PI);
            float x = width / 2 + random(radiusFromEmitter) * cos(rotation);
            float y = height / 2 + random(radiusFromEmitter) * sin(rotation);
            location = new PVector(x, y);
        } else {
            int x = (int) (Math.floor((int) random(2)) * width + radiusFromEmitter);
            int y = (int) (Math.floor((int) random(2)) * height + radiusFromEmitter);
            location = new PVector(x, y);
        }

        birthColor = color(random(255), random(255), random(255));

        if (random(1) < senseTypeProbability) {
            bSR = senseTypeRange;// * random( 1 - geneticHit );
            bS = birthSpeed;
        } else {
            bSR = birthSenseRange;// * random( 1 - geneticHit );
            bS = speedTypeSpeed;
        }

        float bSZ = map(bS * bSR, 0, speedTypeSpeed * senseTypeRange, 0, birthSize);

        boolean gender;

        gender = random(1) < femaleProbability;

        Entity ent = new Entity(this, location, birthSize, birthColor, strokeColor, mass, restitution, bS, birthForce, bSR, gender, lifeSpan, xBound, yBound);
        en.add(ent);
    }

    public void mouseClicked() {
        //addSperm();  
    }

    void outputRigglerCount(ArrayList<Entity> arEl) {

        int nOR = arEl.size();
        int nOH = 0;
        int nOAtD = 0;

        for (Entity ent : arEl) {
            if (ent.isHungry()) nOH++;
            if (ent.isAboutToDie()) nOAtD++;
        }
        String sT = "Rigglers: " + Integer.toString(nOR) + ", Hungry: " + Integer.toString(nOH) + ", About to Die: " + Integer.toString(nOAtD) + ", Food: " + Integer.toString(fooSys.size());
        stroke(255);
        numberOfRigglers.setText(sT);
    }

    boolean isNumeric(String str) {
        try {
            int in = Integer.parseInt(str);
        } catch (NumberFormatException nfe) {
            return false;
        }

        return true;
    }

    void addMeFood(int hMF) {
        for (int i = 0; i < hMF; i++) {
            fooSys.addOneFood();
        }
    }

    void riggleMySperm(int rIS) {
        int nOSP = en.size();
        int nOES = maxSpermCount - nOSP;
        int nOSTBA;

        if (rIS < nOES) nOSTBA = rIS;
        else nOSTBA = nOES;

        for (int i = 0; i < nOSTBA; i++) {
            addSperm();
        }
    }

    public ArrayList<Entity> getEn() {
        return en;
    }

    public FoodSystem getFooSys() {
        return fooSys;
    }

    public int getRateOfFood() {
        return rateOfFood;
    }

    public int getMaxFood() {
        return maxFood;
    }

    public float getFoodRange() {
        return foodRange;
    }

    public float getFoodSize() {
        return foodSize;
    }

    public int getFoodShade() {
        return foodShade;
    }

    public boolean isCenterEmitter() {
        return centerEmitter;
    }

    public float getRadiusFromEmitter() {
        return radiusFromEmitter;
    }

    public int getxBound() {
        return xBound;
    }

    public int getyBound() {
        return yBound;
    }

    public float getRestitution() {
        return restitution;
    }

    public float getMass() {
        return mass;
    }

    public float getSenseTypeProbability() {
        return senseTypeProbability;
    }

    public float getGeneticHit() {
        return geneticHit;
    }

    public float getBirthSpeed() {
        return birthSpeed;
    }

    public float getSpeedTypeSpeed() {
        return speedTypeSpeed;
    }

    public float getBirthForce() {
        return birthForce;
    }

    public float getBirthSenseRange() {
        return birthSenseRange;
    }

    public float getSenseTypeRange() {
        return senseTypeRange;
    }

    public float getBirthSize() {
        return birthSize;
    }

    public int getBirthColor() {
        return birthColor;
    }

    public int getStrokeColor() {
        return strokeColor;
    }

    public float getFemaleProbability() {
        return femaleProbability;
    }

    public int getLifeSpan() {
        return lifeSpan;
    }

    public int getMaxSpermCount() {
        return maxSpermCount;
    }

    public float getSeparationCoeff() {
        return separationCoeff;
    }

    /* =========================================================
     * ====                   WARNING                        ===
     * =========================================================
     * The code in this tab has been generated from the GUI form
     * designer and care should be taken when editing this file.
     * Only add/edit code inside the event handlers i.e. only
     * use lines between the matching comment tags. e.g.

     void myBtnEvents(GButton button) { //_CODE_:button1:12356:
         // It is safe to enter your event code here
     } //_CODE_:button1:12356:

     * Do not rename this tab!
     * =========================================================
     */

    public void RCPClick(GPanel source, GEvent event) { //_CODE_:RigglerControlPanel:359577:
        println("panel1 - GPanel event occured " + System.currentTimeMillis() % 10000000);
    } //_CODE_:RigglerControlPanel:359577:

    public void riggleButtonClicked(GButton source, GEvent event) { //_CODE_:riggleButton:204530:
        println("button1 - GButton event occured " + System.currentTimeMillis() % 10000000);
        String inpT = riggleAmount.getText();
        inpT.trim();
        if (isNumeric(inpT)) {
            birthSpeed = maxSpeedSliderSns.getValueF();
            speedTypeSpeed = maxSpeedSliderSpd.getValueF();
            birthSize = birthSizeSlider.getValueI();
            birthForce = birthForceSlider.getValueF();
            riggleMySperm(Integer.parseInt(inpT));
            riggleTest.setText("");
            riggleAmount.setText("");
            riggleAmount.setFocus(true);
        } else {
            riggleTest.setText("Enter a bloody number!");
            riggleAmount.setText("");
            riggleTest.setTextBold();
            riggleAmount.setFocus(true);
        }
    } //_CODE_:riggleButton:204530:

    public void riggleAmountField(GTextField source, GEvent event) { //_CODE_:riggleAmount:711267:
        println("textfield1 - GTextField event occured " + System.currentTimeMillis() % 10000000);
    } //_CODE_:riggleAmount:711267:

    public void addFoodButton(GButton source, GEvent event) { //_CODE_:foodbutton:329183:
        println("foodbutton - GButton event occured " + System.currentTimeMillis() % 10000000);

        String inpT = foodField.getText();
        inpT.trim();
        if (isNumeric(inpT)) {
            addMeFood(Integer.parseInt(inpT));
            foodField.setText("");
            foodField.setFocus(true);
        } else {
            riggleTest.setText("Enter a bloody number!");
            riggleTest.setTextBold();
            foodField.setText("");
            foodField.setFocus(true);
        }
    } //_CODE_:foodbutton:329183:

    public void foodFieldEvent(GTextField source, GEvent event) { //_CODE_:foodField:205521:
        println("textfield1 - GTextField event occured " + System.currentTimeMillis() % 10000000);
    } //_CODE_:foodField:205521:

    public void slidedMSSS(GSlider source, GEvent event) { //_CODE_:maxSpeedSliderSpd:499167:
        println("slider1 - GSlider event occured " + System.currentTimeMillis() % 10000000);

        float speedVal = maxSpeedSliderSpd.getValueF();
        maxSpeedLabelSpd.setText("MaximumSpeed(SpeedType):" + String.format("%.2f", speedVal));
        maxSpeedLabelSpd.setTextBold();
    } //_CODE_:maxSpeedSliderSpd:499167:

    public void slidedMSSN(GSlider source, GEvent event) { //_CODE_:maxSpeedSliderSns:557246:
        println("maxSpeedSliderSns - GSlider event occured " + System.currentTimeMillis() % 10000000);

        float speedVal = maxSpeedSliderSns.getValueF();
        maxSpeedLabelSns.setText("MaximumSpeed(SenseType):" + String.format("%.2f", speedVal));
        maxSpeedLabelSns.setTextBold();
    } //_CODE_:maxSpeedSliderSns:557246:

    public void slidedBSS(GSlider source, GEvent event) { //_CODE_:birthSizeSlider:955397:
        println("birthSizeSlider - GSlider event occured " + System.currentTimeMillis() % 10000000);

        int brSz = birthSizeSlider.getValueI();
        birthSizeLabel.setText("RigglerBirthSize:" + brSz);
        birthSizeLabel.setTextBold();
    } //_CODE_:birthSizeSlider:955397:

    public void rigglersKilled(GButton source, GEvent event) { //_CODE_:killAllRigglers:558562:
        println("killAllRigglers - GButton event occured " + System.currentTimeMillis() % 10000000);

        en.clear();
    } //_CODE_:killAllRigglers:558562:

    public void slidedBFS(GSlider source, GEvent event) { //_CODE_:birthForceSlider:932819:
        println("birthForceSlider - GSlider event occured " + System.currentTimeMillis() % 10000000);

        float brFr = birthForceSlider.getValueF();
        birthForceLabel.setText("BirthForce:" + String.format("%.2f", brFr));
        birthForceLabel.setTextBold();
    } //_CODE_:birthForceSlider:932819:

    public void clearedFood(GButton source, GEvent event) { //_CODE_:clearFood:777866:
        println("clearFood - GButton event occured " + System.currentTimeMillis() % 10000000);

        fooSys.clear();
    } //_CODE_:clearFood:777866:


    // Create all the GUI controls.
    // autogenerated do not edit
    public void createGUI() {
        G4P.messagesEnabled(false);
        G4P.setGlobalColorScheme(GCScheme.BLUE_SCHEME);
        G4P.setCursor(ARROW);
        if (frame != null)
            frame.setTitle("Sketch Window");
        numberOfRigglers = new GLabel(this, 1, 1, 155, 32);
        numberOfRigglers.setOpaque(true);
        RigglerControlPanel = new GPanel(this, 550, 0, 150, 500, "Riggler Control Panel");
        RigglerControlPanel.setCollapsible(false);
        RigglerControlPanel.setDraggable(false);
        RigglerControlPanel.setText("Riggler Control Panel");
        RigglerControlPanel.setTextBold();
        RigglerControlPanel.setOpaque(true);
        RigglerControlPanel.addEventHandler(this, "RCPClick");
        riggleButton = new GButton(this, 4, 25, 80, 20);
        riggleButton.setText("Riggle");
        riggleButton.setTextBold();
        riggleButton.addEventHandler(this, "riggleButtonClicked");
        riggleAmount = new GTextField(this, 90, 25, 50, 20, G4P.SCROLLBARS_NONE);
        riggleAmount.setOpaque(true);
        riggleAmount.addEventHandler(this, "riggleAmountField");
        riggleTest = new GLabel(this, 0, 580, 150, 20);
        riggleTest.setOpaque(false);
        foodbutton = new GButton(this, 4, 50, 80, 20);
        foodbutton.setText("Food");
        foodbutton.setTextBold();
        foodbutton.addEventHandler(this, "addFoodButton");
        foodField = new GTextField(this, 90, 50, 50, 20, G4P.SCROLLBARS_NONE);
        foodField.setOpaque(true);
        foodField.addEventHandler(this, "foodFieldEvent");
        maxSpeedSliderSpd = new GSlider(this, 4, 106, 142, 10, 10.0f);
        maxSpeedSliderSpd.setShowValue(true);
        maxSpeedSliderSpd.setShowLimits(true);
        maxSpeedSliderSpd.setLimits(5.0f, 1.0f, 10.0f);
        maxSpeedSliderSpd.setNumberFormat(G4P.DECIMAL, 2);
        maxSpeedSliderSpd.setOpaque(false);
        maxSpeedSliderSpd.addEventHandler(this, "slidedMSSS");
        maxSpeedLabelSpd = new GLabel(this, 8, 72, 124, 30);
        maxSpeedLabelSpd.setText("MaxSpeed(SpeedType):5.0");
        maxSpeedLabelSpd.setTextBold();
        maxSpeedLabelSpd.setOpaque(false);
        maxSpeedLabelSns = new GLabel(this, 6, 120, 134, 30);
        maxSpeedLabelSns.setText("MaxSpeed(SenseType):2.0");
        maxSpeedLabelSns.setTextBold();
        maxSpeedLabelSns.setOpaque(false);
        maxSpeedSliderSns = new GSlider(this, 4, 156, 142, 10, 10.0f);
        maxSpeedSliderSns.setShowValue(true);
        maxSpeedSliderSns.setShowLimits(true);
        maxSpeedSliderSns.setLimits(1.0f, 1.0f, 10.0f);
        maxSpeedSliderSns.setNumberFormat(G4P.DECIMAL, 2);
        maxSpeedSliderSns.setOpaque(false);
        maxSpeedSliderSns.addEventHandler(this, "slidedMSSN");
        birthSizeLabel = new GLabel(this, 4, 208, 134, 20);
        birthSizeLabel.setText("RigglerBirthSize:11");
        birthSizeLabel.setTextBold();
        birthSizeLabel.setOpaque(false);
        birthSizeSlider = new GSlider(this, 4, 234, 142, 10, 10.0f);
        birthSizeSlider.setLimits(11, 2, 30);
        birthSizeSlider.setNumberFormat(G4P.INTEGER, 0);
        birthSizeSlider.setOpaque(false);
        birthSizeSlider.addEventHandler(this, "slidedBSS");
        killAllRigglers = new GButton(this, 40, 535, 70, 20);
        killAllRigglers.setText("Terminate");
        killAllRigglers.setTextBold();
        killAllRigglers.setTextItalic();
        killAllRigglers.addEventHandler(this, "rigglersKilled");
        birthForceLabel = new GLabel(this, 4, 174, 142, 20);
        birthForceLabel.setText("MaxForce:0.05");
        birthForceLabel.setTextBold();
        birthForceLabel.setOpaque(false);
        birthForceSlider = new GSlider(this, 4, 198, 142, 10, 10.0f);
        birthForceSlider.setLimits(0.05f, 0.02f, 0.09f);
        birthForceSlider.setNumberFormat(G4P.DECIMAL, 2);
        birthForceSlider.setOpaque(false);
        birthForceSlider.addEventHandler(this, "slidedBFS");
        clearFood = new GButton(this, 40, 510, 70, 20);
        clearFood.setText("ClearFood");
        clearFood.setTextBold();
        clearFood.setTextItalic();
        clearFood.addEventHandler(this, "clearedFood");
        RigglerControlPanel.addControl(riggleButton);
        RigglerControlPanel.addControl(riggleAmount);
        RigglerControlPanel.addControl(riggleTest);
        RigglerControlPanel.addControl(foodbutton);
        RigglerControlPanel.addControl(foodField);
        RigglerControlPanel.addControl(maxSpeedSliderSpd);
        RigglerControlPanel.addControl(maxSpeedLabelSpd);
        RigglerControlPanel.addControl(maxSpeedLabelSns);
        RigglerControlPanel.addControl(maxSpeedSliderSns);
        RigglerControlPanel.addControl(birthSizeLabel);
        RigglerControlPanel.addControl(birthSizeSlider);
        RigglerControlPanel.addControl(killAllRigglers);
        RigglerControlPanel.addControl(birthForceLabel);
        RigglerControlPanel.addControl(birthForceSlider);
        RigglerControlPanel.addControl(clearFood);
    }

    // Variable declarations
    // autogenerated do not edit
    GLabel numberOfRigglers;
    GPanel RigglerControlPanel;
    GButton riggleButton;
    GTextField riggleAmount;
    GLabel riggleTest;
    GButton foodbutton;
    GTextField foodField;
    GSlider maxSpeedSliderSpd;
    GLabel maxSpeedLabelSpd;
    GLabel maxSpeedLabelSns;
    GSlider maxSpeedSliderSns;
    GLabel birthSizeLabel;
    GSlider birthSizeSlider;
    GButton killAllRigglers;
    GLabel birthForceLabel;
    GSlider birthForceSlider;
    GButton clearFood;

    public static void main(String[] args) {
        PApplet.main("Rigglers");
    }
}
