package model;

import java.util.Set;

public class Dog extends Animal {

    private static final int MIN_ADOPTION_AGE_WEEKS = 10;
    private static final Set<String> REQUIRED_VACCINATIONS = Set.of("RAGE", "DHPP");

    private String breed;

    public Dog(String id, String name, int age, int arrivalDate, AnimalStatus status, String breed) {
        super(id, name, age, arrivalDate, status);
        this.breed = breed;
    }

    public String getBreed() {
        return breed;
    }

    @Override
    public boolean isAdoptionEligible() {
        return getStatus() == AnimalStatus.AVAILABLE && getAge() >= MIN_ADOPTION_AGE_WEEKS
                && getMedicalRecord().hasAllRequiredVaccinations(REQUIRED_VACCINATIONS);
    }
}