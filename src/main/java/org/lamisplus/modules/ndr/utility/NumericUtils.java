package org.lamisplus.modules.ndr.utility;

public class NumericUtils {

    public static boolean isNumeric(String value) {
        try {
            Double.parseDouble(value);
            return true;
        }catch (Exception e) {
            return false;
        }
    }

    private NumericUtils(){

    }
}
