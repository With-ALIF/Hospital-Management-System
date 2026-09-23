package com.hospital.ui;

import com.hospital.model.*;
import com.hospital.service.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class AppState {
    public final HospitalService service = new HospitalService();
    public final PatientService patientService = new PatientService();
    public final DoctorService doctorService = new DoctorService();
    public final EmergencyService emergencyService = new EmergencyService(patientService, doctorService);
    public final AppointmentService appointmentService = new AppointmentService(patientService, doctorService);
    public final BedService bedService = new BedService();
    public final MedicalRecordService medicalRecordService = new MedicalRecordService(patientService,
            com.hospital.repository.MedicalRecordRepository.DEFAULT_FILE);
    public final PrescriptionService prescriptionService = new PrescriptionService();
    public final PharmacyService pharmacyService = new PharmacyService();
    public final LabService labService = new LabService(patientService,
            com.hospital.repository.LabTestRepository.DEFAULT_FILE);
    public final BillingService billingService = new BillingService();
    public final AdmissionService admissionService = new AdmissionService(bedService, patientService,
            com.hospital.repository.AdmissionRepository.DEFAULT_FILE,
            com.hospital.repository.DischargeRepository.DEFAULT_FILE);
    public final NotificationService notificationService = new NotificationService();
    public final AuditLogService auditLogService = new AuditLogService();
    public final StaffService staffService = new StaffService();
    public final AuditLogService authAudit = auditLogService;
    public final AuthenticationService authenticationService =
            new AuthenticationService(com.hospital.repository.UserAccountRepository.DEFAULT_FILE,
                    auditLogService);
    public final PermissionService permissionService = PermissionService.getInstance();
    public final SessionManager sessionManager = SessionManager.getInstance();
    public final RiskScoreService riskScoreService = new RiskScoreService();
    public final WorkloadService workloadService = new WorkloadService();
    public final ReportService reportService = new ReportService();

    public final BloodBankService bloodBankService = new BloodBankService();
    public final AmbulanceService ambulanceService = new AmbulanceService();
    public final OperationService operationService = new OperationService();
    public final VitalMonitoringService vitalMonitoringService = new VitalMonitoringService();
    public final FollowUpService followUpService = new FollowUpService();
    public final EquipmentService equipmentService = new EquipmentService();
    public final StaffShiftService staffShiftService = new StaffShiftService();
    public final BackupService backupService = new BackupService();
    public final AdvancedSearchService advancedSearchService = new AdvancedSearchService();
    public final AdvancedReportService advancedReportService = new AdvancedReportService();
    public final EventBus eventBus = EventBus.getInstance();

    public final ObservableList<Doctor> doctors = FXCollections.observableArrayList();
    public final ObservableList<Patient> patients = FXCollections.observableArrayList();
    public final ObservableList<Appointment> appointments = FXCollections.observableArrayList();
    public final ObservableList<EmergencyCase> emergencyCases = FXCollections.observableArrayList();

    public Runnable onMetricsChanged;

    public AppState() {
        loadDataFromService();
    }

    public void notifyChange() {
        if (onMetricsChanged != null) onMetricsChanged.run();
    }

    private void loadDataFromService() {
        doctors.addAll(service.getDoctors());
        patients.addAll(service.getPatients());
        appointments.addAll(service.getAppointments());
        try { emergencyCases.addAll(emergencyService.getAllCases()); } catch (Exception ignored) {}
    }

    public void refreshFromService() {
        service.loadAllData();
        doctors.setAll(service.getDoctors());
        patients.setAll(service.getPatients());
        appointments.setAll(service.getAppointments());
        try { emergencyCases.setAll(emergencyService.getAllCases()); } catch (Exception ignored) {}
        notifyChange();
    }

    public void refreshEmergency() {
        try { emergencyCases.setAll(emergencyService.getAllCases()); } catch (Exception ignored) {}
        notifyChange();
    }

    public void refreshAppointments() {
        try { appointments.setAll(appointmentService.getAllAppointments()); } catch (Exception ignored) {}
        service.loadAllData();
        notifyChange();
    }
}
