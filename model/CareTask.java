package model;

import contract.Identifiable;
import exception.ShelterException;

public class CareTask implements Identifiable {
    private final String idTask;
    private final String description;
    private final String requiredSkill;
    private CareTaskStatus status;
    private String assignedWorkerId; // Stocke l'ID du Volunteer ou du Doctor

    public CareTask(String idTask, String description, String requiredSkill) {
        if (idTask == null || idTask.isBlank()) {
            throw new ShelterException("Task ID is required.");
        }
        if (description == null || description.isBlank()) {
            throw new ShelterException("Task description is required.");
        }
        if (requiredSkill == null || requiredSkill.isBlank()) {
            throw new ShelterException("Required skill is required.");
        }

        this.idTask = idTask;
        this.description = description;
        this.requiredSkill = requiredSkill;
        this.status = CareTaskStatus.UNASSIGNED;
        this.assignedWorkerId = null;
    }

    // Contrat imposé par Identifiable pour fonctionner avec Repository<CareTask>
    @Override
    public String getId() {
        return idTask;
    }

    public String getIdTask() {
        return idTask;
    }

    public String getDescription() {
        return description;
    }

    public String getRequiredSkill() {
        return requiredSkill;
    }

    public CareTaskStatus getStatus() {
        return status;
    }

    public String getAssignedWorkerId() {
        return assignedWorkerId;
    }

    /**
     * Assigne la tâche à un volontaire ou docteur via son identifiant unique.
     */
    public void assignTo(String workerId) {
        if (this.status == CareTaskStatus.COMPLETED) {
            throw new ShelterException("Cannot reassign a completed task.");
        }
        if (workerId == null || workerId.isBlank()) {
            throw new ShelterException("Worker ID cannot be empty.");
        }
        this.assignedWorkerId = workerId;
        this.status = CareTaskStatus.ASSIGNED;
    }

    /**
     * Valide l'achèvement de la tâche selon l'invariant d'état.
     */
    public void markCompleted() {
        if (this.status != CareTaskStatus.ASSIGNED) {
            throw new ShelterException("Only assigned tasks can be marked as completed.");
        }
        this.status = CareTaskStatus.COMPLETED;
    }

    @Override
    public String toString() {
        return "[" + idTask + "] " + description + " (Skill: " + requiredSkill 
                + ", Status: " + status + (assignedWorkerId != null ? ", Assigned to: " + assignedWorkerId : "") + ")";
    }
}