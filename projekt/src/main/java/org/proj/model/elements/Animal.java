package org.proj.model.elements;

import javafx.scene.paint.Color;
import org.proj.utils.IMoveValidator;
import org.proj.utils.EMapDirection;
import org.proj.utils.PositionOrientationTuple;
import org.proj.utils.Vector2d;

import java.util.*;

public class Animal implements IWorldElement {
    // for random orientation etc
    private static final Random random = new Random();

    // main fields
    private Vector2d position;
    private EMapDirection orientation;
    private final EMoveStyle moveStyle;
    private boolean back = false;

    // simulation values
    private int energy;
    private final int maxEnergy;
    private int age;
    private final int birthDate;
    private int deathDate;
    private final int[] genome;
    private int geneIndex;
    private int childrenMade;
    private int plantsEaten;

    private final List<Animal> children = new ArrayList<>();

    // constructor
    public Animal(Vector2d position, int energy, int maxEnergy, int birthDate, int[] genome, EMoveStyle moveStyle) {
        this.position = position;
        this.orientation = EMapDirection.values()[random.nextInt(8)];
        this.moveStyle = moveStyle;
        this.energy = energy;
        this.maxEnergy = maxEnergy;
        this.age = 0;
        this.birthDate = birthDate;
        this.deathDate = -1;
        this.genome = genome;
        this.geneIndex = 0;
        this.childrenMade = 0;
        this.plantsEaten = 0;
    }

    public List<Animal> getChildren() {
        return children;
    }

    // getters
    @Override
    public Vector2d getPosition(){ return this.position; }
    public EMapDirection getOrientation() { return this.orientation; }
    public EMoveStyle getMoveStyle() { return moveStyle; }
    public Integer getEnergy() { return energy; }
    public Integer getMaxEnergy() { return maxEnergy; }
    public Integer getAge() { return age; }
    public Integer getBirthDate() { return birthDate; }
    public Integer getDeathDate() { return deathDate; }
    public int[] getGenome() { return genome; }
    public Integer getGeneIndex() { return geneIndex; }
    public Integer getChildrenMade() { return childrenMade; }
    public Integer getPlantsEaten() { return plantsEaten; }

    // setters
    public void setPosition(Vector2d position) { this.position = position; }
    public void setOrientation(EMapDirection orientation) { this.orientation = orientation; }
    public void addAge() { this.age++; }
    public void setDeathDate(int deathDate) { this.deathDate = deathDate; }
    public void removeEnergy(int energy) { this.energy -= energy; }
    public void addChild() { this.childrenMade++; }

    public boolean isAt(Vector2d position){ return this.position.equals(position);}

    public void move(IMoveValidator moveValidator) {
        if(moveStyle == EMoveStyle.FULLY_PREDESTINED) {
            this.geneIndex = (this.geneIndex + 1) % this.genome.length;
        }
        else if (moveStyle == EMoveStyle.BACK_AND_FORTH) {
            if (this.geneIndex == this.genome.length - 1)
                this.back = true;
            else if (this.geneIndex == 0)
                this.back = false;

            if (this.back)
                this.geneIndex--;
            else
                this.geneIndex++;
        }

        EMapDirection newOrientation = this.orientation.rotate(this.genome[this.geneIndex]);
        Vector2d newPosition = this.position.add(newOrientation.unitVector());
        PositionOrientationTuple correctedPosition = moveValidator.correctPosition(this.position, newPosition, newOrientation);
        this.orientation = correctedPosition.orientation();
        this.position = correctedPosition.position();
    }

    public void eat(int howMuch) {
        this.energy += howMuch;
        if (this.energy > this.maxEnergy) {
            this.energy = this.maxEnergy;
        }
        this.plantsEaten++;
    }

    public void addChildToList(Animal child) {
        this.children.add(child);
    }

    public synchronized Integer countDescendants() {
        Set<Animal> visited = new HashSet<>();
        return countDescendantsRecursive(this, visited);
    }

    public Integer countDescendantsRecursive(Animal animal, Set<Animal> visited) {
        int count = 0;
        visited.add(animal);
        for (Animal child : animal.getChildren()) {
            if (!visited.contains(child)) {
                count += countDescendantsRecursive(child, visited)+1;
            }
        }
        return count;
    }

    @Override
    public FieldPaint getFieldPaint() {
        float k = 0.2f + (float)(energy*0.8)/maxEnergy;
        Color entityColor = new Color(Color.CHOCOLATE.getRed()*k, Color.CHOCOLATE.getGreen()*k, Color.CHOCOLATE.getBlue()*k, 1.0);
        return new FieldPaint(entityColor, Color.TRANSPARENT);
    }

    @Override
    public EElementType getElementType() {
        return EElementType.ANIMAL;
    }
}
