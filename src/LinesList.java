/**
 * LinesList.java
 * Copyright (c) 2016 Mai Anh Vu
 */

import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;

/**
 * This class is the abstraction of the list of lines contained in a file.
 * It does not take care of I/O operations (this is taken care by the TextFile
 * class instead), but it takes care of the core functionality implemented by
 * TextBuddy like: add, delete, display, clear, search, sort, etc.
 */
public class LinesList {

    /**
     * Constants
     */
    private static final LineComparator COMPARATOR_LINES = new LineComparator();

    /**
     * Properties
     */
    private ArrayList<Line> lines_;
    private HashMap<String, Set<Line>> searchMap_;
    private ArrayList<String> contentCacheList_;

    /**
     * Constructs an empty list of lines
     */
    public LinesList() {
        this.initializeProperties();
    }

    private void initializeProperties() {
        this.lines_ = new ArrayList<>();
        this.searchMap_ = new HashMap<>();
        this.contentCacheList_ = new ArrayList<>();
    }

    /**
     * Removes all lines from the list
     */
    public void clear() {
        this.initializeProperties();
    }

    /**
     * Appends a new line to the end of the list, and add all of its words
     * to search indexing.
     * @param text a string
     * @return the index of the newly added line
     */
    public int add(String text) {
        // Append to the end, so obviously the line number is the
        // size of the list before adding
        int lineNumber = this.lines_.size();

        Line line = new Line(text, lineNumber);
        this.lines_.add(line);

        // Index the words inside the line
        this.indexWords(line);

        // Add the line content to cache
        this.contentCacheList_.add(text);

        return lineNumber;
    }

    /**
     * Searches for a word inside the search index and returns a set of line numbers
     * where the word was found.
     * @param word a string
     * @return a set containing the line numbers where the word was found
     */
    public Set<Integer> search(String word) {
        Set<Line> lineSet = this.searchMap_.get(word.toLowerCase());

        if (lineSet == null) {
            return null;
        }

        ArrayList<Integer> lineNumbers = lineSet.stream().map(Line::getNumber)
                .collect(Collectors.toCollection(ArrayList::new));

        return new CopyOnWriteArraySet<>(lineNumbers);
    }

    /**
     * Removes the line at the specified index.
     * @param index the index to remove
     * @return the removed line's content
     */
    public String remove(int index) {
        try {
            Line removedLine = this.lines_.remove(index);
            this.contentCacheList_.remove(index);

            // Remove references
            this.unindexWords(removedLine);

            // Finally, return the removed line
            return removedLine.getContent();
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    /**
     * Returns the number of lines contained by this list.
     * @return the number of lines in the list
     */
    public int count() {
        return this.lines_.size();
    }

    /**
     * Returns a list of the content of all the lines.
     * @return a list of content of all lines
     */
    public List<String> getAll() {
        return this.contentCacheList_;
    }

    /**
     * Returns the content of the line at the specified index.
     * @param index the index of the line
     * @return the content of the line
     * @throws IndexOutOfBoundsException exception thrown when the index is beyond the list's capacity
     */
    public String get(int index) throws IndexOutOfBoundsException {
        return this.contentCacheList_.get(index);
    }

    /**
     * Returns the line object at the specified index.
     * @param index the index of the line
     * @return the line object
     * @throws IndexOutOfBoundsException exception thrown when the index is beyond the list's capacity
     */
    public Line getLine(int index) throws IndexOutOfBoundsException {
        return this.lines_.get(index);
    }

    /**
     * Sorts all the lines according to alphabetical order.
     */
    public void sort() {
        this.lines_.sort(COMPARATOR_LINES);

        // Re-establish line numbers
        for (int i = 0; i < this.lines_.size(); i++) {
            Line line = this.lines_.get(i);
            line.setNumber(i);
        }

        this.contentCacheList_ = new ArrayList<>();
        this.contentCacheList_.addAll(this.lines_.stream().map(Line::getContent).collect(Collectors.toList()));
    }

    /**
     * Perform indexing on all the words within the specified line.
     * @param line the line containing words to index
     */
    private void indexWords(Line line) {
        for (String word : line.getWords()) {
            // Check line set
            Set<Line> lineSet = null;
            if ((lineSet = this.searchMap_.get(word)) == null) {
                // Create new set and assign to word
                lineSet = new TreeSet<>();
                this.searchMap_.put(word, lineSet);
            }
            // Add line reference to set
            lineSet.add(line);
        }
    }

    /**
     * Removes indexing on all the words within the specified line.
     * @param line the line to be removed
     */
    private void unindexWords(Line line) {
        for (String word : line.getWords()) {
            Set<Line> lineSet = this.searchMap_.get(word);
            if (lineSet == null) {
                continue;
            }
            lineSet.remove(line);
        }
    }

    /**
     * Comparator class used to sort lines according to their relative
     * alphabetical ordering
     */
    private static class LineComparator implements Comparator<Line> {
        @Override
        public int compare(Line o1, Line o2) {
            return o1.compareTo(o2);
        }
    }
}
