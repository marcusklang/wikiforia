package se.lth.cs.nlp.wikipedia.parser.annotation;

import java.util.regex.Pattern;

/**
 * Utility class to do some text cleaning on the fly.
 */
public class FilteringStringBuilder {
    private final StringBuilder flushed = new StringBuilder();

    public int length() {
        return flushed.length();
    }

    public final char charAt(int pos) {
        return flushed.charAt(pos);
    }

    public String substring(int start, int end) {
        return flushed.substring(start, end);
    }

    private final StringBuilder workingBuffer = new StringBuilder();

    private final static Pattern removeWhitespaceNewline = Pattern.compile("( *)\n( )*");
    private final static Pattern removeRedudantNewline = Pattern.compile("\n{2,}");
    private final static Pattern removeRedudantWhitespace = Pattern.compile(" {2,}");
    private final static Pattern removeParanthesis = Pattern.compile("(\\(((\\(|\\)| )*)\\))");

    protected final void append(final char chr) {
        switch (chr) {
            case ' ':
            case '\n':
            case '(':
            case ')':
                    workingBuffer.append(chr);
                break;
            case '\r':
                break;
            default:
                flush();
                flushed.append(chr);
                break;
        }
    }

    public final void flush() {
        if(workingBuffer.length() > 0) {

            //Rules:
            // * space followed by newline => newline
            // * ( followed by whitespace* ) => [nothing]
            // * more than 2 newlines in sequence are truncated to 2
            String text = workingBuffer.toString();
            text = removeWhitespaceNewline.matcher(text).replaceAll("\n");
            text = removeRedudantNewline.matcher(text).replaceAll("\n\n");
            text = removeParanthesis.matcher(text).replaceAll("");
            text = removeRedudantWhitespace.matcher(text).replaceAll(" ");

            flushed.append(text);
            workingBuffer.setLength(0);
        }
    }

    public final void append(String text) {
        //Multiple whitespaces truncates to one
        //More than 2 following newlines truncates to 2
        //Empty paranthesis: ([whitespace]) is removed
        for(int i = 0; i < text.length(); i++) {
            append(text.charAt(i));
        }
    }

    public final void append(char[] chars) {
        for (char aChar : chars) {
            append(aChar);
        }
    }

    @Override
    public String toString() {
        return flushed.toString();
    }
}
