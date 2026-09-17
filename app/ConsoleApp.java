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
import service.AssignmentService;

<<<<<<< HEAD
=======
import java.io.File;
import java.util.List;
import java.util.Scanner;

>>>>>>> bf6923aacee9ccdb0a16571a25d1560a22d8beb8
public class ConsoleApp {

    private final Repository<Volunteer> volunteerRepo = new Repository<>();
    private final Repository<Doctor> doctorRepo = new Repository<>();
    private final Repository<CareTask> taskRepo = new Repository<>();
    private final Repository<Animal> animalRepo = new Repository<>();
    private final AssignmentService assignmentService = new AssignmentService();
    private final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        new ConsoleApp().run();
    }

    public void run() {
        loadAllData();

        while (true) {
            System.out.println("\n=== Animal Shelter Management System ===");
            System.out.println("1. Manager (Attribuer les tâches selon compétences)");
            System.out.println("2. Volontaire (Consulter et valider mes tâches)");
            System.out.println("3. Docteur (Mes interventions et actes médicaux)");
            System.out.println("4. Adopter un animal");
            System.out.println("5. Quitter");
            System.out.print("Choix : ");

            String choice = readLine();

            switch (choice) {
                case "1" -> runManagerMenu();
                case "2" -> runVolunteerMenu();
                case "3" -> runDoctorMenu();
                case "4" -> runAdoptionMenu();
                case "5" -> {
                    System.out.println("Fermeture de l'application.");
                    return;
                }
                default -> System.out.println("Choix invalide. Veuillez réessayer.");
            }
        }
    }

    // ---------------------------------------------------------------
    // 1. Manager Flow
    // ---------------------------------------------------------------

    private void runManagerMenu() {
        System.out.println("\n--- Attribution des tâches par le Manager ---");
        List<CareTask> unassigned = taskRepo.filter(t -> t.getStatus() == CareTaskStatus.UNASSIGNED);

        if (unassigned.isEmpty()) {
            System.out.println("Aucune tâche en attente d'assignation.");
            return;
        }

        System.out.println("Tâches disponibles :");
        for (CareTask t : unassigned) {
            System.out.println("- [" + t.getIdTask() + "] " + t.getDescription()
                    + " (Compétence/Spécialité requise : " + t.getRequiredSkill() + ")");
        }

        System.out.print("\nID de la tâche à assigner (ou laisser vide pour annuler) : ");
        String taskId = readLine();
        if (taskId.isBlank()) return;

        try {
            CareTask task = taskRepo.findById(taskId);

            System.out.println("Type de collaborateur à assigner :");
            System.out.println("1. Volontaire");
            System.out.println("2. Docteur");
            System.out.print("Choix : ");
            String targetType = readLine();

            if ("1".equals(targetType)) {
                System.out.print("ID du volontaire : ");
                String volId = readLine();
                Volunteer v = volunteerRepo.findById(volId);
                assignmentService.assignTaskToVolunteer(task, v);
                System.out.println("Succès : Tâche [" + task.getIdTask() + "] assignée à " + v.getNameVolunteer() + ".");
            } else if ("2".equals(targetType)) {
                System.out.print("ID du docteur : ");
                String docId = readLine();
                Doctor d = doctorRepo.findById(docId);
                assignmentService.assignTaskToDoctor(task, d);
                System.out.println("Succès : Tâche [" + task.getIdTask() + "] assignée au Dr. " + d.getName() + ".");
            } else {
                System.out.println("Option non reconnue.");
            }
        } catch (ShelterException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }

    // ---------------------------------------------------------------
    // 2. Volunteer Flow
    // ---------------------------------------------------------------

    private void runVolunteerMenu() {
        System.out.print("\nEntrez votre identifiant volontaire : ");
        String volunteerId = readLine();

        Volunteer volunteer;
        try {
            volunteer = volunteerRepo.findById(volunteerId);
        } catch (ShelterException e) {
            System.out.println("Erreur : " + e.getMessage());
            return;
        }

        System.out.println("Bonjour " + volunteer.getNameVolunteer() + " !");

        List<CareTask> myTasks = taskRepo.filter(t ->
                volunteer.getId().equals(t.getAssignedWorkerId()) && t.getStatus() == CareTaskStatus.ASSIGNED
        );

        if (myTasks.isEmpty()) {
            System.out.println("Vous n'avez actuellement aucune tâche assignée en attente.");
            return;
        }

        System.out.println("Vos tâches obligatoires assignées par le manager :");
        for (CareTask t : myTasks) {
            System.out.println("- [" + t.getIdTask() + "] " + t.getDescription());
        }

        System.out.print("\nEntrez l'ID de la tâche terminée pour la clôturer (ou vide) : ");
        String taskId = readLine();
        if (taskId.isBlank()) return;

        try {
            CareTask task = taskRepo.findById(taskId);
            if (!volunteer.getId().equals(task.getAssignedWorkerId())) {
                System.out.println("Erreur : Cette tâche ne vous a pas été attribuée.");
                return;
            }
            task.markCompleted();
            System.out.println("Tâche " + taskId + " complétée avec succès. Merci !");
        } catch (ShelterException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }

    // ---------------------------------------------------------------
    // 3. Doctor Flow
    // ---------------------------------------------------------------

    private void runDoctorMenu() {
        System.out.print("\nEntrez votre identifiant docteur : ");
        String docId = readLine();

        Doctor doctor;
        try {
            doctor = doctorRepo.findById(docId);
        } catch (ShelterException e) {
            System.out.println("Erreur : " + e.getMessage());
            return;
        }

        System.out.println("Bonjour Dr. " + doctor.getName() + " (" + doctor.getSpecialization() + ")");

        List<CareTask> assignedTasks = taskRepo.filter(t ->
                doctor.getId().equals(t.getAssignedWorkerId()) && t.getStatus() == CareTaskStatus.ASSIGNED
        );

        if (!assignedTasks.isEmpty()) {
            System.out.println("\nInterventions chirurgicales / soins assignés :");
            for (CareTask t : assignedTasks) {
                System.out.println("- [" + t.getIdTask() + "] " + t.getDescription());
            }
        }

        System.out.println("\nActions disponibles :");
        System.out.println("1. Enregistrer un traitement libre sur un animal");
        System.out.println("2. Valider une tâche assignée comme terminée");
        System.out.print("Choix : ");
        String choice = readLine();

        if ("1".equals(choice)) {
            System.out.print("ID de l'animal soigné : ");
            String aId = readLine();
            System.out.print("Description de l'acte / traitement : ");
            String desc = readLine();

            try {
                doctor.performTreatment(aId, desc);
                System.out.println("Traitement enregistré avec succès pour l'animal " + aId + ".");
            } catch (ShelterException e) {
                System.out.println("Erreur : " + e.getMessage());
            }
        } else if ("2".equals(choice)) {
            System.out.print("ID de la tâche terminée : ");
            String tId = readLine();
            try {
                CareTask t = taskRepo.findById(tId);
                if (!doctor.getId().equals(t.getAssignedWorkerId())) {
                    System.out.println("Erreur : Cette tâche n'a pas été assignée à votre compte.");
                    return;
                }
                t.markCompleted();
                System.out.println("Intervention " + tId + " validée comme terminée.");
            } catch (ShelterException e) {
                System.out.println("Erreur : " + e.getMessage());
            }
        }
    }

    // ---------------------------------------------------------------
    // 4. Adoption Flow
    // ---------------------------------------------------------------

    private void runAdoptionMenu() {
        List<Animal> eligible = animalRepo.filter(Animal::isAdoptionEligible);

        if (eligible.isEmpty()) {
            System.out.println("\nAucun animal n'est éligible à l'adoption pour le moment.");
            return;
        }

        System.out.println("\nAnimaux prêts à être adoptés :");
        for (Animal a : eligible) {
            System.out.println("- [" + a.getId() + "] " + a.getName()
                    + " (" + a.getClass().getSimpleName() + ", " + a.getAge() + " semaines)");
        }

        System.out.print("\nEntrez l'ID de l'animal à adopter (ou vide pour quitter) : ");
        String id = readLine();
        if (id.isBlank()) return;

        try {
            Animal a = animalRepo.findById(id);
            a.completeAdoption();
            System.out.println("Félicitations ! L'adoption de " + a.getName() + " est enregistrée.");
        } catch (ShelterException e) {
            System.out.println("Adoption refusée : " + e.getMessage());
        }
    }

    // ---------------------------------------------------------------
    // Helpers & Data Initialization
    // ---------------------------------------------------------------

    private String readLine() {
        return scanner.nextLine().trim();
    }

    private void loadAllData() {
<<<<<<< HEAD
        new AnimalFileLoader().loadFromFile("data/Animals.txt", animalRepo);
        new VolunteerFileLoader().loadFromFile("data/Volunteers.txt", volunteerRepo);
        new MedicalRecordFileLoader().loadFromFile("data/MedicalRecords.txt", animalRepo);
=======
        // Résolution robuste des fichiers de données (prend en compte majuscules et minuscules)
        String animalFile = resolvePath("data/Animals.txt");
        String volunteerFile = resolvePath("data/Volunteers.txt");
        String medicalFile = resolvePath("data/MedicalRecords.txt");
        String doctorFile = resolvePath("data/Doctors.txt");

        new AnimalFileLoader().loadFromFile(animalFile, animalRepo);
        new VolunteerFileLoader().loadFromFile(volunteerFile, volunteerRepo);
        new MedicalRecordFileLoader().loadFromFile(medicalFile, animalRepo);
>>>>>>> bf6923aacee9ccdb0a16571a25d1560a22d8beb8

        Doctor martin = new Doctor("DOC1", "Martin", "Chirurgie");
        doctorRepo.add(martin);

        taskRepo.add(new CareTask("T1", "Feed the dogs", "feeding"));
        taskRepo.add(new CareTask("T2", "Walk the cats", "walking"));
        taskRepo.add(new CareTask("T3", "Chirurgie patte cassée", "Chirurgie"));
    }

    private String resolvePath(String... candidates) {
        for (String candidate : candidates) {
            if (new File(candidate).exists()) {
                return candidate;
            }
        }
        return candidates[0];
    }
}