package repository;

import exception.ShelterException;
import model.Volunteer;
import model.VolunteerStatus;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

public class VolunteerFileLoader {

    public void loadFromFile(String filePath, Repository<Volunteer> volunteerRepo) {
        if (filePath == null || filePath.isBlank()) {
            throw new ShelterException("File path cannot be null or empty.");
        }
        if (volunteerRepo == null) {
            throw new ShelterException("Volunteer repository cannot be null.");
        }

        try (InputStream is = openStream(filePath);
             Scanner scanner = new Scanner(is)) {

            int lineNumber = 0;
            while (scanner.hasNextLine()) {
                lineNumber++;
                String line = scanner.nextLine().trim();

                // Ignore les lignes vides et les commentaires
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                try {
                    Volunteer volunteer = parseLine(line);
                    volunteerRepo.add(volunteer);
                } catch (ShelterException e) {
                    System.err.println("[Volunteers.txt] Ligne " + lineNumber + " ignorée : " + e.getMessage());
                }
            }
        } catch (IOException e) {
            throw new ShelterException("Volunteer data file not found or unreadable: " + filePath);
        }
    }

    private Volunteer parseLine(String line) {
        // Découpage avec limitation pour éviter les pertes de champs vides
        String[] fields = line.split(";", -1);
        if (fields.length < 5) {
            throw new ShelterException("Malformed volunteer record (expected 5 fields): " + line);
        }

        String id = fields[0].trim();
        String name = fields[1].trim();
        String rawAvailability = fields[2].trim();
        String rawSkills = fields[3].trim();
        String rawStatus = fields[4].trim();

        if (id.isEmpty()) {
            throw new ShelterException("Volunteer ID cannot be empty in line: " + line);
        }
        if (name.isEmpty()) {
            throw new ShelterException("Volunteer name cannot be empty in line: " + line);
        }

        boolean availability = parseAvailability(rawAvailability, line);
        Set<String> skills = parseSkills(rawSkills);
        VolunteerStatus status = parseStatus(rawStatus, line);

        return new Volunteer(id, name, availability, skills, status);
    }

    private boolean parseAvailability(String value, String line) {
        if ("true".equalsIgnoreCase(value) || "1".equals(value)) {
            return true;
        }
        if ("false".equalsIgnoreCase(value) || "0".equals(value)) {
            return false;
        }
        throw new ShelterException("Invalid boolean availability value '" + value + "' in line: " + line);
    }

    private Set<String> parseSkills(String rawSkills) {
        return Arrays.stream(rawSkills.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());
    }

    private VolunteerStatus parseStatus(String value, String line) {
        if (value.isEmpty()) {
            throw new ShelterException("Missing volunteer status in line: " + line);
        }
        try {
            return VolunteerStatus.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ShelterException("Invalid volunteer status '" + value + "' in line: " + line);
        }
    }

    private InputStream openStream(String filePath) throws IOException {
        File file = new File(filePath);
        if (file.exists() && file.isFile()) {
            return new FileInputStream(file);
        }

        String resourcePath = filePath.startsWith("/") ? filePath : "/" + filePath;
        InputStream is = VolunteerFileLoader.class.getResourceAsStream(resourcePath);
        if (is != null) {
            return is;
        }

        throw new IOException("Cannot locate file at " + filePath + " (user.dir: " + System.getProperty("user.dir") + ")");
    }
}