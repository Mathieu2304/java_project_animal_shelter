package app;

import exception.ShelterException;
import java.util.List;
import java.util.Scanner;
import model.Animal;
import model.CareTask;
import model.CareTaskStatus;
import model.Doctor;
import model.Volunteer;
import repository.AnimalFileLoader;
import repository.MedicalRecordFileLoader;
import repository.Repository;
import repository.VolunteerFileLoader;

public class ConsoleApp {

    private final Repository<Volunteer> volunteerRepo = new Repository<>();
    private final Repository<Doctor> doctorRepo = new Repository<>();
    private final Repository<CareTask> taskRepo = new Repository<>();
    private final Repository<Animal> animalRepo = new Repository<>();
    private final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        new ConsoleApp().run();
    }

    public void run() {
        loadAllData(); // real Scanner-based file loading (Module 1)

        System.out.println("=== Animal Shelter Management System ===");
        System.out.println("Se connecter en tant que :");
        System.out.println("1. Volontaire");
        System.out.println("2. Docteur");
        System.out.println("3. Adopter un animal");
        System.out.print("Choix : ");

        String choice = readLine();

        switch (choice) {
            case "1" -> runVolunteerMenu();
            case "2" -> runDoctorMenu();
            case "3" -> runAdoptionMenu();
            default -> System.out.println("Choix invalide.");
        }
    }

    // ---------------------------------------------------------------
    // Volunteer flow
    // ---------------------------------------------------------------

    private void runVolunteerMenu() {
        System.out.print("Entrez votre id volontaire : ");
        String volunteerId = readLine();

        Volunteer volunteer;
        try {
            volunteer = volunteerRepo.findById(volunteerId);
        } catch (ShelterException e) {
            System.out.println("Erreur : " + e.getMessage());
            return;
        }

        System.out.println("Bonjour " + volunteer.getNameVolunteer() + " !");

        List<CareTask> myTasks = taskRepo.filter(
                task -> task.getStatus() == CareTaskStatus.UNASSIGNED
                        && volunteer.getSkills().contains(task.getRequiredSkill())
        );

        if (myTasks.isEmpty()) {
            System.out.println("Aucune tâche disponible correspondant à vos compétences pour le moment.");
            return;
        }

        System.out.println("Tâches disponibles pour vous :");
        for (CareTask task : myTasks) {
            System.out.println("- [" + task.getIdTask() + "] " + task.getDescription()
                    + " (compétence requise : " + task.getRequiredSkill() + ")");
        }

        System.out.print("Entrez l'id de la tâche à compléter (ou vide pour quitter) : ");
        String taskId = readLine();
        if (taskId.isBlank()) {
            return;
        }

        try {
            CareTask task = taskRepo.findById(taskId);
            task.markCompleted();
            System.out.println("Tâche " + taskId + " marquée comme terminée. Merci !");
        } catch (ShelterException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }

    // ---------------------------------------------------------------
    // Doctor flow
    // ---------------------------------------------------------------

    private void runDoctorMenu() {
        System.out.print("Entrez votre id docteur : ");
        String doctorId = readLine();

        Doctor doctor;
        try {
            doctor = doctorRepo.findById(doctorId);
        } catch (ShelterException e) {
            System.out.println("Erreur : " + e.getMessage());
            return;
        }

        System.out.println("Bonjour Dr. " + doctor.getName() + " (" + doctor.getSpecialization() + ")");
        System.out.print("Id de l'animal traité : ");
        String animalId = readLine();
        System.out.print("Description du traitement : ");
        String description = readLine();

        try {
            doctor.performTreatment(animalId, description);
            System.out.println("Traitement enregistré pour l'animal " + animalId + ".");
        } catch (ShelterException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }

    // ---------------------------------------------------------------
    // Adoption flow
    // ---------------------------------------------------------------

    private void runAdoptionMenu() {
        List<Animal> eligibleAnimals = animalRepo.filter(Animal::isAdoptionEligible);

        if (eligibleAnimals.isEmpty()) {
            System.out.println("Aucun animal n'est éligible à l'adoption pour le moment.");
            return;
        }

        System.out.println("Animaux disponibles à l'adoption :");
        for (Animal animal : eligibleAnimals) {
            System.out.println("- [" + animal.getId() + "] " + animal.getName()
                    + " (" + animal.getClass().getSimpleName() + ", " + animal.getAge() + " semaines)");
        }

        System.out.print("Id de l'animal que vous voulez adopter (ou vide pour quitter) : ");
        String animalId = readLine();
        if (animalId.isBlank()) {
            return;
        }

        try {
            Animal animal = animalRepo.findById(animalId);
            animal.completeAdoption();
            System.out.println("Félicitations, l'adoption de " + animal.getName() + " est confirmée !");
        } catch (ShelterException e) {
            System.out.println("Adoption refusée : " + e.getMessage());
        }
    }

    // ---------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------

    private String readLine() {
        return scanner.nextLine().trim();
    }

    /**
     * Real Module 1 loading: animals.txt and volunteers.txt via Scanner,
     * then medical_records.txt is applied on top of the already-loaded
     * animals. Doctors and CareTasks stay hardcoded for now — the project
     * only requires file loading for animals, volunteers, and medical
     * records.
     */
    private void loadAllData() {
        new AnimalFileLoader().loadFromFile("data/Animals.txt", animalRepo);
        new VolunteerFileLoader().loadFromFile("data/Volunteers.txt", volunteerRepo);
        new MedicalRecordFileLoader().loadFromFile("data/MedicalRecords.txt", animalRepo);

        Doctor martin = new Doctor("DOC1", "Martin", "Chirurgie");
        doctorRepo.add(martin);

        taskRepo.add(new CareTask("T1", "Feed the dogs", "feeding"));
        taskRepo.add(new CareTask("T2", "Walk the cats", "walking"));
        taskRepo.add(new CareTask("T3", "Medical check", "medical"));
    }
}