//Copyright © 2016 - 2020 luofei86@gmail.com All Rights Reserved.罗飞 版本所有
package com.mokous.dragon.boat.base;

import java.io.*;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author luofei
 * Generate 2020/01/11
 */
public class DragonUtils {
    public static final String LINE_SEPARATOR = java.security.AccessController
            .doPrivileged(new sun.security.action.GetPropertyAction("line.separator"));

    public static final String ID_COLUMN = "id";
    public static final String DEL_FLAG_COLUMN = "del_flag";
    public static final String DEL_FLAG = "delFlag";
    public static final String UPDATE_TIME_COLUMN = "update_time";
    public static final String CREATE_TIME_COLUMN = "create_time";
    public static final String UPDATE_TIME = "updateTime";
    public static final String CREATE_TIME = "createTime";

    private static final String YMDHMS_FORMAT = "yyyy-MM-dd HH:m:ss";

    public static final int INDEX_NOT_FOUND = -1;
    public static final String JAVA_SRC_FOLDER_SYMBOL = ".";

    public static final String PRIMARY_KEY_SYMBOL = "PRIMARY KEY";
    public static final String KEY_SYMBOL = "KEY";
    public static final String UNIQUE_KEY_SYMBOL = "UNIQUE KEY";
    public static final String CREATE_TABLE_KEY_SYMBOL = "CREATE TABLE";
    public static final String ESCAPE_SQL_SYMBOL = "`";
    public static final String SQL_EXECUTE_END_SYMBOL = ";";
    public static final String UNSIGNED_SQL_SYMBOL = " UNSIGNED ";
    public static final String AUTO_INCREMENT_SYMBOL = "AUTO_INCREMENT";
    public static final String DBNAME_SEPARATE_SYMBOL = "_";
    public static final String OPEN_PARENTHESIS = "(";
    public static final String CLOSED_PARENTHESIS = ")";

    public static final String COMMA_SEPARATE_SYMBOL = ",";

    public static final String convertDate2StrLong_1(final Date date) {
        if (date == null) {
            return "";
        }
        return new SimpleDateFormat(YMDHMS_FORMAT).format(date);
    }

    public static final boolean emptyOrNull(String[] args) {
        return args == null || args.length == 0;
    }

    public static final boolean emptyOrNull(String args) {
        return args == null || args.length() == 0;
    }

    public static final boolean notEmptyAndNull(String str) {
        return !emptyOrNull(str);
    }

    public static String buildAttributeValue(String domainName) {
        if (isEmpty(domainName)) {
            return "";
        }
        char c = domainName.charAt(0);
        if (c >= 'a' && c <= 'z') {
            return domainName;
        }
        if (c >= 'A' && c <= 'Z') {
            return new StringBuilder().append((char) (c + 32)).append(domainName.substring(1)).toString();
        }
        return domainName;
    }

    public static String convertJavaPathToFilePath(String packageName) {
        return replace(packageName, JAVA_SRC_FOLDER_SYMBOL, File.separator);
    }

    public static String replace(String packageName, String javaSrcFolderSymbol, String separator) {
        return replace(packageName, javaSrcFolderSymbol, separator, -1);
    }

    public static String replace(final String text, final String searchString, final String replacement, int max) {
        if (isEmpty(text) || isEmpty(searchString) || replacement == null || max
                == 0) {
            return text;
        }
        int start = 0;
        int end = text.indexOf(searchString);
        if (end == INDEX_NOT_FOUND) {
            return text;
        }

        final int replLength = searchString.length();
        int increase = replacement.length() - replLength;
        increase = Math.max(increase, 0);
        increase *= max < 0 ? 16 : Math.min(max, 64);
        final StringBuilder buf = new StringBuilder(text.length() + increase);
        while (end != INDEX_NOT_FOUND) {
            buf.append(text.substring(start, end)).append(replacement);
            start = end + replLength;
            if (--max == 0) {
                break;
            }
            end = text.indexOf(searchString, start);
        }
        buf.append(text.substring(start));
        return buf.toString();
    }

    private static boolean isEmpty(String domainName) {
        return domainName == null || domainName.length() == 0;
    }

    public static String buildHeadComment() {
        return "/**" + LINE_SEPARATOR + " * Copyright © 2016 - " + Calendar.getInstance().get(Calendar.YEAR)
                + " luofei86@gmail.com All Rights Reserved.罗飞 版本所有" + LINE_SEPARATOR + " */";
    }

    public static void main(String[] args) {
        System.out.println(buildHeadComment());
    }

    public static String buildPackageStatement(String domainInJavaPath) {
        return "package " + domainInJavaPath + ";" + LINE_SEPARATOR;
    }


    public static String buildPackage(String s, String s1) {
        if (s1 == null || s1.isEmpty()) {
            return s;
        }
        if (s == null || s.isEmpty()) {
            return s1;
        }
        if (s.endsWith(JAVA_SRC_FOLDER_SYMBOL) && s1.startsWith(JAVA_SRC_FOLDER_SYMBOL)) {
            return s + s1.substring(1);
        }
        if (s.endsWith(JAVA_SRC_FOLDER_SYMBOL) && !s1.startsWith(JAVA_SRC_FOLDER_SYMBOL)) {
            return s + s1;
        }
        if (!s.endsWith(JAVA_SRC_FOLDER_SYMBOL) && s1.startsWith(JAVA_SRC_FOLDER_SYMBOL)) {
            return s + s1;
        }
        return s + JAVA_SRC_FOLDER_SYMBOL + s1;
    }

    public static String buildImport(String buildPackage) {
        return "import " + buildPackage + ";" + LINE_SEPARATOR;
    }

    public static String buildClassComment() {
        return "/**" + LINE_SEPARATOR + " * @author GEN_BY_DRAGON_BOAT " + convertDate2StrLong_1(new Date())
                + LINE_SEPARATOR + " */";
    }

    public static boolean strEquals(String dbFieldInfoType, String type) {
        return (dbFieldInfoType == null && type == null) || (dbFieldInfoType != null && dbFieldInfoType
                .equalsIgnoreCase(type));
    }



    public static boolean strEquals(String dbFieldInfoType, String... type) {
        for (String s : type) {
            if (strEquals(dbFieldInfoType, s)) {
                return true;
            }
        }
        return false;
    }

    public static String nextLong() {
        return String.valueOf(new Random().nextLong());
    }

    public static String buildCameCaseValue(String name) {
        if (isEmpty(name)) {
            return "";
        }
        char c = name.charAt(0);
        if (c >= 'A' && c <= 'Z') {
            return name;
        }
        if (c >= 'a' && c <= 'z') {
            return new StringBuilder().append((char) (c - 32)).append(name.substring(1)).toString();
        }
        return name;
    }

    public static String buildJavaEnd() {
        return "}";
    }

    public static void writeLines(File domainFile, String encoding, List<String> lines) throws IOException {
        writeLines(domainFile, encoding, lines, null);
    }

    private static void writeLines(File domainFile, String encoding, List<String> lines, String lineEncoding)
            throws IOException {
        OutputStream out = null;
        try {
            out = openOutStream(domainFile);
            writeLines(lines, lineEncoding, out, encoding);
        } finally {
            closeQuietly(out);
        }
    }

    private static void closeQuietly(OutputStream out) {
        if (out != null) {
            try {
                out.close();
            } catch (IOException e) {
                //ignore
            }
        }
    }

    private static void writeLines(List<String> lines, String lineEncoding, OutputStream out, String encoding)
            throws IOException {
        if (lineEncoding == null) {
            lineEncoding = LINE_SEPARATOR;
        }
        if (encoding != null) {
            for (String line : lines) {
                if (line != null) {
                    out.write(line.toString().getBytes(encoding));
                }
                out.write(lineEncoding.getBytes(encoding));
            }
        } else {
            for (String line : lines) {
                if (line != null) {
                    out.write(line.toString().getBytes());
                }
                out.write(lineEncoding.getBytes());
            }
        }
    }

    private static OutputStream openOutStream(File file) throws IOException {
        if (file.exists()) {
            if (file.isDirectory() || !file.canWrite()) {
                throw new IOException("File " + file + " is directory or can not be written to.");
            }
        } else {
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) {
                if (!parent.mkdirs()) {
                    throw new IOException("File " + parent + " could not be created.");
                }
            }
        }
        return new FileOutputStream(file);
    }

    public static String buildFile(String... paths) {
        return buildPathBySep(File.separator, paths);
    }

    public static String buildPackage(String... paths) {
        return buildPathBySep(JAVA_SRC_FOLDER_SYMBOL, paths);
    }


    public static String buildPathBySep(String sep, String... paths) {
        StringBuilder sb = new StringBuilder();
        if (paths == null || paths.length == 0) {
            return "";
        }
        boolean isEndWithSep = false;
        for (String path : paths) {
            if (sb.length() == 0) {
                sb.append(path);
                isEndWithSep = path.endsWith(sep);
            } else {
                if (isEndWithSep) {
                    if (path.endsWith(sep)) {
                        sb.append(path.substring(1));
                    } else {
                        sb.append(path);
                    }
                } else {
                    if (!path.endsWith(sep)) {
                        sb.append(sep);
                    }
                    sb.append(path);
                }
            }
            isEndWithSep = path.endsWith(sep);
        }
        return sb.toString();
    }

    public static List<String> readLines(File file, Charset encoding) throws IOException {
        FileInputStream in = null;

        List var3;
        try {
            in = openInputStream(file);
            var3 = readLines(in, Charsets.toCharset(encoding));
        } finally {
            closeQuietly(in);
        }

        return var3;
    }


    public static List<String> readLines(InputStream input, Charset encoding) throws IOException {
        InputStreamReader reader = new InputStreamReader(input, Charsets.toCharset(encoding));
        return readLines((Reader) reader);
    }

    public static List<String> readLines(InputStream input, String encoding) throws IOException {
        return readLines(input, Charsets.toCharset(encoding));
    }

    public static BufferedReader toBufferedReader(Reader reader) {
        return reader instanceof BufferedReader ? (BufferedReader) reader : new BufferedReader(reader);
    }

    public static List<String> readLines(Reader input) throws IOException {
        BufferedReader reader = toBufferedReader(input);
        List<String> list = new ArrayList();

        for (String line = reader.readLine(); line != null; line = reader.readLine()) {
            list.add(line);
        }

        return list;
    }


    public static List<String> readLines(File file, String encoding) throws IOException {
        return readLines(file, Charsets.toCharset(encoding));
    }

    public static List<String> readLines(File file) throws IOException {
        return readLines(file, Charset.defaultCharset());
    }

    public static FileInputStream openInputStream(File file) throws IOException {
        if (file.exists()) {
            if (file.isDirectory()) {
                throw new IOException("File '" + file + "' exists but is a directory");
            } else if (!file.canRead()) {
                throw new IOException("File '" + file + "' cannot be read");
            } else {
                return new FileInputStream(file);
            }
        } else {
            throw new FileNotFoundException("File '" + file + "' does not exist");
        }
    }

    public static void closeQuietly(InputStream input) {
        closeQuietly((Closeable) input);
    }

    public static void closeQuietly(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException var2) {
        }

    }

    public static boolean contains(String value, String contain) {
        if (value == null && contain == null) {
            return true;
        }
        if (value == null || contain == null) {
            return false;
        }
        return value.toLowerCase().contains(contain.toLowerCase());
    }

    public static boolean startsWithIgnoreCase(String sql, String create) {
        if (sql == null && create == null) {
            return true;
        }
        if (sql == create) {
            return true;
        }
        if (sql == null || create == null) {
            return false;
        }
        if (sql.toLowerCase().startsWith(create.toLowerCase())) {
            return true;
        }
        return false;
    }

    public static String substringBetween(String sql, String s) {
        return substringBetween(sql, s, s);
    }

    public static String substringBetween(String str, final String open, final String close) {
        if (str == null || open == null || close == null) {
            return null;
        }
        final int start = str.indexOf(open);
        if (start == INDEX_NOT_FOUND) {
            return null;
        }
        final int end = str.indexOf(close, start + open.length());
        if (end == INDEX_NOT_FOUND) {
            return null;
        }
        return str.substring(start + open.length(), end);
    }

    public static boolean endsWithIgnoreCase(String sql, String end) {
        {
            if (sql == end) {
                return true;
            }
            if (sql == null && end == null) {
                return true;
            }
            if (sql == null || end == null) {
                return false;
            }
            return sql.toLowerCase().startsWith(end.toLowerCase());
        }
    }

    public static String convertToClassAttribute(String columnName) {
        if (!contains(columnName, DBNAME_SEPARATE_SYMBOL)) {
            return columnName;
        }
        String[] subNames = split(columnName, DBNAME_SEPARATE_SYMBOL);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < subNames.length; i++) {
            if (i == 0) {
                sb.append(subNames[i]);
            } else if (subNames[i].length() == 1) {
                sb.append(subNames[i].toUpperCase());
            } else {
                sb.append(Character.toUpperCase(subNames[i].charAt(0))).append(subNames[i].substring(1));
            }
        }
        return sb.toString();
    }

    public static String[] split(final String str, final String separatorChars) {
        return splitWorker(str, separatorChars, -1, false);
    }

    private static String[] splitWorker(final String str, final String separatorChars, final int max,
            final boolean preserveAllTokens) {
        // Performance tuned for 2.0 (JDK1.4)
        // Direct code is quicker than StringTokenizer.
        // Also, StringTokenizer uses isSpace() not isWhitespace()

        if (str == null) {
            return null;
        }
        final int len = str.length();
        if (len == 0) {
            return new String[0];
        }
        final List<String> list = new ArrayList<>();
        int sizePlus1 = 1;
        int i = 0, start = 0;
        boolean match = false;
        boolean lastMatch = false;
        if (separatorChars == null) {
            // Null separator means use whitespace
            while (i < len) {
                if (Character.isWhitespace(str.charAt(i))) {
                    if (match || preserveAllTokens) {
                        lastMatch = true;
                        if (sizePlus1++ == max) {
                            i = len;
                            lastMatch = false;
                        }
                        list.add(str.substring(start, i));
                        match = false;
                    }
                    start = ++i;
                    continue;
                }
                lastMatch = false;
                match = true;
                i++;
            }
        } else if (separatorChars.length() == 1) {
            // Optimise 1 character case
            final char sep = separatorChars.charAt(0);
            while (i < len) {
                if (str.charAt(i) == sep) {
                    if (match || preserveAllTokens) {
                        lastMatch = true;
                        if (sizePlus1++ == max) {
                            i = len;
                            lastMatch = false;
                        }
                        list.add(str.substring(start, i));
                        match = false;
                    }
                    start = ++i;
                    continue;
                }
                lastMatch = false;
                match = true;
                i++;
            }
        } else {
            // standard case
            while (i < len) {
                if (separatorChars.indexOf(str.charAt(i)) >= 0) {
                    if (match || preserveAllTokens) {
                        lastMatch = true;
                        if (sizePlus1++ == max) {
                            i = len;
                            lastMatch = false;
                        }
                        list.add(str.substring(start, i));
                        match = false;
                    }
                    start = ++i;
                    continue;
                }
                lastMatch = false;
                match = true;
                i++;
            }
        }
        if (match || preserveAllTokens && lastMatch) {
            list.add(str.substring(start, i));
        }
        return list.toArray(new String[list.size()]);
    }
}
