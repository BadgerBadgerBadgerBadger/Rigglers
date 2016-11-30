import processing.core.PVector;

import java.util.ArrayList;

import static processing.core.PApplet.*;
import static processing.core.PConstants.TWO_PI;

class FoodSystem {

    private Rigglers parent;

    private ArrayList<Food> fS;

    private int maxFood;
    private int genRate;
    private float rangeRadius;

    private float foodSize;
    private int shade;

    FoodSystem(Rigglers parent, int maxFood, int genRate, float rangeRadius, float foodSize, int shade, int xBound,
               int yBound) {

        this.parent = parent;

        this.maxFood = maxFood;
        this.genRate = genRate;
        this.rangeRadius = rangeRadius;
        this.foodSize = foodSize;
        this.shade = shade;

        fS = new ArrayList<Food>();
    }

    void display() {
        for (Food foo : fS) {
            foo.display();
        }
    }

    int size() {
        return fS.size();
    }

    Food nearestFood(PVector something) {
        Food theFood = null;
        float dist = parent.width * 3;

        for (Food f : fS) {
            if (PVector.dist(something, f.foodLocation) < dist) {
                dist = PVector.dist(something, f.foodLocation);
                theFood = f;
            }
        }

        return theFood;
    }

    void clear() {
        fS.clear();
    }

    void remove(Food f) {
        fS.remove(f);
    }

    boolean hasFood() {
        if (fS.size() > 0) return true;
        else return false;
    }

    void addOneFood() {
        float deg = parent.random(TWO_PI);
        float rad = parent.random(rangeRadius);

        PVector loc = new PVector(rad * cos(deg), rad * sin(deg));
        loc.add(new PVector(parent.getxBound() / 2, parent.getyBound() / 2));

        if (fS.size() < maxFood) {
            fS.add(new Food(this.parent, loc, foodSize, shade));
        }
    }

    void generate() {

        float deg = parent.random(TWO_PI);
        float rad = parent.random(rangeRadius);

        PVector loc = new PVector(rad * cos(deg), rad * sin(deg));
        loc.add(new PVector(parent.width / 2, parent.height / 2));

        int timeStep = 60 / genRate;
        int inis = parent.frameCount;

        if (fS.size() < maxFood) {

            fS.add(new Food(this.parent, loc, foodSize, shade));
        }
    }

    public ArrayList<Food> getfS() {
        return fS;
    }

    public int getMaxFood() {
        return maxFood;
    }

    public int getGenRate() {
        return genRate;
    }

    public float getRangeRadius() {
        return rangeRadius;
    }

    public float getFoodSize() {
        return foodSize;
    }

    public int getShade() {
        return shade;
    }
}