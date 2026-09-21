<div align="center">

# 🏥 Hospital Management & Emergency Operations System

### *Enterprise-Grade Java 21 Desktop & CLI Operations Platform*

An intuitive, high-performance Java 21 healthcare operations application designed for managing **patients, doctors, appointments, and critical emergency triage**. Features an asynchronous **JavaFX dark-theme desktop dashboard**, an **interactive CLI console client**, and a **fault-tolerant, auto-synchronizing JSON persistence engine** requiring zero external database configuration.

[![Java 21](https://img.shields.io/badge/Java-21%20LTS-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://openjdk.org/projects/jdk/21/)
[![JavaFX](https://img.shields.io/badge/JavaFX-21.0.2-2D7DD2?style=for-the-badge&logo=javafx&logoColor=white)](https://openjfx.io/)
[![Maven](https://img.shields.io/badge/Apache%20Maven-3.8+-C71A36?style=for-the-badge&logo=apache-maven&logoColor=white)](https://maven.apache.org/)
[![Jackson](https://img.shields.io/badge/Jackson%20JSON-2.17.0-2E7D32?style=for-the-badge)](https://github.com/FasterXML/jackson)


---

</div>

## 🌟 Key Highlights

- **Dual-Interface Flexibility**: Switch seamlessly between a modern **JavaFX Desktop Dashboard** with dynamic metric cards and a scriptable, menu-driven **Interactive CLI Console**.
- **Zero-Config File Persistence**: Instant read/write serialization to human-readable JSON files. Changes automatically persist immediately without manual "Save" actions.
- **Strict Business Logic Validation**: Guarantees zero double-booking, enforces doctor duty hours with slot parsing (`HH:mm-HH:mm`), and validates mandatory fields.
- **Priority-Weighted Emergency Triage**: Priority queue mechanism ranking patient urgency (`CRITICAL` > `HIGH` > `MEDIUM` > `LOW`) for emergency room triage.
- **Resilient Crash-Handling**: Safe startup initialization that self-heals empty, missing, or corrupted JSON files gracefully with warning logs.
- **Collision-Free Auto-IDs**: Centralized monotonic sequence generation (`P-1001`, `D-1001`, `E-1001`, `A-1001`) that avoids ID reuse even after deletions.

---

## 🚀 Core Modules

### 1. 🧑‍⚕️ Patient Management
* **Registration & Demographics**: Captures full name, contact number, gender, blood group (`A+`, `A-`, `B+`, `B-`, `AB+`, `AB-`, `O+`, `O-`), and emergency contact.
* **Smart ID Allocation**: Deterministic generation (`P-1001`, `P-1002`, ...) calculated from the highest existing index.
* **Instant Search & Filter**: Real-time filtering by ID, patient name, or blood group.

### 2. 👨‍⚕️ Doctor Directory & Duty Scheduling
* **Specialization Profiles**: Cataloging doctors by medical department (Cardiology, Neurology, Pediatrics, etc.).
* **Multi-Slot Working Hours**: Flexible time slot parser parsing custom schedules (e.g., `09:00-13:00, 15:00-18:00`).
* **Real-time Status Toggle**: Instant toggle between `Available` and `Busy` states reflected across GUI metric counters.

### 3. 📅 Smart Appointment Engine
* **Conflict Prevention**: Prohibits scheduling overlapping appointments for the same doctor.
* **Duty-Hour Enforcer**: Rejects any booking attempting to register outside the doctor's declared working hours.
* **Full Lifecycle Tracking**: Transition appointments through:
  $$\text{SCHEDULED} \longrightarrow \text{CONFIRMED} \longrightarrow \text{IN\_PROGRESS} \longrightarrow \text{COMPLETED}$$
  *(With support for `CANCELLED` and `NO_SHOW` terminal states)*.

### 4. 🚨 Emergency Room & Triage
* **Emergency Patient Linkage**: Instantly registers emergency cases linked to existing patient records.
* **Priority Queue Sorting**: Automatically organizes waiting patients using medical priority tiers:
  $$\text{CRITICAL} \succ \text{HIGH} \succ \text{MEDIUM} \succ \text{LOW}$$
* **Patient Journey Tracking**: Manages case status progression:
  $$\text{WAITING} \longrightarrow \text{IN\_TREATMENT} \longrightarrow \text{TREATED} \longrightarrow \text{DISCHARGED}$$

### 5. 🖥️ JavaFX Modern Dashboard
* **Sleek Dark Theme**: Styled with clean CSS variables and modern card-based layouts
* **Live KPI Metric Badges**: Header bar displaying real-time counts for Active Doctors, Available Doctors, Total Patients, and Scheduled Appointments.
* **Tabbed Navigation**: Instant tab switching between **Overview**, **Appointments**, **Doctors**, and **Patients**.

---

## ⚖️ Business Rules & Validation

| Domain Entity | Validation Rule | Behavioral Result |
| :--- | :--- | :--- |
| **Doctor Schedule** | Appointment time $\notin [\text{Slot}_{\text{start}}, \text{Slot}_{\text{end}}]$ | Throws `InvalidDataException` ("Appointment time is outside doctor duty hours.") |
| **Appointments** | Doctor already booked for the exact date & time | Throws `AppointmentConflictException` ("Doctor is already booked at this time.") |
| **Patients** | Blank or null patient name | Throws `InvalidDataException` with field rejection |
| **Emergency Triage** | Priority-ordered queue sorting | Automatically prioritizes `CRITICAL` cases to the head of the emergency queue |
| **Auto-Sync** | Any Create / Update / Delete operation | Immediate automatic flush to corresponding JSON file in `data/` |

---

## 🛠️ Tech Stack & Requirements

### Technical Specifications:
- **Language**: Java 21 (LTS)
- **GUI Toolkit**: JavaFX 21.0.2 (`javafx-controls`, `javafx-fxml`)
- **JSON Engine**: Jackson Databind 2.17.0 + Jackson JSR-310 (`jackson-datatype-jsr310`)
- **Build System**: Apache Maven 3.8+
- **Unit Testing**: JUnit 3.8+ / Maven Surefire

### Prerequisites:
Ensure **Java 21** and **Maven** are installed and configured:
```bash
java -version
mvn -version
```

---

## 💻 Running the System

### 1. Launch Modern JavaFX GUI
To launch the desktop graphical dashboard:
```bash
mvn clean compile javafx:run
```
*Alternatively, run with Maven exec:*
```bash
mvn exec:java
```

### 2. Launch Interactive CLI Console
For headless environments, automated demos, or terminal usage:
```bash
mvn compile exec:java -Pconsole
```

### 3. Package as Executable JAR
To produce a compiled distribution JAR in `target/`:
```bash
mvn clean package
```

---

## 🧪 Testing & Quality Assurance

The project includes an automated test suite verifying business rules, conflict resolution, and persistent data round-trips:

Run the entire test suite via:
```bash
mvn test
```

### Test Coverage Highlights:
- **`AppTest`**:
  - `testAppointmentWithinDutyHours`: Validates booking within doctor duty slot intervals.
  - `testAppointmentOutsideDutyHoursThrowsException`: Verifies rejection of out-of-schedule bookings.
- **`Day3PersistenceTest`**:
  - `testAutoSyncOnAdd/Update/Delete`: Confirms immediate JSON file write without explicit save invocations.
  - `testCorruptFileResilience`: Tests graceful fallback and error isolation on malformed JSON data.
  - `testPriorityQueueOrdering`: Validates triage priority ordering under concurrent admissions.

---

## 💾 Data Storage & Persistence

Data is stored as formatted, human-readable JSON within the [`data/`](file:///home/alif/Desktop/coding/HospitalSystem/data) folder.

<details>
<summary><b>Click to expand JSON schema examples</b></summary>

#### `patients.json`
```json
[
  {
    "id": "P-1001",
    "name": "Rahim Ahmed",
    "phone": "01811000000",
    "gender": "MALE",
    "bloodGroup": "O+",
    "emergencyContact": "01711000000"
  }
]
```

#### `doctors.json`
```json
[
  {
    "id": "D-1001",
    "name": "Dr. Sarah Khan",
    "phone": "01700000000",
    "gender": "FEMALE",
    "specialization": "Cardiology",
    "dutySlots": [
      { "startTime": "09:00:00", "endTime": "13:00:00" },
      { "startTime": "15:00:00", "endTime": "18:00:00" }
    ],
    "available": true
  }
]
```

#### `emergency_cases.json`
```json
[
  {
    "id": "E-1001",
    "patientId": "P-1001",
    "priority": "CRITICAL",
    "description": "Acute myocardial infarction",
    "status": "IN_TREATMENT"
  }
]
```

</details>

---

## 🗺️ Future Roadmap

- [ ] **Role-Based Access Control (RBAC)**: Dedicated login portals for Receptionists, Doctors, and Hospital Admins.
- [ ] **Prescription & Billing Generation**: Export medical prescriptions and invoices directly to PDF.
- [ ] **SMS/Email Notifications**: Automated reminders sent to patients prior to scheduled appointments.
- [ ] **Telemetry & Analytics**: Graphs and charts tracking daily admission statistics and peak emergency room hours.

---

## 📄 License & Attribution

This project is licensed under the **MIT License** — free for academic, instructional, and personal use.

*Developed with ❤️ using Java 21 & JavaFX.*