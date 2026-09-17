package model;

import contract.Identifiable;
import exception.ShelterException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Doctor implements Identifiable {

    private final String doctorId;
    private final String name;
    private final String specialization;
    private final List<String> performedTreatments;

    public Doctor(String doctorId, String name, String specialization) {
        if (doctorId == null || doctorId.isBlank()) {
            throw new ShelterException("Doctor id is required.");
        }
        if (name == null || name.isBlank()) {
            throw new ShelterException("The doctor's name is required.");
        }
        if (specialization == null || specialization.isBlank()) {
            throw new ShelterException("The doctor's specialization is required.");
        }

        this.doctorId = doctorId;
        this.name = name;
        this.specialization = specialization;
        this.performedTreatments = new ArrayList<>();
    }

    @Override
    public String getId() {
        return doctorId;
    }

    public String getName() {
        return name;
    }

    public String getSpecialization() {
        return specialization;
    }

    public List<String> getPerformedTreatments() {
        return Collections.unmodifiableList(performedTreatments);
    }

    public void performTreatment(String animalId, String treatmentDescription) {
        if (animalId == null || animalId.isBlank()) {
            throw new ShelterException("The animal's ID is required.");
        }
        if (treatmentDescription == null || treatmentDescription.isBlank()) {
            throw new ShelterException("The treatment description is required.");
        }

        String record = "Animal [" + animalId + "] : " + treatmentDescription;
        this.performedTreatments.add(record);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Doctor doctor = (Doctor) o;
        return Objects.equals(doctorId, doctor.doctorId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(doctorId);
    }

    @Override
    public String toString() {
        return "Dr. " + name + " (ID: " + doctorId + ", Spécialité: " + specialization + ")";
    }
}