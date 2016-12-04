package io.github.scionofbytes.Rigglers;

import g4p_controls.*;
import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.Iterator;

public class Main extends PApplet {

    private ArrayList<Sperm> sperms;
    private FoodSystem foodSystem;

    private boolean centerEmitter;
    private float radiusFromEmitter;

    private int xBound;
    private int yBound;

    private float restitution;
    private float mass;

    private float senseTypeProbability;

    private float birthSpeed;
    private float speedTypeSpeed;
    private float birthForce;

    private float birthSenseRange;
    private float senseTypeRange;

    private float birthSize;
    private int strokeColor;

    private float femaleProbability;

    private int lifeSpan;

    private int maxSpermCount;
    private float separationCoefficient;

    public void settings() {
        fullScreen();
    }

    public void setup() {

        background(0);
        createGUI();

        sperms = new ArrayList<>();

        centerEmitter = true;
        radiusFromEmitter = 100;

        xBound = width - 150;
        yBound = height;

        restitution = 0.7f;
        mass = 0;

        senseTypeProbability = 0.5f;

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
        separationCoefficient = PI / 180 * 2;

        int rateOfFood = 5;
        int maxFood = 50;
        float foodRange = height / 2;
        float foodSize = 5;
        int foodShade = color(0, 255, 0);

        foodSystem = new FoodSystem(this, maxFood, rateOfFood, foodRange, foodSize, foodShade, xBound, yBound);
    }

    public void draw() {

        background(0);
        outputRigglerCount(sperms);
        foodSystem.display();

        Iterator spermIterator = sperms.iterator();

        while (spermIterator.hasNext()) {

            Sperm sperm = (Sperm) spermIterator.next();

            if (sperm.isDead()) {
                spermIterator.remove();
            }

            if (sperm.isHungry()) {
                steerToFood(sperm);
                eatFood(sperm);
            }

            sperm.separate(sperms, separationCoefficient);
            sperm.update();
            sperm.display();
            sperm.dieOff();
        }

    }

    private void eatFood(Sperm thEn) {

        if (foodSystem.hasFood()) {

            Food theFood = foodSystem.nearestFood(thEn.getLocation());

            if (PVector.dist(theFood.foodLocation, thEn.getLocation()) < thEn.getSize()) {

                foodSystem.remove(theFood);
                thEn.fed();
            }
        }
    }

    private void steerToFood(Sperm thEn) {

        if (foodSystem.hasFood()) {

            Food toFood = foodSystem.nearestFood(thEn.getLocation());
            thEn.steer(toFood.foodLocation);
        }
    }

    private void addSperm() {

        PVector location;
        float birthSenseRange;
        float birthSpeed;

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

        int birthColor = color(random(255), random(255), random(255));

        if (random(1) < senseTypeProbability) {

            birthSenseRange = senseTypeRange;// * random( 1 - geneticHit );
            birthSpeed = this.birthSpeed;
        } else {

            birthSenseRange = this.birthSenseRange;// * random( 1 - geneticHit );
            birthSpeed = speedTypeSpeed;
        }

        boolean gender;

        gender = random(1) < femaleProbability;

        Sperm ent = new Sperm(this, location, birthSize, birthColor, strokeColor, mass, restitution, birthSpeed, birthForce,
                birthSenseRange, gender, lifeSpan);
        sperms.add(ent);
    }

    private void outputRigglerCount(ArrayList<Sperm> arEl) {

        int nOR = arEl.size();
        int nOH = 0;
        int nOAtD = 0;

        for (Sperm ent : arEl) {
            if (ent.isHungry()) nOH++;
            if (ent.isAboutToDie()) nOAtD++;
        }
        String sT = "Main: " + Integer.toString(nOR) + ", Hungry: " + Integer.toString(nOH) + ", About to Die: " + Integer.toString(nOAtD) + ", Food: " + Integer.toString(foodSystem.size());
        stroke(255);
        numberOfRigglers.setText(sT);
    }

    private void addMeFood(int hMF) {

        for (int i = 0; i < hMF; i++) {
            foodSystem.addOneFood();
        }
    }

    private void riggleMySperm(int riggleInputSize) {

        int noOfSpermsPresent = sperms.size();
        int noOfEmptySpermSlots = maxSpermCount - noOfSpermsPresent;
        int noOfSpermToBeAdded;

        if (riggleInputSize < noOfEmptySpermSlots) {
            noOfSpermToBeAdded = riggleInputSize;
        } else {
            noOfSpermToBeAdded = noOfEmptySpermSlots;
        }

        for (int i = 0; i < noOfSpermToBeAdded; i++) {
            addSperm();
        }
    }

    int getXBound() {
        return xBound;
    }

    int getYBound() {
        return yBound;
    }

    private static boolean isNumeric(String str) {

        try {
            Integer.parseInt(str);
        } catch (NumberFormatException nfe) {
            return false;
        }

        return true;
    }

    public static void main(String[] args) {
        PApplet.main("io.github.scionofbytes.Rigglers.Main");
    }

    public void riggleButtonClicked(GButton source, GEvent event) {

        String inpT = riggleAmount.getText();
        inpT.trim();

        if (Main.isNumeric(inpT)) {

            this.birthSpeed = maxSpeedSliderSns.getValueF();
            this.speedTypeSpeed = maxSpeedSliderSpd.getValueF();
            this.birthSize = birthSizeSlider.getValueI();
            this.birthForce = birthForceSlider.getValueF();
            this.riggleMySperm(Integer.parseInt(inpT));

            riggleTest.setText("");
            riggleAmount.setText("");
            riggleAmount.setFocus(true);

        } else {

            riggleTest.setText("Enter a bloody number!");
            riggleAmount.setText("");
            riggleTest.setTextBold();
            riggleAmount.setFocus(true);
        }
    }

    public void addFoodButton(GButton source, GEvent event) {

        String inpT = foodField.getText();
        inpT.trim();

        if (Main.isNumeric(inpT)) {

            this.addMeFood(Integer.parseInt(inpT));
            foodField.setText("");
            foodField.setFocus(true);
        } else {

            riggleTest.setText("Enter a bloody number!");
            riggleTest.setTextBold();
            foodField.setText("");
            foodField.setFocus(true);
        }
    }

    public void slidedMSSS(GSlider source, GEvent event) {

        float speedVal = maxSpeedSliderSpd.getValueF();

        maxSpeedLabelSpd.setText("MaximumSpeed(SpeedType):" + String.format("%.2f", speedVal));
        maxSpeedLabelSpd.setTextBold();
    }

    public void slidedMSSN(GSlider source, GEvent event) {

        float speedVal = maxSpeedSliderSns.getValueF();

        maxSpeedLabelSns.setText("MaximumSpeed(SenseType):" + String.format("%.2f", speedVal));
        maxSpeedLabelSns.setTextBold();
    }

    public void slidedBSS(GSlider source, GEvent event) {

        int brSz = birthSizeSlider.getValueI();

        birthSizeLabel.setText("RigglerBirthSize:" + brSz);
        birthSizeLabel.setTextBold();
    }

    public void rigglersKilled(GButton source, GEvent event) {
        this.sperms.clear();
    }

    public void slidedBFS(GSlider source, GEvent event) {

        float brFr = birthForceSlider.getValueF();

        birthForceLabel.setText("BirthForce:" + String.format("%.2f", brFr));
        birthForceLabel.setTextBold();
    }

    public void clearedFood(GButton source, GEvent event) {
        this.foodSystem.clear();
    }

    private GLabel numberOfRigglers;
    private GPanel RigglerControlPanel;
    private GButton riggleButton;
    private GTextField riggleAmount;
    private GLabel riggleTest;
    private GButton foodButton;
    private GTextField foodField;
    private GSlider maxSpeedSliderSpd;
    private GLabel maxSpeedLabelSpd;
    private GLabel maxSpeedLabelSns;
    private GSlider maxSpeedSliderSns;
    private GLabel birthSizeLabel;
    private GSlider birthSizeSlider;
    private GButton killAllRigglers;
    private GLabel birthForceLabel;
    private GSlider birthForceSlider;
    private GButton clearFood;

    private void createGUI() {

        G4P.messagesEnabled(false);
        G4P.setGlobalColorScheme(GCScheme.BLUE_SCHEME);
        G4P.setCursor(ARROW);

        if (this.surface != null) {
            this.surface.setTitle("Sketch Window");
        }

        numberOfRigglers = new GLabel(this, 1, 1, 155, 32);
        numberOfRigglers.setOpaque(true);

        RigglerControlPanel = new GPanel(this, this.width - 150, 0, 150, this.height, "Riggler Control Panel");
        RigglerControlPanel.setCollapsible(true);
        RigglerControlPanel.setDraggable(false);
        RigglerControlPanel.setOpaque(true);

        riggleButton = new GButton(this, 4, 25, 80, 20);
        riggleButton.setText("Riggle");
        riggleButton.setTextBold();
        riggleButton.addEventHandler(this, "riggleButtonClicked");

        riggleAmount = new GTextField(this, 90, 25, 50, 20, G4P.SCROLLBARS_NONE);
        riggleAmount.setOpaque(true);
        riggleAmount.addEventHandler(this, "riggleAmountField");

        riggleTest = new GLabel(this, 0, 580, 150, 20);
        riggleTest.setOpaque(false);

        foodButton = new GButton(this, 4, 50, 80, 20);
        foodButton.setText("Food");
        foodButton.setTextBold();
        foodButton.addEventHandler(this, "addFoodButton");

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
        RigglerControlPanel.addControl(foodButton);
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
}
