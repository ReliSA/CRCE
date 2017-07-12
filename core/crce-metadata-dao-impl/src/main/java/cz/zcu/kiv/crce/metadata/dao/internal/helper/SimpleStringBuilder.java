package cz.zcu.kiv.crce.metadata.dao.internal.helper;

/**
 * Wrapper of StringBuilder which makes its usage more comfortable and clear.
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public class SimpleStringBuilder {

    private static final String LINE_SEPARATOR = System.getProperty("line.separator");
    private final StringBuilder sb = new StringBuilder();

    public void append(String... strings) {
        append(true, strings);
    }

    public void append(boolean newLine, String... strings) {
        if (newLine && this.sb.length() != 0) {
            this.sb.append(LINE_SEPARATOR);
        }
        for (String string : strings) {
            this.sb.append(string);
        }
    }

    @Override
    public String toString() {
        return this.sb.toString();
    }

}
