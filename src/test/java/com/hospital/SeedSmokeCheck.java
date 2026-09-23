package com.hospital;

import com.hospital.model.*;
import com.hospital.repository.*;

public class SeedSmokeCheck {
    public static void main(String[] args) {
        int patients = new PatientRepository().findAll().size();
        int doctors = new DoctorRepository().findAll().size();
        int staff = new StaffRepository().findAll().size();
        int appts = new AppointmentRepository().findAll().size();
        int emerg = new EmergencyCaseRepository().findAll().size();
        int wards = new WardRepository().findAll().size();
        int beds = new BedRepository().findAll().size();
        int meds = new MedicineRepository().findAll().size();
        int labs = new LabTestRepository().findAll().size();
        int mrc = new MedicalRecordRepository().findAll().size();
        int pre = new PrescriptionRepository().findAll().size();
        int bills = new BillRepository().findAll().size();
        int pays = new PaymentRepository().findAll().size();
        int adms = new AdmissionRepository().findAll().size();
        int ntf = new NotificationRepository().findAll().size();
        System.out.println("patients=" + patients + " doctors=" + doctors + " staff=" + staff
                + " appts=" + appts + " emergency=" + emerg + " wards=" + wards + " beds=" + beds
                + " medicines=" + meds + " labs=" + labs + " records=" + mrc + " prescriptions=" + pre
                + " bills=" + bills + " payments=" + pays + " admissions=" + adms + " notifications=" + ntf);
        if (patients < 10 || doctors < 5 || staff < 10 || appts < 10 || emerg < 3
                || wards < 5 || beds < 20 || meds < 15 || labs < 5 || ntf < 1) {
            System.err.println("SEED_INCOMPLETE");
            System.exit(1);
        }
        System.out.println("SEED_OK");
    }
}
