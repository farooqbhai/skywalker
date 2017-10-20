package org.speechbot.raw;

import java.util.Arrays;
import java.util.List;

public class Constants {

    private static int RECORD_DURATION_IN_SEC = 6;
    
    public static int RECORD_DURATION_IN_MS = RECORD_DURATION_IN_SEC * 1000;
    
    public static String RECORDED_FILES_DIR = "RECORDED_FILES_DIR";
    
    public static String GOOGLE_APPLICATION_CREDENTIALS = "GOOGLE_APPLICATION_CREDENTIALS";
    
    public static List<String> mandatoryEnv = Arrays.asList(GOOGLE_APPLICATION_CREDENTIALS, RECORDED_FILES_DIR);
}
