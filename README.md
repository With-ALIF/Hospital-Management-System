<div align="center">

<img src="logo.svg" alt="Hospital Management System Logo" width="320" />

# Hospital Management System
### Operations & Emergency Control Platform

**Full hospital operations on desktop: patients, doctors, staff, appointments, emergency triage, beds, records, prescriptions, pharmacy, lab, billing, reports — Java 21 + JavaFX.**

[![Java 21](https://img.shields.io/badge/Java-21%20LTS-ED8B00?style=flat-square&logo=openjdk&logoColor=white)](https://openjdk.org/projects/jdk/21/)
[![JavaFX](https://img.shields.io/badge/JavaFX-21.0.2-2563EB?style=flat-square&logo=javafx&logoColor=white)](https://openjfx.io/)
[![Maven](https://img.shields.io/badge/Maven-3.8+-C71A36?style=flat-square&logo=apache-maven&logoColor=white)](https://maven.apache.org/)
[![Jackson](https://img.shields.io/badge/Jackson-2.17.0-2E7D32?style=flat-square)](https://github.com/FasterXML/jackson)

</div>

---

## Overview

Hospital Management System is a production-style desktop admin system: register patients, manage doctors/staff, triage emergencies by medical priority, schedule appointments with conflict detection, track beds/wards, keep medical records and prescriptions, manage pharmacy stock and lab tests, bill patients, and audit every action — all persisted to zero-config JSON.

**Flow:** Public Dashboard (no login) → Login → role-based private Dashboard → protected modules. Dual interface: JavaFX desktop (public light / private dark Indigo) + scriptable CLI, shared service/repository layer.

## Features

| Module | Highlights |
|---|---|
| Public Dashboard | Aggregate stats only — no PHI, Login in header |
| Dashboard | Role KPI cards, emergency queue, today’s appointments |
| Patients | CRUD, search, status, profile with history |
| Emergency | Triage queue by severity×score, start/complete/cancel flow |
| Appointments | Booking rules, no double-booking, status transitions |
| Doctors / Staff | Directory, duty slots, availability, workload |
| Beds / Wards | Assign/transfer/discharge, occupancy by ward |
| Records / Rx | Medical records, prescriptions with items |
| Pharmacy / Lab | Medicine stock + expiry, lab status pipeline |
| Billing | Bills, payments (CASH/CARD/MOBILE_BANKING), status |
| Reports / Audit | Operational reports + audit logs + notifications |
| Auth / RBAC | SHA-256+salt, session, Login Required redirect |

IDs: `PAT-0001`, `DOC-0001`, `APT-0001`, `EMG-0001`, `STF-0001`, `WARD/BED/MRC/PRE/MED/LAB/BILL/PAY/ADM/NTF/LOG/ACC-0001`.

## Tech Stack

| Layer | Choice |
|---|---|
| Language | Java 21 LTS |
| UI | JavaFX 21.0.2 (code-based views, dark Indigo default) |
| Persistence | Jackson 2.17.0 + jackson-datatype-jsr310 |
| Build | Maven 3.8+ |
| Test | JUnit 3.8.1 + Surefire |

## Architecture

```
UI (views/components) → services (business rules) → repositories (JSON) → data/*.json
AppState wires services + observable lists; ThemeManager loads dark (default) / light CSS.
```

OOP: encapsulation in models, inheritance (Staff/User, repository base), polymorphism (Payable, Searchable, Reportable), interfaces across layers.

## Structure

```
pom.xml
data/*.json                          seed + runtime persistence
src/main/java/com/hospital/
  App.java  ConsoleApp.java  Main.java
  enums/ model/ repository/ service/ exception/ util/ storage/
  ui/ AppState ThemeManager components/ views/
src/main/resources/                  dark(default)/light × theme/surface/controls CSS
src/test/java/com/hospital/          Day3 + Day4 + Auth/RBAC test suites
```

## How to Run

```bash
java -version && mvn -version          # Java 21, Maven 3.8+

mvn clean compile javafx:run           # Opens Public Dashboard (light)
mvn compile exec:java -Pconsole        # CLI console
mvn clean package                      # JAR → target/
java -jar target/HospitalSystem-1.0-SNAPSHOT.jar

mvn clean test                         # Unit tests
```

Default credentials (seeded on first run):

| Role | Username | Password |
|---|---|---|
| ADMIN | `admin` | `Admin123` |
| DOCTOR | `doctor1` | `Doctor123` |
| NURSE | `nurse1` | `Nurse1234` |
| RECEPTIONIST | `reception1` | `Reception123` |
| PHARMACIST | `pharmacist1` | `Pharma123` |
| LAB_TECHNICIAN | `lab1` | `LabTech123` |

Existing accounts in `data/user_accounts.json` are kept (not overwritten). Passwords above work as-is (no forced change on first login). New/changed passwords must meet: min 8 chars, upper, lower, digit. Failed logins lock after 3 attempts (last active admin is never locked).

## Screenshots
**Screenshots** (after `mvn javafx:run`):
- Public Dashboard (white/blue, stats, departments, Login)
- Login → role Dashboard (KPI cards, emergency queue)
- Emergency triage (critical cases + queue)
- Appointments (filters + status badges + new booking modal)
- Settings (Appearance → Toggle Dark / Light)

## Data Persistence
JSON files under `data/` (patients, doctors, appointments, emergency cases, wards, beds, medicines, staff, lab tests, medical records, prescriptions, bills, payments, admissions, notifications). Loaded via Jackson `JavaTimeModule`; missing/corrupt files warn and start empty. Saves are automatic on service writes. Default accounts are created on first run.

## Future Improvements
- Persist dark/light theme choice to `settings.json`
- Barcode/QR patient wristband export; PDF bill/record export
- Real-time websocket dashboard; multi-branch hospital support

---

**Author:** Alif · MIT License
