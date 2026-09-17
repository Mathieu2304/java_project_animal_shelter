package model;

import java.util.Set;

public class Cat extends Animal {

    private static final int MIN_ADOPTION_AGE_WEEKS = 8;
    private static final Set<String> REQUIRED_VACCINATIONS = Set.of("FVRCP");
    private boolean indoorOnly;

    public Cat(String id, String name, int age, int arrivalDate, AnimalStatus status, boolean indoorOnly) {
        super(id, name, age, arrivalDate, status);
        this.indoorOnly = indoorOnly;
    }

    public boolean getIndoorOnly() {
        return indoorOnly;
    }

    @Override
    public boolean isAdoptionEligible() {
        return getStatus() == AnimalStatus.AVAILABLE && getAge() >= MIN_ADOPTION_AGE_WEEKS
                && getMedicalRecord().hasAllRequiredVaccinations(REQUIRED_VACCINATIONS);
    }
}