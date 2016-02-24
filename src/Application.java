/**
 * Application.java
 * Copyright (c) 2016 Mai Anh Vu
 */

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * An abstraction of the TextBuddy application that pieces together all the other
 * abstraction classes (Command, Display, TextFile) to build the main application logic handler.
 * The Application instance will first prompt the commands from the Display interface, and use
 * the Command abstraction to parse the command, and finally commit the changes to the TextFile
 * object.
 */
public class Application {

    /**
     * Constants
     */
    private static final Error ERROR_LINE_NUMBER_INVALID = new Error("Invalid line number");
    private static final Error ERROR_MISSING_SEARCH_QUERY = new Error("Search query missing");

    private static final Comparator<Integer> COMPARATOR_LINE_NUMBERS = new LineNumberComparator();

    private static final String STRING_PROMPT_COMMAND = "command: ";

    private static final String STRING_ERROR_FILE_READ = "Cannot open file for reading";
    private static final String STRING_ERROR_FILE_SAVE = "Cannot save to file";
    private static final String STRING_ERROR_COMMAND_UNRECOGNISED = "Unrecognised command";

    private static final String STRING_DELIMITER_SEARCH_RESULTS = ", ";
    private static final String STRING_DELIMITER_SEARCH_QUERIES = "\\s+";
    private static final String STRING_CONNECTIVE_LAST_SEARCH_RESULT = "and ";

    private static final String STRING_FORMAT_MESSAGE_WELCOME = "Welcome to TextBuddy. %1$s is ready for use";
    private static final String STRING_FORMAT_INFO_SEARCH_NOT_FOUND = "No occurrences of %1$s found in %2$s";
    private static final String STRING_FORMAT_SUCCESS_SEARCH_FOUND = "Found %1$s in %2$s on lines %3$s";
    private static final String STRING_FORMAT_SUCCESS_SORT = "All lines in %1$s are sorted in alphabetical order";
    private static final String STRING_FORMAT_SUCCESS_CLEAR = "Cleared all lines from %1$s";
    private static final String STRING_FORMAT_SUCCESS_DELETE = "Deleted from %1$s: %2$s";
    private static final String STRING_FORMAT_SUCCESS_ADD = "Added new line to %1$s: %2$s";
    private static final String STRING_FORMAT_SUCCESS_ADD_EMPTY = "Added an empty line to %1$s";

    /**
     * Properties
     */
    private final Display display_;
    private TextFile textFile_;

    /**
     * Constructs a TextBuddy application editing the file located at the
     * path specified.
     * @param filePath path to the file to edit
     */
    public Application(String filePath) {
        this.display_ = new Display();
        this.initializeTextFile(filePath);
    }

    /**
     * Attempts to create a text file with the path specified.
     * @param filePath path to the file to initialize
     */
    private void initializeTextFile(String filePath) {
        try {
            this.textFile_ = new TextFile(filePath);
        } catch (IOException e) {
            this.display_.error(STRING_ERROR_FILE_READ);
        } catch (Error e) {
            this.display_.error(e);
        }
    }

    /**
     * Executes the application.
     */
    public void run() {
        // Shows the welcome message containing the path to the file editing
        this.display_.line(STRING_FORMAT_MESSAGE_WELCOME,
                this.textFile_.getFilePath());

        this.enterProgrammeLoop();
        this.cleanUp();
    }

    /**
     * Cleans up the application before closing. Normally entails saving the
     * file one last time.
     */
    private void cleanUp() {
        try {
            this.textFile_.save();
        } catch (IOException e) {
            this.display_.error(STRING_ERROR_FILE_SAVE);
        }
    }

    /**
     * Keeps reading in commands ane executing them until a
     * terminate (exit) command is input by the user.
     */
    private void enterProgrammeLoop() {
        boolean quit = false;
        while (!quit) {

            // Reads in command, then try to detect the instruction and parameters
            String rawCommand = this.display_.promptLine(STRING_PROMPT_COMMAND);
            Command command = Command.interpret(rawCommand);

            switch (command.getType()) {
                case UNRECOGNISED:
                    // Skip unrecognised commands
                    this.display_.error(STRING_ERROR_COMMAND_UNRECOGNISED);
                    continue;
                case EXIT:
                    // Flag for exit
                    quit = true;
                    break;
                default:
                    // Executes the valid command, logging out any errors caught
                    try {
                        this.execute(command);
                    } catch (Error e) {
                        this.display_.error(e);
                    }
            }
        }
    }

    /**
     * Performs appropriate actions for the command instruction and parameter
     * interpreted.
     * @param command a command to execute
     * @throws Error error thrown when parameter is invalid
     */
    private void execute(Command command) throws Error {
        switch (command.getType()) {
            case ADD:
                executeAdd(command);
                break;
            case DISPLAY:
                executeDisplay();
                break;
            case DELETE:
                executeDelete(command);
                break;
            case CLEAR:
                executeClear();
                break;
            case SORT:
                executeSort();
                break;
            case SEARCH:
                executeSearch(command);
                break;
        }
    }

    /**
     * Executes the add instruction, then logs the success message
     * using the display helper
     * @param command a command object containing the parameter, which serves
     *                as the line content to be added
     */
    private void executeAdd(Command command) {
        String lineToAdd = command.getParameter();
        String messageFormat = STRING_FORMAT_SUCCESS_ADD;

        if (lineToAdd == null || lineToAdd.equals("")) {
            lineToAdd = "";
            messageFormat = STRING_FORMAT_SUCCESS_ADD_EMPTY;
        }

        this.textFile_.addLine(lineToAdd);
        this.display_.success(
                String.format(messageFormat, this.textFile_.getFilePath(), lineToAdd)
        );
    }

    /**
     * Shows a list of lines to the user using the display helper.
     */
    private void executeDisplay() {
        this.display_.orderedList(
                this.textFile_.getAllLines()
        );
    }

    /**
     * Executes the delete instruction, logging the success and any
     * error that occured during execution.
     * @param command a command object containing the parameter, which serves
     *                as the line number to be deleted
     * @throws Error
     */
    private void executeDelete(Command command) throws Error {
        if (command.getParameter() == null) {
            throw ERROR_LINE_NUMBER_INVALID;
        }

        Integer deleteIndex = null;
        try {
            deleteIndex = Integer.parseInt(command.getParameter());
        } catch (NumberFormatException e) {
            throw ERROR_LINE_NUMBER_INVALID;
        }

        deleteIndex -= 1; // Get real index
        String deletedLine = this.textFile_.removeLine(deleteIndex);

        if (deletedLine == null) {
            throw ERROR_LINE_NUMBER_INVALID;
        }

        this.display_.success(
                String.format(STRING_FORMAT_SUCCESS_DELETE, this.textFile_.getFilePath(), deletedLine)
        );
    }

    /**
     * Executes the clear instruction, then logs the success afterwards.
     */
    private void executeClear() {
        this.textFile_.clearAllLines();
        this.display_.success(
                String.format(STRING_FORMAT_SUCCESS_CLEAR, this.textFile_.getFilePath())
        );
    }

    /**
     * Executes the sort instruction, then logs the success afterwards.
     */
    private void executeSort() {
        this.textFile_.sortLines();
        this.display_.success(
                String.format(STRING_FORMAT_SUCCESS_SORT, this.textFile_.getFilePath())
        );
    }

    /**
     * Executes the search instruciton, then print the search result to the user using
     * the display helper. Also logs any error during execution.
     * @param command a command object containing the parameter, which serves
     *                as the query for searching
     * @throws Error error thrown when query is missing from the command
     */
    private void executeSearch(Command command) throws Error {
        if (command.getParameter() == null) {
            throw ERROR_MISSING_SEARCH_QUERY;
        }

        // Split the parameter into multiple search queries
        String[] query = command.getParameter()
                .trim().split(STRING_DELIMITER_SEARCH_QUERIES);

        if (query.length == 0) {
            throw ERROR_MISSING_SEARCH_QUERY;
        }

        // Invoke the search method on the text file, which then return a set
        // of the lines in which the query was successful
        Set<Integer> searchResults = this.textFile_.searchFor(query[0]);

        // In case where there was no results found
        if (searchResults == null) {
            this.display_.info(
                    String.format(STRING_FORMAT_INFO_SEARCH_NOT_FOUND,
                            query[0],
                            this.textFile_.getFilePath())
            );
        }
        // When results are found, logs the appropriate information
        else {

            // The line numbers contained in the search results are from
            // a set and therefore is not guaranteed to be in any order.
            // We must then sort these numbers first
            List<Integer> lineNumbers = new ArrayList<>(searchResults);
            lineNumbers.sort(COMPARATOR_LINE_NUMBERS);

            // Build a list of the line numbers
            StringBuilder lineNumbersString = new StringBuilder();

            for (int i = 0; i < lineNumbers.size(); i++) {

                // This is only for human-readability of the search result,
                // the actual application does not depend on this
                if (i != 0) {
                    // Insert a comma before if not the first line number in the list
                    lineNumbersString.append(STRING_DELIMITER_SEARCH_RESULTS);

                    // Insert an 'and' word if is the last line number in the list
                    // and that it is not the only entry
                    if (i == lineNumbers.size() - 1) {
                        lineNumbersString.append(STRING_CONNECTIVE_LAST_SEARCH_RESULT);
                    }
                }

                // Append with the actual line number readable by human, which is
                // the line index plus 1.
                lineNumbersString.append(lineNumbers.get(i) + 1);
            }

            // Log success message with the line numbers via the display helper
            this.display_.success(
                    String.format(STRING_FORMAT_SUCCESS_SEARCH_FOUND,
                            query[0],
                            this.textFile_.getFilePath(),
                            lineNumbersString)
            );

            // Map the result lines to their respective line numbers
            List<Line> resultLines = lineNumbers.stream().map(
                    lineNumber -> this.textFile_.getLineAt(lineNumber)
            ).collect(Collectors.toList());

            // Display these lines out to the screen eventually
            this.display_.lines(resultLines);
        }
    }

    /**
     * Comparator class used to sort lines according to their line number (ascending)
     */
    private static class LineNumberComparator implements Comparator<Integer> {
        @Override
        public int compare(Integer o1, Integer o2) {
            return o1 - o2;
        }
    }
}
