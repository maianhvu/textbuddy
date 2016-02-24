/**
 * TextFile.java
 * Copyright (c) 2016 Mai Anh Vu
 */

import java.io.*;
import java.util.List;
import java.util.Set;

/**
 * This class is the abstraction of the text file being edited by the application.
 * It does not interfaces directly with the list of strings inside the file, but
 * through another abstraction layer of the LinesList class. Therefore, it only handles
 * the I/O side of the actual text file.
 */
public class TextFile {

    /**
     * Constants
     */
    private static final String STRING_PATTERN_TRAILING_SPACES = "\\s+$";
    private static final Error ERROR_FILE_IS_DIRECTORY = new Error("Cannot edit a directory");

    /**
     * Properties
     */
    private final String filePath_;
    private final File textFile_;
    private final LinesList linesList_;
    private final boolean isDebugMode_;
    private boolean isPristine_;


    /**
     * Constructs an abstraction of the text file being edited, with I/O handlers
     * and debug flag.
     * @param filePath path leading to the file being edited
     * @param isDebugMode debug mode flag, while on debug mode the file does not
     *                    actually get created
     * @throws IOException exception occured when the user does not have permissions on the file
     * @throws Error error thrown when the file is actually a directory
     */
    public TextFile(String filePath, boolean isDebugMode) throws IOException, Error {
        // Initialize properties
        this.filePath_ = filePath;
        this.textFile_ = new File(filePath);
        this.linesList_ = new LinesList();
        this.isDebugMode_ = isDebugMode;
        this.isPristine_ = true;

        this.createTextFileIfNotExists();
        this.populateLinesFromFile();
    }

    /**
     * Constructs an abstraction of the text file being edited, with I/O handlers and
     * without debugging.
     * @param filePath path leading to the file being edited
     * @throws IOException exception occured when the user does not have permissions on the file
     * @throws Error error thrown when the file is actually a directory
     */
    public TextFile(String filePath) throws IOException, Error {
        this(filePath, false);
    }

    /**
     * Removes all trailing white spaces from a string.
     * @param original a string
     * @return the original string minus trailing white spaces
     */
    private static String rightTrim(String original) {
        return original.replaceAll(STRING_PATTERN_TRAILING_SPACES, "");
    }

    private void createTextFileIfNotExists() throws IOException, Error {
        // Does not create file in debug mode
        if (this.isDebugMode_) {
            return;
        }

        if (!this.textFile_.exists()) {
            this.textFile_.createNewFile();
        } else if (this.textFile_.isDirectory()) {
            throw ERROR_FILE_IS_DIRECTORY;
        }
    }

    private void populateLinesFromFile() throws IOException {
        // Does not read data from file if is debug mode
        if (this.isDebugMode_) {
            return;
        }

        BufferedReader reader = new BufferedReader(
                new FileReader(this.textFile_)
        );

        // Start reading lines from the file
        String line;
        while ((line = reader.readLine()) != null) {
            linesList_.add(line);
        }

        // Trim trailing empty lines
        for (int i = linesList_.count() - 1; i >= 0 && linesList_.get(i).trim().isEmpty(); i--) {
            linesList_.remove(i);
        }
    }

    /**
     * Returns the number of lines currently in the text file.
     * @return the number of lines in the text file
     */
    public int getLinesCount() {
        return this.linesList_.count();
    }

    /**
     * Returns a list containing all the lines in the text file.
     * @return a list containing all lines
     */
    public List<String> getAllLines() {
        return this.linesList_.getAll();
    }

    /**
     * Returns the path to the file currently being edited.
     * @return the path to the current file
     */
    public String getFilePath() {
        return filePath_;
    }

    /**
     * Appends a line to the end of the file.
     * @param newLine a string
     * @return the index of the newly added line
     */
    public int addLine(String newLine) {
        int lineId = this.linesList_.add(rightTrim(newLine));
        this.isPristine_ = false;
        return lineId;
    }

    /**
     * Returns the line at the specified index, counting from 0
     * @param index the index of the line
     * @return the line at the specified index
     * @throws IndexOutOfBoundsException when the specified index is beyond the list's capacity
     */
    public Line getLineAt(int index) throws IndexOutOfBoundsException {
        return this.linesList_.getLine(index);
    }

    /**
     * Removes the line at the specified index, returning the removed line's content.
     * @param index the index of the line to delete
     * @return the content of the deleted line, or null if the removal fails
     */
    public String removeLine(int index) {
        String removedLine = this.linesList_.remove(index);
        if (removedLine != null) {
            this.isPristine_ = false;
        }
        return removedLine;
    }

    /**
     * Removes all lines from the file.
     */
    public void clearAllLines() {
        this.linesList_.clear();
        this.isPristine_ = false;
    }

    /**
     * Sorts lines in alphabetical order.
     */
    public void sortLines() {
        this.linesList_.sort();
        this.isPristine_ = false;
    }

    /**
     * Search for the word (case-insensitive) inside the text file
     * and return the line numbers where the word was found in.
     * @param word a query string
     * @return a set containing the indices of the lines where the word was found in
     */
    public Set<Integer> searchFor(String word) {
        return this.linesList_.search(word);
    }

    /**
     * Saves the content of the current file to disk if it has been modified
     * since the last save.
     * @throws IOException exception thrown when the user does not have access rights to the file being edited
     */
    public void save() throws IOException {
        // Skip if file was not modified since last save
        if (this.isPristine_) {
            return;
        }

        BufferedWriter writer = new BufferedWriter(new FileWriter(this.textFile_));
        List<String> lines = this.getAllLines();
        for (String line : lines) {
            writer.write(line);
            writer.newLine();
        }
        writer.close();

        this.isPristine_ = true;
    }
}
