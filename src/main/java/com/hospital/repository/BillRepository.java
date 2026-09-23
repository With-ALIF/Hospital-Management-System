package com.hospital.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hospital.model.Bill;

import java.util.List;

public class BillRepository extends AbstractJsonRepository<Bill> {
    public static final String DEFAULT_FILE = "data/bills.json";

    public BillRepository() { this(DEFAULT_FILE); }
    public BillRepository(String filePath) {
        super(filePath, new TypeReference<List<Bill>>() {}, "Bill");
    }

    @Override
    protected String idOf(Bill entity) { return entity.getId(); }

    public String nextBillId() { return nextId("BILL-", 1); }

    public List<Bill> findByPatient(String patientId) {
        return findAll().stream()
                .filter(b -> patientId != null && patientId.equals(b.getPatientId())).toList();
    }
}
