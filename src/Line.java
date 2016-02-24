/**
 * Line.java
 * Copyright (c) 2016 Mai Anh Vu
 *
 * This class is the abstraction of a line of text inside the file.
 * It stores extra information than a string, so as to facilitate
 * more advanced operations required by the TextBuddy application.
 */
public class Line implements Comparable<Line> {

    private static final String STRING_DELIMITER_WORD = "\\s+";

    private int number_;
    private final String content_;
    private final String[] words_;

    public Line(String content, int number) {
        this.number_ = number;
        this.content_ = content;
        this.words_ = content.toLowerCase().split(STRING_DELIMITER_WORD);
    }

    public String[] getWords() {
        return this.words_;
    }

    public String getContent() {
        return this.content_;
    }

    public int getNumber() {
        return this.number_;
    }

    public void setNumber(int newNumber) {
        this.number_ = newNumber;
    }

    public int getDisplayNumber() {
        return this.number_ + 1;
    }

    @Override
    public boolean equals(Object another) {
        if (another == null) return false;
        if (this == another) return true;
        if (!(another instanceof Line)) return false;
        Line l = (Line) another;
        return this.content_.equals(l.content_) && this.number_ == l.number_;
    }

    @Override
    public int compareTo(Line another) {
        int comparison = this.content_.compareTo(another.content_);
        if (comparison != 0) {
            return comparison;
        }
        return this.number_ - another.number_;
    }
}


