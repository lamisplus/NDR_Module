package org.lamisplus.modules.ndr.mapper;

public enum RecencyStatus {
    RITA_RECENT("RR"),
    RITA_LONG_TERM("RL"),
    RITA_INCONCLUSIVE("RI");

    private final String value;

    RecencyStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
