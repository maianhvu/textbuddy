import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by maianhvu on 22/2/16.
 */
public class LinesListTest {

    private LinesList linesList_;

    @Before
    public void setUp() {
        this.linesList_ = new LinesList();
    }

    private void addLines(String[] lines) {
        for (String line : lines) {
            this.linesList_.add(line);
        }
    }

    @Test
    public void Adding_a_line_returns_line_number() {
        linesList_.add("Dolor sit");
        assertThat(linesList_.add("Lorem ipsum"), is(1));
    }

    @Test
    public void Searching_returns_a_set_of_lines() {
        addLines("lorem ipsum\nDolor lorem\nSit amet".split("\n"));

        Set<Integer> searchResults = this.linesList_.search("lorem");
        assertThat(searchResults, hasItems(0, 1));
    }

    @Test
    public void Searching_is_case_insensitive() {
        addLines("Lorem ipsum\nDolor lorem\nSit amet".split("\n"));

        Set<Integer> searchResults = this.linesList_.search("LOREM");
        assertThat(searchResults, hasItems(0, 1));
    }

    @Test
    public void Searching_for_non_existent_word_returns_null() {
        this.linesList_.add("Lorem ipsum dolor sit amet");
        Set<Integer> searchResults = this.linesList_.search("consectetur");
        assertThat(searchResults, is(nullValue()));
    }

    @Test
    public void Removing_a_line_returns_the_content_of_the_line() {
        String line = "Hello World!";
        this.linesList_.add(line);
        assertThat(this.linesList_.remove(0), equalTo(line));
    }

    @Test
    public void Removing_a_line_decrements_lines_count() {
        this.linesList_.add("Hello World!");
        assertThat(this.linesList_.count(), is(1));
        this.linesList_.remove(0);
        assertThat(this.linesList_.count(), is(0));
    }

    @Test
    public void Removing_an_invalid_line_returns_null() {
        this.linesList_.add("Line 1");
        assertThat(this.linesList_.remove(1), is(nullValue()));
    }

    @Test
    public void Removing_a_line_unindexes_existing_words() {
        addLines("Lorem ipsum\nDolor ipsum\nSit amet".split("\n"));

        assertThat(this.linesList_.search("ipsum"), hasItems(0, 1));
        this.linesList_.remove(1);
        assertThat(this.linesList_.search("ipsum"), not(hasItems(1)));
    }

    @Test
    public void Clearing_removes_all_lines_and_search_indices() {
        final String[] lines = "Lorem ipsum dolor sit amet".split(" ");
        addLines(lines);

        assertThat(this.linesList_.count(), is(lines.length));
        assertThat(this.linesList_.search("ipsum"), hasItems(1));

        this.linesList_.clear();

        assertThat(this.linesList_.count(), is(0));
        assertThat(this.linesList_.search("ipsum"), is(nullValue()));
    }

    @Test
    public void Sorting_arranges_lines_in_alphabetical_order() {
        final String[] lines = "Lorem ipsum dolor sit amet".split(" ");
        addLines(lines);

        Arrays.sort(lines);
        this.linesList_.sort();
        List<String> linesFromList = this.linesList_.getAll();

        for (int i = 0; i < lines.length; i++) {
            assertThat(linesFromList.get(i), equalTo(lines[i]));
        }
    }

    @Test
    public void Sorting_also_alter_search_indices() {
        final String[] lines = "e d a c b a".split(" ");
        addLines(lines);
        this.linesList_.sort();
        assertThat(this.linesList_.search("a"), hasItems(0, 1));
    }

}
