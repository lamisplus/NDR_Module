package org.lamisplus.modules.ndr.mapper;

public enum RecencyInterpretation {
    RECENT("R"),
    LONG_TERM("L"),
    NEGATIVE("Neg"),
    INVALID("Inv");
    private final String value;

    RecencyInterpretation(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    }
