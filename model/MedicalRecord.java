package model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MedicalRecord {

    private final List<String> notes = new ArrayList<>();
    private final List<String> treatments = new ArrayList<>();
    private final Set<String> vaccinations = new HashSet<>();

    public boolean addVaccination(String vaccination) {
        return vaccinations.add(vaccination);
    }

    public boolean hasVaccination(String vaccination) {
        return vaccinations.contains(vaccination);
    }

    public boolean addTreatment(String treatment) {
        return treatments.add(treatment);
    }

    public boolean addNote(String note) {
        return notes.add(note);
    }

    public List<String> getTreatments() {
        return List.copyOf(this.treatments);
    }

    public List<String> getNotes() {
        return List.copyOf(this.notes);
    }

    public Set<String> getVaccinations() {
        return Set.copyOf(this.vaccinations);
    }

    public boolean hasAllRequiredVaccinations(Set<String> requiredVaccinations) {
        return vaccinations.containsAll(requiredVaccinations);
    }

}