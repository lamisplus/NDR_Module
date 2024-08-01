package org.lamisplus.modules.ndr.mapper;

public enum RecencyTestName {
    ASANTE("AS"),
    OTHERS("OTH");

    private final String value;

    RecencyTestName(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
    }
