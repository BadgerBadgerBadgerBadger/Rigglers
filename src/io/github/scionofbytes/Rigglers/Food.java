package io.github.scionofbytes.Rigglers;

import processing.core.PApplet;
import processing.core.PVector;

import static processing.core.PConstants.CENTER;

class Food {

    private PApplet parent;

    PVector foodLocation;
    private float size;
    private int shade;

    Food(PApplet parent, PVector foodLocation, float size, int shade) {

        this.parent = parent;

        this.foodLocation = foodLocation.get();
        this.size = size;
        this.shade = shade;
    }

    void display() {

        parent.pushMatrix();
        parent.translate(foodLocation.x, foodLocation.y);
        parent.rectMode(CENTER);
        parent.noStroke();
        parent.fill(shade);
        parent.rect(0, 0, size, size);
        parent.popMatrix();
    }
}