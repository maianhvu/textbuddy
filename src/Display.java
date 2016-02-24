/**
 * Display.java
 * Copyright (c) 2016 Mai Anh Vu
 */
import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;

/**
 * A display helper interface to handle all I/O operations inside the application.
 * This class can either be constructed to use default I/O channels (System.in and
 * System.out) or to use alternative streams:
 *      void printThings() {
 *          Display display = new Display(inputStream, outputStream);
 *          ...
 *      }
 * This helper interface contains alias methods to print out messages, and also contains
 * convenient methods to log different levels of output: info, success, and errors.
 */
public class Display {

    /**
     * Constants
     */
    private static final String STRING_FORMAT_ORDERED_LIST_LINE = "%d. %s";

    private static final InputStream STREAM_INPUT_DEFAULT = System.in;
    private static final PrintStream STREAM_OUTPUT_DEFAULT = System.out;

    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_CYAN = "\u001B[36m";

    private static final String COLOR_ERROR = ANSI_RED;
    private static final String COLOR_SUCCESS = ANSI_GREEN;
    private static final String COLOR_INFO = ANSI_CYAN;

    private static final String FLAG_ERROR = "ERROR: ";
    private static final String FLAG_SUCCESS = "DONE: ";
    private static final String FLAG_INFO = "INFO: ";
    private static final String STRING_NEW_LINE = "\n";

    /**
     * Properties
     */
    private final Scanner inputScanner_;
    private final PrintStream outputPrinter_;

    /**
     * Constructs a display helper that handles I/O operations on the specified
     * input and output streams.
     * @param inputStream the stream to read input from. Defaults to <b>System.in</b>
     * @param outputStream the stream to write output to. Defaults to <b>System.out</b>
     */
    public Display(InputStream inputStream, PrintStream outputStream) {
        this.inputScanner_ = new Scanner(inputStream);
        this.outputPrinter_ = outputStream;
    }

    /**
     * Constructs a display helper that handles I/O operations using standard I/O
     * streams.
     */
    public Display() {
        this(STREAM_INPUT_DEFAULT, STREAM_OUTPUT_DEFAULT);
    }

    /**
     * Prints out the prompt message and return the next line
     * read from the input.
     * @param prompt a command prompt shown to user
     * @return the next line read from input
     */
    public String promptLine(String prompt) {
        this.text(prompt);
        return this.inputScanner_.nextLine();
    }

    /**
     * Prints out the string to the current output stream.
     * @param string the string to be printed
     */
    public void text(String string) {
        this.outputPrinter_.print(string);
    }

    /**
     * Prints out the string with a new line to the current output stream.
     * @param string the string to be printed
     */
    public void line(String string) {
        this.outputPrinter_.println(string);
    }

    /**
     * Prints out the string formed using the format and the arguments given.
     * @param format a format string
     * @param args arguments to be used by the format string
     */
    public void text(String format, Object... args) {
        this.outputPrinter_.printf(format, args);
    }

    /**
     * Prints out the string formed using the format and the arguments given, with
     * an extra new line character.
     * @param format a format string
     * @param args arguments to be used by the format string
     */
    public void line(String format, Object... args) {
        format = format + STRING_NEW_LINE;
        this.text(format, args);
    }

    /**
     * Logs the error message to the output stream.
     * @param err an error message
     */
    public void error(String err) {
        this.line("%s%s%s%s", COLOR_ERROR, FLAG_ERROR, ANSI_RESET, err);
    }

    /**
     * Logs the error message of the Throwable object to the output stream.
     * @param e a Throwable object
     */
    public void error(Throwable e) {
        this.error(e.getMessage());
    }

    /**
     * Logs the success of a task to the output stream.
     * @param task a task that succeeded
     */
    public void success(String task) {
        this.line("%s%s%s%s", COLOR_SUCCESS, FLAG_SUCCESS, ANSI_RESET, task);
    }

    /**
     * Logs additional information to the output stream.
     * @param information info to be printed
     */
    public void info(String information) {
        this.line("%s%s%s%s", COLOR_INFO, FLAG_INFO, ANSI_RESET, information);
    }

    /**
     * Prints the list of string as a numbered list (first index starts from 1).
     * @param lines a list containing the Strings to be printed
     */
    public void orderedList(List<String> lines) {
        for (int i = 0; i < lines.size(); i++) {
            this.line(STRING_FORMAT_ORDERED_LIST_LINE,
                    i + 1,
                    lines.get(i));
        }
    }

    /**
     * Prints the Line objects (the line number followed by content) to the output stream.
     * @param lines a list containing the Lines to be printed
     */
    public void lines(List<Line> lines) {
        for (Line line : lines) {
            this.line(STRING_FORMAT_ORDERED_LIST_LINE,
                    line.getDisplayNumber(),
                    line.getContent());
        }
    }

}
