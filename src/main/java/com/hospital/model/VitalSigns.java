package com.hospital.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VitalSigns {
    private int heartRate;
    private int systolicBp;
    private int diastolicBp;
    private int oxygenSaturation;
    private double temperature;

    public VitalSigns() {
    }

    public VitalSigns(int heartRate, int systolicBp, int diastolicBp, int oxygenSaturation, double temperature) {
        this.heartRate = heartRate;
        this.systolicBp = systolicBp;
        this.diastolicBp = diastolicBp;
        this.oxygenSaturation = oxygenSaturation;
        this.temperature = temperature;
    }

    public int getHeartRate() { return heartRate; }
    public void setHeartRate(int heartRate) { this.heartRate = heartRate; }
    public int getSystolicBp() { return systolicBp; }
    public void setSystolicBp(int systolicBp) { this.systolicBp = systolicBp; }
    public int getDiastolicBp() { return diastolicBp; }
    public void setDiastolicBp(int diastolicBp) { this.diastolicBp = diastolicBp; }
    public int getOxygenSaturation() { return oxygenSaturation; }
    public void setOxygenSaturation(int oxygenSaturation) { this.oxygenSaturation = oxygenSaturation; }
    public double getTemperature() { return temperature; }
    public void setTemperature(double temperature) { this.temperature = temperature; }

    public boolean hasData() {
        return heartRate > 0 || systolicBp > 0 || oxygenSaturation > 0 || temperature > 0;
    }
}
