import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;

import static processing.core.PApplet.*;
import static processing.core.PConstants.PI;

class Entity {

    private PApplet parent;

    private PVector location;
    private PVector velocity;
    private PVector acceleration;

    private float size;
    private int shade;
    private int strocke;

    private float mass;
    private float restitution;

    private float maxSpeed;
    private float maxForce;

    private float senseRange;

    private boolean gender;

    private float tailSwipe;
    private float woff;

    private int fullLife;
    private int lifeSpan;

    private int xBound;
    private int yBound;

    Entity(Rigglers parent, PVector location, float size, int shade, int strocke, float mass, float restitution,
           float maxSpeed, float maxForce, float senseRange, boolean gender, int lifeSpan, int xBound, int yBound) {

        this.parent = parent;

        this.location = location.get();
        this.size = size;
        this.shade = shade;
        this.strocke = strocke;
        this.mass = mass;
        this.restitution = restitution;
        this.maxSpeed = maxSpeed;
        this.maxForce = maxForce;
        this.senseRange = senseRange;
        this.gender = gender;
        this.lifeSpan = lifeSpan;
        this.fullLife = lifeSpan;

        velocity = new PVector();
        acceleration = new PVector();

        tailSwipe = 0;
        woff = parent.random(100000);

        xBound = parent.getxBound();
        yBound = parent.getyBound();
    }

    private void applyForce(PVector force) {
        acceleration.add(force);
    }

    void flock(ArrayList<Entity> alEnt, float afLimit) {

        PVector toFlock = new PVector();
        int avgCount = 0;
        for (Entity ent : alEnt) {
            if (PVector.dist(ent.location, location) < size * 7) {
                toFlock.add(ent.location);
                avgCount++;
            }
        }

        toFlock.div(avgCount);
        steer(toFlock);
    }

    void update() {

        velocity.add(acceleration);
        location.add(velocity);
        edgeCheck();

        acceleration.mult(0);
    }

    void dieOff() {
        lifeSpan--;
    }

    boolean isDead() {
        return lifeSpan <= 0;
    }

    void steer(PVector target) {

        PVector desired = PVector.sub(target, location);
        float desMag = desired.mag();
        desired.normalize();

        if (desMag < size * 3) {
            float limiter = map(desMag, 0, size * 3, 0, maxSpeed);
            desired.mult(limiter);
        } else {
            desired.mult(maxSpeed);
        }

        PVector steeringForce = PVector.sub(desired, velocity);
        steeringForce.limit(maxForce);
        applyForce(steeringForce);

    }

    void edgeCheck() {

        if (location.x + size / 2 > xBound) {
            location.x = xBound - size / 2;
            velocity.mult(-(1 - restitution));
        }
        if (location.y + size / 2 > yBound) {
            location.y = yBound - size / 2;
            velocity.mult(-(1 - restitution));
        }
        if (location.x - size / 2 < 0) {
            location.x = 0 + size / 2;
            velocity.mult(-(1 - restitution));
        }
        if (location.y - size / 2 < 0) {
            location.y = 0 + size / 2;
            velocity.mult(-(1 - restitution));
        }
    }

    void separate(ArrayList<Entity> alEnt, float afLimit) {
        int count = 0;
        PVector away = new PVector();
        for (Entity ent : alEnt) {
            if (PVector.dist(location, ent.location) > 0 && PVector.dist(location, ent.location) < size * 4.1) {
                PVector awayForce = PVector.sub(location, ent.location);
                awayForce.normalize();
                away.add(awayForce);
                count++;
            }
        }

        if (count > 0) {
            away.div(count);
            away.setMag(maxSpeed);
            PVector steerF = PVector.sub(away, velocity);
            steerF.limit(afLimit);

            applyForce(steerF);
        }
    }

    void display() {
        parent.pushMatrix();
        parent.translate(location.x, location.y);
        parent.rotate(velocity.heading() + PI / 2);

        parent.fill(shade);
        int forStroke = 255;

        if (isHungry()) {
            forStroke = parent.color(0, 0, 255);
        }
        if (isAboutToDie()) {
            forStroke = parent.color(255, 0, 0);
        }

        tail(new PVector(0, 0), size * 0.5f, PI / 4, forStroke);

        if (gender) {
            parent.strokeWeight(4);
            parent.line(0, 0, 0, -size * 0.7f);
        }

        parent.stroke(255);
        parent.strokeWeight(1);
        parent.ellipse(0, 0, size * 0.8f, size);


        parent.popMatrix();
    }

    boolean isHungry() {
        return lifeSpan < fullLife / 2;
    }

    boolean isAboutToDie() {
        return lifeSpan < fullLife / 5;
    }

    void fed() {
        lifeSpan = fullLife;
    }

    void displayRange() {

        parent.noStroke();
        parent.fill(200, 50);
        parent.pushMatrix();
        parent.translate(location.x, location.y);
        parent.ellipse(0, 0, senseRange * 2, senseRange * 2);
        parent.popMatrix();
    }

    void wander(float wanFixed, float wanRad, float wanRange) {

        PVector wanderCircle = new PVector(location.x, location.y);

        PVector addWander = velocity.get();
        addWander.normalize();
        addWander.mult(wanFixed);

        wanderCircle.add(addWander);

        float ranDeg = map(parent.noise(woff), 0, 1, -PI / 2, PI / 2);
        woff += 0.03;

        float wanX = (cos(ranDeg) * wanRad) + wanderCircle.x;
        float wanY = (sin(ranDeg) * wanRad) + wanderCircle.y;

        parent.fill(255);
        steer(new PVector(wanX, wanY));

    }

    void tail(PVector location, float len, float deg, int forStroke) {

        float vMag = velocity.mag();

        float firstC = sin(tailSwipe) * vMag * 1;
        float secondC = sin(tailSwipe + PI) * vMag * 0.5f;
        tailSwipe += 0.5;

        parent.pushMatrix();

        parent.translate(location.x, location.y);
        parent.stroke(forStroke);

        parent.strokeWeight(3);
        parent.line(0, 0, firstC, size);

        parent.translate(0, size);
        parent.strokeWeight(2);
        parent.line(firstC, 0, secondC, size / 2);

        parent.translate(0, size / 2);
        parent.strokeWeight(1);
        parent.line(secondC, 0, firstC, size / 2);

        parent.popMatrix();
    }

    public PVector getLocation() {
        return location;
    }

    public void setLocation(PVector location) {
        this.location = location;
    }

    public PVector getVelocity() {
        return velocity;
    }

    public void setVelocity(PVector velocity) {
        this.velocity = velocity;
    }

    public PVector getAcceleration() {
        return acceleration;
    }

    public void setAcceleration(PVector acceleration) {
        this.acceleration = acceleration;
    }

    public float getSize() {
        return size;
    }

    public void setSize(float size) {
        this.size = size;
    }

    public int getShade() {
        return shade;
    }

    public void setShade(int shade) {
        this.shade = shade;
    }

    public int getStrocke() {
        return strocke;
    }

    public void setStrocke(int strocke) {
        this.strocke = strocke;
    }

    public float getMass() {
        return mass;
    }

    public void setMass(float mass) {
        this.mass = mass;
    }

    public float getRestitution() {
        return restitution;
    }

    public void setRestitution(float restitution) {
        this.restitution = restitution;
    }

    public float getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(float maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public float getMaxForce() {
        return maxForce;
    }

    public void setMaxForce(float maxForce) {
        this.maxForce = maxForce;
    }

    public float getSenseRange() {
        return senseRange;
    }

    public void setSenseRange(float senseRange) {
        this.senseRange = senseRange;
    }

    public boolean isGender() {
        return gender;
    }

    public void setGender(boolean gender) {
        this.gender = gender;
    }

    public float getTailSwipe() {
        return tailSwipe;
    }

    public void setTailSwipe(float tailSwipe) {
        this.tailSwipe = tailSwipe;
    }

    public float getWoff() {
        return woff;
    }

    public void setWoff(float woff) {
        this.woff = woff;
    }

    public int getFullLife() {
        return fullLife;
    }

    public void setFullLife(int fullLife) {
        this.fullLife = fullLife;
    }

    public int getLifeSpan() {
        return lifeSpan;
    }

    public void setLifeSpan(int lifeSpan) {
        this.lifeSpan = lifeSpan;
    }
}
