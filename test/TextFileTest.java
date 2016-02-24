/**
 * Copyright (c) 2016 Mai Anh Vu
 */
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class TextFileTest {
    private String filePath_;
    private File file_;
    private TextFile textFile_;

    @Before
    public void setUp() {
        this.filePath_ = "dummy.txt";
        this.file_ = new File(this.filePath_);
    }

    @After
    public void tearDown() {
        if (!this.file_.exists()) {
            return;
        }
        this.file_.delete();
    }

    private void createTextFile() throws Exception {
        this.textFile_ = new TextFile(this.filePath_);
    }

    private void createDebugTextFile() throws Exception {
        this.textFile_ = new TextFile(this.filePath_, true);
    }

    @Test
    public void Text_file_gets_created_upon_constructed() throws Exception, Error {
        createTextFile();
        assertThat(this.file_.exists(), is(true));
    }

    @Test
    public void Creating_debug_text_file_does_not_generate_file() throws Exception, Error {
        createDebugTextFile();
        assertThat(this.file_.exists(), is(false));
    }

    @Test(expected = Error.class)
    public void Error_is_thrown_when_trying_to_edit_directory() throws Exception {
        this.file_.mkdir();
        createTextFile();
    }

    @Test
    public void Lines_are_populated_upon_construction() throws Exception {
        String[] lines = new String[]{
                "Lorem ipsum",
                "Dolor sit",
                "Amet, consectetur",
                "Adipiscing elit"
        };
        BufferedWriter writer = new BufferedWriter(new FileWriter(this.file_));
        for (String line : lines) {
            writer.write(line);
            writer.newLine();
        }
        writer.close();

        createTextFile();
        assertThat(textFile_.getLinesCount(), is(lines.length));
        List<String> linesFromFile = textFile_.getAllLines();
        for (int i = 0; i < lines.length; i++) {
            assertThat(linesFromFile.get(i), equalTo(lines[i]));
        }
    }

    @Test
    public void Add_line_returns_index_of_the_added_line() throws Exception {
        createDebugTextFile();
        int index = this.textFile_.addLine("Hello World!");
        assertThat(index, is(0));
    }

    @Test
    public void Added_lines_get_appended_to_lines_list() throws Exception {
        createDebugTextFile();
        this.textFile_.addLine("Hello World!");

        String newLine = "Lorem ipsum dolor sit amet";
        this.textFile_.addLine(newLine);

        assertThat(this.textFile_.getAllLines().get(this.textFile_.getLinesCount() - 1), equalTo(newLine));
    }

    @Test
    public void Blank_lines_are_added_as_empty_line() throws Exception {
        String blankLine = "      ";

        createDebugTextFile();
        this.textFile_.addLine(blankLine);

        assertThat(this.textFile_.getLinesCount(), is(1));

        List<String> allLines = this.textFile_.getAllLines();
        assertThat(allLines.get(allLines.size() - 1), equalTo(""));
    }

    @Test
    public void Removing_valid_line_decrement_lines_count() throws Exception {
        createDebugTextFile();
        this.textFile_.addLine("Line 1");
        this.textFile_.addLine("Line 2");
        this.textFile_.removeLine(1);
        assertThat(this.textFile_.getLinesCount(), is(1));
    }

    @Test
    public void Removing_invalid_line_returns_null() throws Exception {
        createDebugTextFile();
        this.textFile_.addLine("Line 1");
        String removedLine = this.textFile_.removeLine(1);
        assertThat(removedLine, is(nullValue()));
    }

    @Test
    public void Removing_line_returns_the_removed_line() throws Exception {
        createDebugTextFile();
        String lineText = "Lorem ipsum dolor sit amet";
        this.textFile_.addLine(lineText);
        String removedLine = this.textFile_.removeLine(0);
        assertThat(removedLine, equalTo(lineText));
    }

    @Test
    public void Clearing_deletes_all_lines() throws Exception {
        createDebugTextFile();
        final String[] lines = "Lorem ipsum dolor sit amet".split(" ");
        for (String line : lines) {
            this.textFile_.addLine(line);
        }

        assertThat(this.textFile_.getLinesCount(), is(lines.length));

        this.textFile_.clearAllLines();
        assertThat(this.textFile_.getLinesCount(), is(0));
    }

    @Test
    public void Sorting_arranges_lines_in_alphabetical_order() throws Exception {
        createDebugTextFile();
        final String[] lines = "Lorem ipsum dolor sit amet, consectetur adipiscing elit".split(" ");
        for (String line : lines) {
            this.textFile_.addLine(line);
        }

        Arrays.sort(lines);
        this.textFile_.sortLines();
        List<String> linesInFile = this.textFile_.getAllLines();

        for (int i = 0; i < lines.length; i++) {
            assertThat(linesInFile.get(i), equalTo(lines[i]));
        }
    }

    @Test
    public void Searching_returns_correct_results() throws Exception {
        createDebugTextFile();
        final String[] lines = "The quick brown fox jumps over the lazy dog".split(" ");
        for (String line : lines) {
            this.textFile_.addLine(line);
        }

        assertThat(this.textFile_.searchFor("the"), hasItems(0, 6));
    }

    @Test
    public void Saving_modifies_file_on_disk() throws Exception {
        createTextFile();
        final String[] lines = "The quick brown fox\njumps over\nthe lazy dog".split("\n");
        for (String line : lines) {
            this.textFile_.addLine(line);
        }

        this.textFile_.save();

        BufferedReader reader = new BufferedReader(new FileReader(this.file_));
        for (String line : lines) {
            String lineFromFile = reader.readLine();
            assertThat(lineFromFile, equalTo(line));
        }
        assertThat(reader.readLine(), is(nullValue()));
        reader.close();
    }

    @Test
    public void Only_save_when_file_is_modified() throws Exception {
        createTextFile();
        this.textFile_.addLine("Hello World!");
        this.textFile_.save();

        long lastModified = this.file_.lastModified();
        Thread.sleep(1000);

        this.textFile_.save();
        long newLastModified = this.file_.lastModified();
        assertThat(newLastModified, is(lastModified));
    }

}
