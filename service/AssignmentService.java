package service;

import exception.ShelterException;
import model.CareTask;
import model.Doctor;
import model.Volunteer;

public class AssignmentService {

    public void assignTaskToVolunteer(CareTask task, Volunteer volunteer) {
        if (volunteer == null) {
            throw new ShelterException("Volunteer cannot be null.");
        }
        if (!volunteer.getSkills().contains(task.getRequiredSkill())) {
            throw new ShelterException("Volunteer " + volunteer.getNameVolunteer() 
                    + " lacks the required skill: " + task.getRequiredSkill());
        }
        task.assignTo(volunteer.getId());
    }

    public void assignTaskToDoctor(CareTask task, Doctor doctor) {
        if (doctor == null) {
            throw new ShelterException("Doctor cannot be null.");
        }
        if (!doctor.getSpecialization().equalsIgnoreCase(task.getRequiredSkill())) {
            throw new ShelterException("Dr. " + doctor.getName() 
                    + " lacks the required specialization: " + task.getRequiredSkill());
        }
        task.assignTo(doctor.getId());
    }
}