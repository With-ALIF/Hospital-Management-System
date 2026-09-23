package com.hospital.repository;

import java.util.Map;

public interface Reportable {
    String reportTitle();

    Map<String, Object> generateReport();
}
