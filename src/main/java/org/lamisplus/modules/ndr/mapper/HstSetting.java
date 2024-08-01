package org.lamisplus.modules.ndr.mapper;

public enum HstSetting {
    TEST_SETTING_CT("1"),
    TEST_SETTING_TB("2"),
    TEST_SETTING_STI("3"),
    TEST_SETTING_FP("4"),
    TEST_SETTING_OPD("5"),
    TEST_SETTING_WARD("6"),
    TEST_SETTING_OUTREACH("7"),
    TEST_SETTING_OTHERS("8"),
    TEST_SETTING_STANDALONE_HTS("9");
    private final String value;

    HstSetting(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
