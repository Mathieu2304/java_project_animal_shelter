package repository;

import exception.ShelterException;
import model.Animal;
import model.MedicalRecord;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class MedicalRecordFileLoader {

    public void loadFromFile(String filePath, Repository<Animal> animalRepo) {
        try (Scanner scanner = new Scanner(new File(filePath))) {
            int lineNumber = 0;
            while (scanner.hasNextLine()) {
                lineNumber++;
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) {
                    continue;
                }
                try {
                    applyLine(line, animalRepo);
                } catch (ShelterException e) {
                    System.out.println("[medical_records.txt] Ligne " + lineNumber + " ignorée : " + e.getMessage());
                }
            }
        } catch (FileNotFoundException e) {
            throw new ShelterException("Medical record data file not found: " + filePath);
        }
    }

    private void applyLine(String line, Repository<Animal> animalRepo) {
        String[] fields = line.split(";", -1);
        if (fields.length != 4) {
            throw new ShelterException("Malformed medical record (expected 4 fields): " + line);
        }

        String animalId = fields[0].trim();
        String vaccinationsField = fields[1].trim();
        String treatmentsField = fields[2].trim();
        String notesField = fields[3].trim();

        if (animalId.isBlank()) {
            throw new ShelterException("Animal id cannot be blank.");
        }
        Animal animal = animalRepo.findById(animalId);

        MedicalRecord record = animal.getMedicalRecord();
        if (!vaccinationsField.isBlank()) {
            String[] vaccinations = vaccinationsField.split(",");

            for (String vaccination : vaccinations) {
                String value = vaccination.trim();

                if (!value.isBlank()) {
                    record.addVaccination(value);
                }
            }
        }
        if (!treatmentsField.isBlank()) {
            String[] treatments = treatmentsField.split(",");

            for (String treatment : treatments) {
                String value = treatment.trim();

                if (!value.isBlank()) {
                    record.addTreatment(value);
                }
            }
        }
        if (!notesField.isBlank()) {
            record.addNote(notesField);
        }
    }
}