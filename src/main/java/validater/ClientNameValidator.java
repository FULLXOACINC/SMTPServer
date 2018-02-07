package validater;

import java.io.File;
import java.util.regex.Pattern;

public class ClientNameValidator {
    private final static String PATH = "post";
    private final static String REGEX_DOMEN_NAME = "\\<([A-Z]|[a-z]|[0-9])+@([A-Z]|[a-z])+\\.([A-Z]|[a-z])+\\>";

    public static boolean isClientExist(String name) {
        File file = new File(PATH + "/" + name);
        return file.exists() && file.isDirectory();

    }

    public static boolean validateParams(String data) {
        Pattern pattern = Pattern.compile(REGEX_DOMEN_NAME);
        return pattern.matcher(data).matches();
    }
}