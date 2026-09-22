<div align="center">

<img src="logo.svg" alt="Hospital Management System Logo" width="360" />

# Hospital Management System
### Operations & Emergency Control Platform

**A professional, desktop-first hospital operations system for patient care, staff management, emergency triage and appointments — built with Java 21 + JavaFX.**

[![Java 21](https://img.shields.io/badge/Java-21%20LTS-ED8B00?style=flat-square&logo=openjdk&logoColor=white)](https://openjdk.org/projects/jdk/21/)
[![JavaFX](https://img.shields.io/badge/JavaFX-21.0.2-2563EB?style=flat-square&logo=javafx&logoColor=white)](https://openjfx.io/)
[![Maven](https://img.shields.io/badge/Maven-3.8+-C71A36?style=flat-square&logo=apache-maven&logoColor=white)](https://maven.apache.org/)
[![Jackson](https://img.shields.io/badge/Jackson-2.17.0-2E7D32?style=flat-square)](https://github.com/FasterXML/jackson)
[![License: MIT](https://img.shields.io/badge/License-MIT-F8FAFC?style=flat-square&color=0F172A)](LICENSE)

[Features](#-features) • [UI Preview](#-ui-design) • [Quick Start](#-quick-start) • [Architecture](#-architecture) • [Data](#-data--persistence) • [Tests](#-tests)

</div>

---

### Overview

Hospital Management System is a **real hospital admin system**, not a demo. It manages the full daily workflow: register patients, manage doctors & duty schedules, triage emergency cases by medical priority, and schedule appointments with conflict detection — all with **zero-config JSON persistence** that auto-syncs on every change.

Designed as **clean, minimal and data-focused** — easy to scan, consistent spacing, clear hierarchy. No gradients, no glassmorphism, no animations.

> **Dual Interface:** Modern **JavaFX Desktop (light professional)** + scriptable **CLI Console** — both share the same service & repository layer.

---

### ✨ Features

| Module | What it does |
|---|---|
| **Dashboard** | `Good Morning / Hospital Overview` + 4 KPI cards (Patients, Doctors Available, Emergency Waiting/Critical, Appointments Today) + Emergency Queue (priority → arrival) + Today's Appointments |
| **Patients** | Search + table `Patient ID / Name / Gender / Blood Group / Phone / Status` + `View/Edit` → profile dialog with tabs: *Profile / Appointments / Emergency Cases / History* |
| **Emergency** | **Triage-first** UI — `Critical Cases` (left red accent `3px #FECACA`) + `Emergency Queue` (`# · Patient · Priority · ID`) + actions `Start Treatment / Complete / Cancel`, `+ New Emergency Case` |
| **Appointments** | Filters `Search / Date / Doctor / Status` + table `Date / Time / Patient / Doctor / Reason / Status` (`SCHEDULED/CONFIRMED/COMPLETED/CANCELLED` badges) + `+ New Appointment` modal with validation |
| **Doctors** | Directory + `Search` + Availability `● Available #16A34A / ● Busy #D97706` + `Today's Schedule` + `View` |
| **Doctor Schedule** | Per-doctor timeline `08:00–18:00` half-hour rows — `Available` / `Appointment` (blue left border) / `Conflict` (red) / `Unavailable` (dimmed), legend included |
| **Reports / Settings** | Counts by status/priority, system info, theme & storage config |

**Core guarantees:**
- Collision-free IDs (`P-1001`, `D-1001`, `E-1001`, `A-1001`)
- Doctor duty parsing (`09:00-13:00`) + availability check
- No double-booking (doctor & patient, same `date+time`, `CANCELLED` excluded)
- Emergency `PriorityQueue` — `CRITICAL > HIGH > MEDIUM > LOW`, FIFO in same priority, auto doctor assignment
- Resilient JSON — missing/empty/corrupt file → warning + empty start, never crash

---

### 🎨 UI Design

**Direction:** Modern hospital admin — clean, professional, minimal, desktop-first.

```
┌──────────────────────────────────────────────────────────────┐
│ Sidebar (240px)  │ Top Header (56px)                         │
│                  ├──────────────────────────────────────────-┤
│  Hospital Mgmt.  │ Good Morning / Hospital Overview          │
│  Dashboard       │ [Patients 1,248] [Doctors 48] [Emergency] │
│  PATIENT CARE    │ Emergency Queue      | Today's Appts      │
│   Patients       │  CRITICAL Karim 10:21 Waiting            │
│   Emergency      │                                           │
│   Appointments   │                                           │
│  STAFF           │                                           │
│   Doctors        │                                           │
└──────────────────────────────────────────────────────────────┘
```

**Sidebar:** `Hospital Management / Operations System` (`H` mark `#2563EB`), nav `Dashboard`, `PATIENT CARE → Patients/Emergency/Appointments`, `STAFF → Doctors/Doctor Schedule`, `SYSTEM → Reports/Settings`, active `#EFF6FF + left 3px #2563EB`, bottom `System Status ● Operational`.

**Top Header:** `Search…` (`#F1F5F9`, focus `#2563EB`) + date `EEE, MMM d` + bell `◷` + `Admin / Administrator` + avatar `A`.

**Design Tokens:** `Primary #2563EB` `BG #F8FAFC` `Surface #FFFFFF` `Text #0F172A` `Secondary #64748B` `Border #E2E8F0` `Success #16A34A` `Warning #D97706` `Danger #DC2626` · Font `Inter / Segoe UI` · Radius `8/6px` · Spacing `8/12/16/24` · Table row `10px`.

> `src/main/resources/light-theme.css` — single source of truth, no inline gradients. `dark-theme.css` kept for reference.

---

### 🛠 Tech Stack

| Layer | Choice |
|---|---|
| Language | **Java 21 LTS** |
| UI | **JavaFX 21.0.2** (`controls`, `fxml`) — no FXML, code-based views |
| Persistence | **Jackson 2.17.0** + `jackson-datatype-jsr310` (`JavaTimeModule`, `WRITE_DATES_AS_TIMESTAMPS=false`, `INDENT_OUTPUT`) |
| Build | **Maven 3.8+** |
| Test | **JUnit 3.8.1** + Surefire |

**Requirements:** Java 21, Maven 3.8+
```bash
java -version
mvn -version
```

---

### 📁 Project Structure

```
Hospital-Management-System/
├── pom.xml
├── data/
│   ├── patients.json
│   ├── doctors.json
│   ├── emergency_cases.json
│   └── appointments.json
└── src/main/java/com/hospital/
    ├── App.java                      # Sidebar + TopBar + content router (light-theme.css)
    ├── ConsoleApp.java               # CLI — 1..23 menu
    ├── model/                        # Patient, Doctor, TimeSlot, Appointment, EmergencyCase + enums
    ├── repository/                   # Repository<T>, AbstractJsonRepository<T>, Patient/Doctor/Emergency/AppointmentRepository
    ├── service/                      # PatientService, DoctorService, EmergencyService, AppointmentService, HospitalService
    ├── storage/                      # JsonStorage
    ├── exception/                    # InvalidDataException
    └── ui/
        ├── AppState.java             # ObservableLists + layered services
        ├── components/ SidebarView, TopBarView, StatCard, BadgeFactory, EmptyState
        └── views/ Dashboard, Patients, Doctors, Emergency, Appointments, DoctorSchedule, Reports, Settings
```

---

### 🚀 Quick Start

```bash
# 1. Desktop — light professional dashboard
mvn clean compile javafx:run
# or
mvn exec:java

# 2. CLI — headless / demo
mvn compile exec:java -Pconsole

# 3. Jar
mvn clean package
java -jar target/HospitalSystem-1.0-SNAPSHOT.jar
```

---