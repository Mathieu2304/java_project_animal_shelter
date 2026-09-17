package model;

import java.util.Set;

public class Bird extends Animal {

    private static final int MIN_ADOPTION_AGE_WEEKS = 6;
    private static final Set<String> REQUIRED_VACCINATIONS = Set.of("FVRCP", "Rabies");

    private boolean canFly;

    public Bird(String id, String name, int age, int arrivalDate, AnimalStatus status, boolean canFly) {
        super(id, name, age, arrivalDate, status);
        this.canFly = canFly;
    }

    public boolean getCanFly() {
        return canFly;
    }

    @Override
    public boolean isAdoptionEligible() {

        return getStatus() == AnimalStatus.AVAILABLE && getAge() >= MIN_ADOPTION_AGE_WEEKS
                && getMedicalRecord().hasAllRequiredVaccinations(REQUIRED_VACCINATIONS);
    }
}