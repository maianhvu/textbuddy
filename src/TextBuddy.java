/**
 * TextBuddy.java
 * Copyright (c) 2016 Mai Anh Vu
 *
 * This is the default application class.
 */
public class TextBuddy {

    /**
     * Constants
     */
    private static final int ID_ARGUMENT_FILE_PATH = 0;
    private static final String STRING_USAGE = "Usage: java TextBuddy [filepath]";

    public static void main(String[] args) {
        if (!verifyArguments(args)) {
            printUsage();
            return;
        }

        Application app = new Application(args[ID_ARGUMENT_FILE_PATH]);
        app.run();
    }

    /**
     * Check if the arguments passed from the command line is valid
     * @param args arguments used to start programme
     * @return whether the arguments are valid
     */
    private static boolean verifyArguments(String[] args) {
        return args.length > ID_ARGUMENT_FILE_PATH;
    }

    /**
     * Prints the usage string to standard output
     */
    private static void printUsage() {
        System.out.println(STRING_USAGE);
    }

}