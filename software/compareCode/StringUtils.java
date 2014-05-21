
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.Normalizer;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * This class contains various methods for manipulating Strings.  It's a library
 * for all those methods that are general enough to warrant an entry here.
 */
public class StringUtils {
    
    //~ CONSTANT(S) ----------------------------------------------------------
    
    // private static final String[] EMPTY_STRING_ARRAY = new String[0];
    private static final String EMPTY_STRING = "";
    private static final char NO_QUOTE = (char)0;

    
	//~ GENERIC STRING METHOD(S) ---------------------------------------------

    public static int commonPrefix(String s1, String s2, boolean ignoreCase) {
        int len = Math.min(s1.length(), s2.length());
        for (int i = 0; i < len; i++) {
            char ch1 = s1.charAt(i), ch2 = s2.charAt(i);
            if (ch1 == ch2) continue;
            if (ignoreCase && Character.toLowerCase(ch1) == Character.toLowerCase(ch2)) continue;
            return i;
        }
        return len;
    }
    
    public static int commonPrefix(String s1, int offset1, String s2, int offset2, boolean ignoreCase) {
        int len = Math.min(s1.length() - offset1, s2.length() - offset2);
        for (int i = 0; i < len; i++) {
            char ch1 = s1.charAt(offset1 + i), ch2 = s2.charAt(offset2 + i);
            if (ch1 == ch2) continue;
            if (ignoreCase && Character.toLowerCase(ch1) == Character.toLowerCase(ch2)) continue;
            return i;
        }
        return len;
    }
    
    public static final int count(String str, char ch) { return count(str, ch, 0, str.length()); }
    public static final int count(String str, char ch, int fromIndex) { return count(str, ch, fromIndex, str.length()); }

    /**
     * Counts the number of times a charcater appears in a string.
     * 
     * @param str the string to count thr character in
     * @param ch the character
     * @param fromIndex the index to start at (inclusive)
     * @param toIndex the index to end at (exclusive)
     * @return count of charcater <code>ch</code> in string <code>str</code>
     */
    public static final int count(String str, char ch, int fromIndex, int toIndex) {
        int count = 0, len = str.length();
        rangeCheck(len, fromIndex, toIndex);
        for (int i = fromIndex; i < len; i++) if (str.charAt(i) == ch) count++;
        return count;
    }
    
    public static boolean endsWith(String str, String end, boolean ignoreCase) { return endsWith(str, end, str.length(), ignoreCase); }
    public static boolean endsWith(String str, String end, int offset, boolean ignoreCase) {
        int l = offset, el = end.length();
        if (l < el) return false;
        return str.regionMatches(ignoreCase, l - el, end, 0, el);
    }

    /**
     * Return true if <code>val</code> is null or whitespace
     * 
     * @param val a string
     * @return true if <code>val</code> is null or of length zero
     */
    public static boolean isWhitespace(String val) { 
        if (val == null) return true;
        for (int i = 0, len = val.length(); i < len; i++) {
            if (!Character.isWhitespace(val.charAt(i))) return false;
        }
        return true;
    }

    /**
     * Return true if <code>val</code> is null or of length zero.
     * 
     * @param val a string
     * @return true if <code>val</code> is null or of length zero
     */
    public static boolean isEmpty(String val) { return (val == null || val.length() == 0); }

    /**
     * Return true if <code>val</code> is null.
     * 
     * @param val an object
     * @return true if <code>val</code> is null
     */
    public static boolean isEmpty(Object val) { return (val == null); }

    /**
     * Return true if <code>vals</code> is null or of length zero.
     * 
     * @param vals an object vector
     * @return true if <code>vals</code> is null or of length zero
     */
    public static boolean isEmpty(Object[] vals) { return (vals == null || vals.length == 0); }
    
    /**
     * Return true if <code>c</code> is null or of size zero.
     * 
     * @param c a java collection
     * @return true if <code>c</code> is null or of size zero
     */
    public static boolean isEmpty(Collection<?> c) { return (c == null || c.isEmpty()); }

    public static String join(Object[] arr, char sep) {
        if (arr == null) return null;
        StringBuffer buf = new StringBuffer(16 * arr.length);
        for (int i = 0, len = arr.length; i < len; i++) {
            if (i > 0) buf.append(sep);
            if (arr[i] != null) buf.append(arr[i]);
        }
        return buf.toString();
    }
    
    public static String join(Object[] arr, String sep) {
        if (arr == null) return null;
        StringBuffer buf = new StringBuffer(16 * arr.length);
        for (int i = 0, len = arr.length; i < len; i++) {
            if (i > 0) buf.append(sep);
            if (arr[i] != null) buf.append(arr[i]);
        }
        return buf.toString();
    }

    public static <T> String join(Iterable<T> it, char sep) { return join(it, sep, NO_QUOTE); }
    public static <T> String join(Iterable<T> it, char sep, char quote) {
        boolean first = true;
        StringBuffer sb = new StringBuffer();
        for (Object obj : it) {
            if (!first) sb.append(sep);
            if (quote != NO_QUOTE) sb.append(quote);
            sb.append(obj == null? "" : obj.toString());
            if (quote != NO_QUOTE) sb.append(quote);
            first = false;
        }
        return sb.toString();
    }
    
    public static String reverse(String str) {
        int i, len = str.length();
        StringBuffer dest = new StringBuffer(len);
        for (i = len - 1; i >= 0; i--) dest.append(str.charAt(i));
        return dest.toString();
    }

    public static String removeAccents(String str) {
        return Normalizer.normalize(str, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }
    
    /**
     * This function behaves very similarly to the <code>split</code> from the standard
     * <code>String</code> class.  The reason for this one is that it does not work with
     * regular expressions, it just uses a character, and therefore it will perform much
     * better than the one in <code>java.lang.String</code>, since there is no regular
     * expression to compile/evaluate.
     * 
     * <p>For example <code>Strings.split("boo:and:foo",':')<code> would return 
     * <code>{"boo", "and", "foo"}</code> as would be expected.
     * 
     * @param str the string to split
     * @param sep the delimiting character
     * @return a vector of strings
     */
    public static final String[] split(String str, char sep) { return split(str, sep, true); }
    public static final String[] split(String str, char sep, boolean includeEmpty) { 
        String[] arr; 
        split(str, sep, includeEmpty, arr = new String[countSeparators(str, sep, includeEmpty)]); 
        return arr;
    }
    
    // The function below is to be used if one does not want to allocate a new vector for the spliting
    // but the caller must be sure that the number of items to be found fits in the vector passed
    public static final int split(String str, char sep, boolean includeEmpty, String[] arr) {
        int c = 0, len = str.length(), index = 0, last = 0;

        while (index != -1) {
            index = str.indexOf(sep, last);
            int end = (index == -1? len : index);
            if (includeEmpty || end - last > 0) arr[c++] = str.substring(last, end);
            last = index + 1;
        }
        
        return c;
    }
    
    private static final int countSeparators(String str, char sep, boolean includeEmpty) {
        int c = 0, len = str.length(), index = 0, last = 0;

        while (index != -1) {
            index = str.indexOf(sep, last);
            int end = (index == -1? len : index);
            if (includeEmpty || end - last > 0) c++;
            last = index + 1;
        }

        return c;
    }
    
    /**
     * This function behaves very similarly to the <code>split</code> from the standard
     * <code>String</code> class.  The reason for this one is that it does not work with
     * regular expressions, it just uses a string, and therefore it will perform much
     * better than the one in <code>java.lang.String</code>, since there is no regular
     * expression to compile.
     * 
     * <p>For example <code>Strings.split("boo:and:foo",":")<code> would return 
     * <code>{"boo", "and", "foo"}</code> as would be expected.
     * 
     * @param str the string to split
     * @param sep the delimiting string
     * @return a vector of strings
     */
    /*
    public static final String[] split(String str, String sep) {
        if (str.length() == 0) return new String[0];
        
        // Count Tokens
        int c = 1, len = sep.length();
        for (int index = 0; true; index += len) {
            index = str.indexOf(sep, index);
            if (index == -1) break;
            c++;
        }
        
        String[] ret = new String[c];
        if (c == 1) ret[0] = str;
        else {
            int index = 0, last = 0;
            for (int i = 0; i < c; i++) {
                index = str.indexOf(sep, last);
                ret[i] = (index == -1? str.substring(last, str.length()) : str.substring(last, index));
                last = index + len;
            }
        }
        return ret;
    }
    */

    public static final String[] split(String str, String sep) { return split(str, sep, true); }
    public static final String[] split(String str, String sep, boolean includeEmpty) { 
        String[] arr; 
        split(str, sep, includeEmpty, arr = new String[countSeparators(str, sep, includeEmpty)]); 
        return arr;
    }
    
    // The function below is to be used if one does not want to allocate a new vector for the spliting
    // but the caller must be sure that the number of items to be found fits in the vector passed
    public static final int split(String str, String sep, boolean includeEmpty, String[] arr) {
        int c = 0, len = str.length(), index = 0, last = 0, sl = sep.length();

        while (index != -1) {
            index = str.indexOf(sep, last);
            int end = (index == -1? len : index);
            if (includeEmpty || end - last > 0) arr[c++] = str.substring(last, end);
            last = index + sl;
        }
        
        return c;
    }
    
    private static final int countSeparators(String str, String sep, boolean includeEmpty) {
        int c = 0, len = str.length(), index = 0, last = 0;

        while (index != -1) {
            index = str.indexOf(sep, last);
            int end = (index == -1? len : index);
            if (includeEmpty || end - last > 0) c++;
            last = index + 1;
        }

        return c;
    }
    
    public static boolean startsWith(String str, String start, boolean ignoreCase) { return startsWith(str, start, 0, ignoreCase); }
    public static boolean startsWith(String str, String start, int offset, boolean ignoreCase) {
        int l = str.length(), sl = start.length();
        if (l < sl) return false;
        return str.regionMatches(ignoreCase, offset, start, 0, sl);
    }
    
    /**
     * Returns the value of an object as a string.
     * 
	 * @param obj an object
	 * @return a string representing the object <code>obj</code>
	 */
    public static String valueOf(Object obj) { return obj == null? "" : obj.toString(); }

    /**
     * Returns the value of a boolean as a string.  The values are &quot;true&quot;
     * and &quot;false&quot; (in lower case).
     * 
     * @param b a boolean
     * @return a string representing <code>b</code>
     */
    public static String valueOf(boolean b) { return String.valueOf(b); }

    /**
     * Returns the value of a charcater as a string (of length one).
     * 
     * @param c a character
     * @return a string representing <code>c</code>
     */
    public static String valueOf(char c) { return String.valueOf(c); }

    /**
     * Returns the value of a vector of characterd as a string.
     * 
     * @param chars a vector of characters
     * @return a string representing <code>chars</code>
     */
    public static String valueOf(char[] chars) { return String.valueOf(chars); }

    /**
     * Returns the value of a double as a string.
     * 
     * @param d a double
     * @return a string representing <code>db</code>
     */
    public static String valueOf(double d) { return String.valueOf(d); }

    /**
     * Returns the value of a float as a string.
     * 
     * @param f a float
     * @return a string representing <code>f</code>
     */
    public static String valueOf(float f) { return String.valueOf(f); }

    /**
     * Returns the value of an integer as a string.
     * 
     * @param i a float
     * @return a string representing <code>i</code>
     */
    public static String valueOf(int i) { return String.valueOf(i); }

    /**
     * Returns the value of a long as a string.
     * 
     * @param l a float
     * @return a string representing <code>l</code>
     */
    public static String valueOf(long l) { return String.valueOf(l); }
	

    //~ TRIMMING/PADDING METHOD(S) -------------------------------------------

    private static final int PAD_LIMIT = 8192;

    public static String trim(String str) { return str.trim(); }
    
    public static String leftTrim(String str) {
    	int len = str.length();
    	int st = 0;
    	while ((st < len) && (str.charAt(st) <= ' ')) st++;
    	return st > 0? str.substring(st) : str;
    }
    
    public static String rightTrim(String str) {
    	int len = str.length();
    	while ((0 < len) && (str.charAt(len - 1) <= ' ')) len--;
    	return len < str.length()? str.substring(0, len) : str;
    }

    private static String padding(int repeat, char padChar) throws IndexOutOfBoundsException {
        if (repeat < 0) throw new IndexOutOfBoundsException("Cannot pad a negative amount: " + repeat);
        final char[] buf = new char[repeat];
        for (int i = 0; i < buf.length; i++) buf[i] = padChar;
        return new String(buf);
    }

    public static String leftPad(String str, int size) { return leftPad(str, size, ' '); }

    public static String leftPad(String str, int size, char padChar) {
        if (str == null) return null;
        int pads = size - str.length();
        if (pads <= 0) return str;
        if (pads > PAD_LIMIT) return leftPad(str, size, String.valueOf(padChar));
        return padding(pads, padChar).concat(str);
    }

    public static String leftPad(String str, int size, String padStr) {
        if (str == null) return null;
        if (isEmpty(padStr)) padStr = " ";
        int padLen = padStr.length();
        int strLen = str.length();
        int pads = size - strLen;
        if (pads <= 0) return str;
        if (padLen == 1 && pads <= PAD_LIMIT) return leftPad(str, size, padStr.charAt(0));
        if (pads == padLen) return padStr.concat(str);
        else if (pads < padLen) return padStr.substring(0, pads).concat(str);
        else {
            char[] padding = new char[pads];
            char[] padChars = padStr.toCharArray();
            for (int i = 0; i < pads; i++) padding[i] = padChars[i % padLen];
            return new String(padding).concat(str);
        }
    }
    
    public static String rightPad(String str, int size) { return rightPad(str, size, ' '); }
    
    public static String rightPad(String str, int size, char padChar) {
        if (str == null) return null;
        int pads = size - str.length();
        if (pads <= 0) return str; 
        if (pads > PAD_LIMIT) return rightPad(str, size, String.valueOf(padChar));
        return str.concat(padding(pads, padChar));
    }

    public static String rightPad(String str, int size, String padStr) {
        if (str == null) return null;
        if (isEmpty(padStr)) padStr = " ";
        int padLen = padStr.length();
        int strLen = str.length();
        int pads = size - strLen;
        if (pads <= 0) return str; // returns original String when possible
        if (padLen == 1 && pads <= PAD_LIMIT) return rightPad(str, size, padStr.charAt(0));
        if (pads == padLen) return str.concat(padStr);
        else if (pads < padLen) return str.concat(padStr.substring(0, pads));
        else {
            char[] padding = new char[pads];
            char[] padChars = padStr.toCharArray();
            for (int i = 0; i < pads; i++) padding[i] = padChars[i % padLen];
            return str.concat(new String(padding));
        }
    }


	//~ UNESCAPING/ESCAPING METHOD(S) ----------------------------------------

    /**
     * Escapes a string for inclusion inside another string.  This means escaping
     * the <code>\</code> (slash) character in such a way that it will print.  For
     * example <code>Strings.escape("line\n\"with\"\nbreaks")</code> will return
     * <code>"line\\n\\"with\\"\\nbreaks"</code>.  Escape of <code>null</code> is
     * <code>null</code>.
     * 
     * @param str the string to escaped
     * @return the escaped version of <code>str</code>
     */
    public static String escape(String str) {
        if (str == null) return null;

        char ch;
        int l = str.length(), i;
        
        // Check to see if there is a need for escaping, and if there isn't,
        // just return the same string
        for (i = 0; i < l; i++) {
            ch = str.charAt(i);
            if (ch == '\\' || ch == '\t' || ch == '\n' || ch == '\r' || ch == '\"') break;
        }
        if (i == l) return str;

        // Now really do the escaping
        StringBuffer buf = new StringBuffer();
        for (i = 0; i < l; i++) {
            ch = str.charAt(i);
            switch (ch) {
                case '\\': buf.append("\\\\"); break;
                case '\t': buf.append("\\t"); break;
                case '\n': buf.append("\\n"); break;
                case '\r': buf.append("\\r"); break;
                case '\"': buf.append("\\\""); break;
                default: buf.append(ch);
            }
        }
        return buf.toString();
    }

	/*   
    private static boolean needsEscaping(String str) {
        int l = str.length();
        for (int i = 0; i < l; i++) {
            char ch = str.charAt(i);
            if (ch == '\\' || ch == '\t' || ch == '\n' || ch == '\r' || ch == '\"') return true;
        }
        return false;
    }
    */

    /**
     * Unescaped a string that has been escaped to fit inside another string.  Is kind of the 
     * opposite of <code>escape</code>.  Unscape of <code>null</code> is <code>null</code>
     * 
	 * @param str the string to unescape.
	 * @return the escaped version of <code>str</code>
	 */
    /*
	public static String unescape(String str) {
        if (str == null) return null;

        char ch;
        int l = str.length(), i;
        for (i = 0; i < l; i++) {
            ch = str.charAt(i);
            if (ch == '\\') {
                ch = str.charAt(i+1);
                if (ch == '\\' || ch == 't' || ch == 'n' || ch == 'r' || ch == '\"') break;
            }
        }
        if (i == l) return str;

        StringBuffer sb = new StringBuffer();
        int pos = 0;
        int nextPos;
        char current;
        char lookAhead;
        while (pos < l) {
            current = str.charAt(pos);
            if (current == '\\') {
                nextPos = pos + 1;
                if (nextPos < l) {
                    lookAhead = str.charAt(nextPos);
                    switch (lookAhead) {
                        case '\\': sb.append('\\'); pos += 2; break;
                        case 'r':  sb.append('\r'); pos += 2; break;
                        case 'n':  sb.append('\n'); pos += 2; break;
                        case 't':  sb.append('\t'); pos += 2; break;
                        case '"':  sb.append('\"'); pos += 2; break;
                        default: sb.append('\\'); pos++;
                    }
                }
                else {
                    sb.append(current);
                    pos++;
                }
            }
            else {
                sb.append(current);
                pos++;
            }
        }
        return sb.toString();
    }
    */

    /*    
    private static boolean needsUnescaping(String str) {
        int l = str.length();
        for (int i = 0; i < l; i++) {
            char ch = str.charAt(i);
            if (ch == '\\') {
                ch = str.charAt(i+1);
                if (ch == '\\' || ch == 't' || ch == 'n' || ch == 'r' || ch == '\"') return true;
            }
        }
        return false;
    }
    */

    
    //~ BASE64 METHOD(S) -----------------------------------------------------
	
    public static String encodeBase64(byte[] data) {
        if (data == null) return null;

        char[] buffer = new char[data.length / 3 * 4 + ((data.length % 3 == 0) ? 0 : 4)];

        String map = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";

        int buf = 0;
        for (int i = 0; i < data.length; i++) {
            switch (i % 3) {
                case 0 :
                    buffer[i / 3 * 4] = map.charAt((data[i] & 0xFC) >> 2);
                    buf = (data[i] & 0x03) << 4;
                    if (i + 1 == data.length) {
                        buffer[i / 3 * 4 + 1] = map.charAt(buf);
                        buffer[i / 3 * 4 + 2] = '=';
                        buffer[i / 3 * 4 + 3] = '=';
                    }
                    break;
                case 1 :
                    buf += (data[i] & 0xF0) >> 4;
                    buffer[i / 3 * 4 + 1] = map.charAt(buf);
                    buf = (data[i] & 0x0F) << 2;
                    if (i + 1 == data.length) {
                        buffer[i / 3 * 4 + 2] = map.charAt(buf);
                        buffer[i / 3 * 4 + 3] = '=';
                    }
                    break;
                case 2 :
                    buf += (data[i] & 0xC0) >> 6;
                    buffer[i / 3 * 4 + 2] = map.charAt(buf);
                    buffer[i / 3 * 4 + 3] = map.charAt(data[i] & 0x3F);
                    break;
            }
        }

        return new String(buffer);
    }

    public static byte[] decodeBase64(CharSequence cs) {
        int addsize = 0;
        int bufsize = 0;

        for (int i = 0; i < cs.length(); i++) {
            char c = cs.charAt(i);
            if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9') || c == '+' || c == '/') {
                bufsize++;
            }
            else if (c == '=') {
                if (i + 1 < cs.length() && cs.charAt(i + 1) == '=' && (bufsize + 2) % 4 == 0) {
                    bufsize += 2;
                    addsize = -2;
                }
                else if ((bufsize + 1) % 4 == 0) {
                    bufsize += 1;
                    addsize = -1;
                }
                else {
                    return null;
                }
                break;
            }
        }

        byte[] buffer = new byte[bufsize / 4 * 3 + addsize];

        int pos = 0;
        for (int i = 0; i < cs.length(); i++) {
            char c = cs.charAt(i);

            int data = 0;
            if (c >= 'A' && c <= 'Z') {
                data = c - 65;
            }
            else if (c >= 'a' && c <= 'z') {
                data = c - 97 + 26;
            }
            else if (c >= '0' && c <= '9') {
                data = c - 48 + 52;
            }
            else if (c == '+') {
                data = 62;
            }
            else if (c == '/') {
                data = 63;
            }
            else if (c == '=') {
                break;
            }
            else {
                continue;
            }

            switch (pos % 4) {
                case 0 :
                    buffer[pos / 4 * 3] = (byte) (data << 2);
                    break;
                case 1 :
                    buffer[pos / 4 * 3] += (byte) (data >> 4);
                    if (pos / 4 * 3 + 1 < buffer.length) {
                        buffer[pos / 4 * 3 + 1] = (byte) ((data & 0xF) << 4);
                    }
                    break;
                case 2 :
                    buffer[pos / 4 * 3 + 1] += (byte) (data >> 2);
                    if (pos / 4 * 3 + 2 < buffer.length) {
                        buffer[pos / 4 * 3 + 2] = (byte) ((data & 0x3) << 6);
                    }
                    break;
                case 3 :
                    buffer[pos / 4 * 3 + 2] += (byte) data;
                    break;
            }
            pos++;
        }

        return buffer;
    }
    
        
    //~ JAVA-LIKE METHOD(S) --------------------------------------------------
    
    public static String escapeJava(String str) { return escapeJavaStyleString(str, false); }
    public static void escapeJava(Writer out, String str) throws IOException { escapeJavaStyleString(out, str, false); }
    public static String escapeJavaScript(String str) { return escapeJavaStyleString(str, true); }
    public static void escapeJavaScript(Writer out, String str) throws IOException { escapeJavaStyleString(out, str, true); }

    private static String escapeJavaStyleString(String str, boolean escapeSingleQuotes) {
        if (str == null) return null;
        try {
            StringWriter sw = new StringWriter(str.length() * 2);
            escapeJavaStyleString(sw, str, escapeSingleQuotes);
            return sw.toString();
        }
        // This should never ever happen while writing to a StringWriter
        catch (IOException ex) { ex.printStackTrace(); }
        return null;
    }

    private static void escapeJavaStyleString(Writer out, String str, boolean escapeSingleQuote) throws IOException {
        if (out == null) { throw new IllegalArgumentException("The Writer must not be null"); }
        if (str == null) { return; }
        int sz;
        sz = str.length();
        for (int i = 0; i < sz; i++) {
            char ch = str.charAt(i);

            // Handle unicode
            if (ch > 0xfff) out.write("\\u" + hex(ch));
            else if (ch > 0xff) out.write("\\u0" + hex(ch));
            else if (ch > 0x7f) out.write("\\u00" + hex(ch));
            else if (ch < 32) {
                switch (ch) {
                    case '\b' : out.write('\\'); out.write('b'); break;
                    case '\n' : out.write('\\'); out.write('n'); break;
                    case '\t' : out.write('\\'); out.write('t'); break;
                    case '\f' : out.write('\\'); out.write('f'); break;
                    case '\r' : out.write('\\'); out.write('r'); break;
                    default: out.write((ch > 0xf? "\\u00" : "\\u000")+ hex(ch)); break;
                }
            }
            else {
                switch (ch) {
                    case '\'' :
                        if (escapeSingleQuote) out.write('\\');
                        out.write('\'');
                        break;
                    case '"' : out.write('\\'); out.write('"'); break;
                    case '\\' : out.write('\\'); out.write('\\'); break;
                    default : out.write(ch); break;
                }
            }
        }
    }

    public static String unescapeJava(String str) {
        if (str == null) return null;
        try {
            StringWriter sw = new StringWriter(str.length());
            unescapeJava(sw, str);
            return sw.toString();
        }
        // This should never ever happen while writing to a StringWriter
        catch (IOException ex) { ex.printStackTrace(); }
        return null;
    }

    public static void unescapeJava(Writer out, String str) throws IOException {
        if (out == null) throw new IllegalArgumentException("The Writer must not be null");
        if (str == null) return;

        int sz = str.length();
        StringBuffer unicode = new StringBuffer(4);
        boolean hadSlash = false;
        boolean inUnicode = false;
        for (int i = 0; i < sz; i++) {
            char ch = str.charAt(i);
            if (inUnicode) {
                // if in unicode, then we're reading unicode
                // values in somehow
                unicode.append(ch);
                if (unicode.length() == 4) {
                    // unicode now contains the four hex digits
                    // which represents our unicode character
                    try {
                        int value = Integer.parseInt(unicode.toString(), 16);
                        out.write((char) value);
                        unicode.setLength(0);
                        inUnicode = false;
                        hadSlash = false;
                    } catch (NumberFormatException nfe) {
                        throw new RuntimeException("Unable to parse unicode value: " + unicode, nfe);
                    }
                }
                continue;
            }
            if (hadSlash) {
                // handle an escaped value
                hadSlash = false;
                switch (ch) {
                    case '\\': out.write('\\'); break;
                    case '\'': out.write('\''); break;
                    case '\"': out.write('"'); break;
                    case 'r': out.write('\r'); break;
                    case 'f': out.write('\f'); break;
                    case 't': out.write('\t'); break;
                    case 'n': out.write('\n'); break;
                    case 'b': out.write('\b'); break;
                    case 'u': inUnicode = true; break;
                    default : out.write(ch); break;
                }
                continue;
            } 
            else if (ch == '\\') {
                hadSlash = true;
                continue;
            }
            out.write(ch);
        }
        if (hadSlash) {
            // then we're in the weird case of a \ at the end of the
            // string, let's output it anyway.
            out.write('\\');
        }
    }

    public static String unescapeJavaScript(String str) { return unescapeJava(str); }
    public static void unescapeJavaScript(Writer out, String str) throws IOException { unescapeJava(out, str); }

    
    //~ XML METHOD(S) --------------------------------------------------------

    private static final EntityMap XML;

    /**
     * <p>
     * The set of entities supported by HTML 4.0.
     * </p>
     */
    static {
        XML = new EntityMap();
        XML.add("quot", 34); // " - double-quote
        XML.add("amp", 38);  // & - ampersand
        XML.add("lt", 60);   // < - less-than
        XML.add("gt", 62);   // > - greater-than
        XML.add("apos", 39); // XML apostrophe
    }
    
    public String escapeXml(String str) {
        if (str == null) return null;
        StringWriter sw = new StringWriter(str.length() * 2);
        try { 
            escapeXml(sw, str); 
            return sw.toString();
        } 
        // This should never ever happen while writing to a StringWriter
        catch (IOException ex) { ex.printStackTrace(); }
        return null;
    }

    public void escapeXml(Writer writer, String str) throws IOException {
        if (writer == null ) throw new IllegalArgumentException ("The Writer must not be null.");
        if (str == null) return;
        int len = str.length();
        for (int i = 0; i < len; i++) {
            char c = str.charAt(i);
            String entityName = XML.name(c);
            if (entityName == null) {
                if (c > 0x7F) {
                    writer.write("&#");
                    writer.write(Integer.toString(c, 10));
                    writer.write(';');
                } 
                else {
                    writer.write(c);
                }
            } 
            else {
                writer.write('&');
                writer.write(entityName);
                writer.write(';');
            }
        }
    }

    public static String unescapeXml(String str) {
        if (str == null) return null;
        int firstAmp = str.indexOf('&');
        if (firstAmp < 0) return str;
        StringWriter sw = new StringWriter(str.length() * 2);
        try { 
            unescapeXml(sw, str, firstAmp); 
            return sw.toString();
        } 
        // This should never ever happen while writing to a StringWriter
        catch (IOException ex) { ex.printStackTrace(); }
        return null;
    }

    public static void unescapeXml(Writer writer, String str) throws IOException {
        if (writer == null ) throw new IllegalArgumentException ("The Writer must not be null.");
        if (str == null) return;
        int firstAmp = str.indexOf('&');
        if (firstAmp < 0) writer.write(str);
        else unescapeXml(writer, str, firstAmp);
    }

    private static void unescapeXml(Writer writer, String str, int firstAmp) throws IOException {
        writer.write(str, 0, firstAmp);
        int len = str.length();
        for (int i = firstAmp; i < len; i++) {
            char c = str.charAt(i);
            if (c == '&') {
                int nextIdx = i + 1;
                int semiColonIdx = str.indexOf(';', nextIdx);
                if (semiColonIdx == -1) { writer.write(c); continue; }
                int amphersandIdx = str.indexOf('&', i + 1);
                if (amphersandIdx != -1 && amphersandIdx < semiColonIdx) { writer.write(c); continue; }
                String entityContent = str.substring(nextIdx, semiColonIdx);
                int entityValue = -1;
                int entityContentLen = entityContent.length();
                if (entityContentLen > 0) {
                    if (entityContent.charAt(0) == '#') { // escaped value content is an integer (decimal or
                        // hexidecimal)
                        if (entityContentLen > 1) {
                            char isHexChar = entityContent.charAt(1);
                            try {
                                switch (isHexChar) {
                                    case 'X' :
                                    case 'x' :
                                        entityValue = Integer.parseInt(entityContent.substring(2), 16);
                                        break;
                                    default : 
                                        entityValue = Integer.parseInt(entityContent.substring(1), 10);
                                    
                                }
                                if (entityValue > 0xFFFF) entityValue = -1;
                            } 
                            catch (NumberFormatException ex) { entityValue = -1; }
                        }
                    } 
                    else { 
                        entityValue = XML.value(entityContent);
                    }
                }

                if (entityValue == -1) {
                    writer.write('&');
                    writer.write(entityContent);
                    writer.write(';');
                } else {
                    writer.write(entityValue);
                }
                i = semiColonIdx; // move index up to the semi-colon
            } 
            else {
                writer.write(c);
            }
        }
    }
    
    
    //~ REGULAR EXPRESSION METHOD(S) -----------------------------------------

    // Finds all the groups contained in the first match of the pattern
    public static String[] extract(String re, String str) { return extract(Pattern.compile(re), str); }
    public static String[] extract(Pattern re, String str) {
        Matcher matcher = re.matcher(str);
        String[] groups = null;
        if (matcher.find()) {
            groups = new String[matcher.groupCount()];
            for (int i = 0; i < matcher.groupCount(); i++) {
                groups[i] = matcher.group(i+1);
            }
        }
        return groups;
    }

    public static String extract(String re, String str, int i) { return extract(Pattern.compile(re), str, i); }
    public static String extract(Pattern re, String str, int i) {
        Matcher matcher = re.matcher(str);
        return (matcher.find()? matcher.group(i+1) : null);
    }
    
    
    //~ URL METHOD(S) --------------------------------------------------------

    public static String getHost(String url) { 
        int s = url.indexOf(':'), len = url.length();
        if (s == -1 || s + 4 >= len) return EMPTY_STRING;
        int e = url.indexOf('/', s + 3);
        return url.substring(s + 3, e == -1? url.length() : e);
    }
    
    
    //~ MISCELLANEOUS METHOD(S) ----------------------------------------------

    // private static MessageDigest MD5, SHA1;

    public static String md5(String str) { return hex(md5b(str)); }
    public static byte[] md5b(String str) {
        MessageDigest MD5 = null;
        try { if (MD5 == null) MD5 = java.security.MessageDigest.getInstance("MD5"); }
        catch (NoSuchAlgorithmException ex) { ex.printStackTrace(); }
        MD5.reset();
        return MD5.digest(str.getBytes());
    }

    public static String sha1(String str) { return hex(sha1b(str)); }
    public static byte[] sha1b(String str) {
        MessageDigest SHA1 = null;
        try { if (SHA1 == null) SHA1 = java.security.MessageDigest.getInstance("SHA1"); }
        catch (NoSuchAlgorithmException ex) { ex.printStackTrace(); }
        SHA1.reset();
        return SHA1.digest(str.getBytes());
    }
    

    //~ APPROXIMATE STRING MATCHING METHOD(S) --------------------------------
    
    public static int editDistance(String s1, String s2) { return editDistance(s1, s2, 1, 1, 1, 1); }
    public static int editDistance(String s1, String s2, int ic, int dc, int sc, int tc) {
        if (s1 == null || s2 == null) throw new IllegalArgumentException("Strings must not be null");
        
        int n = s1.length(), m = s2.length();
        if (n == 0) return m;
        if (m == 0) return n;

        // Allocate previous cost array, horizontally and current cost array
        int[] pp = new int[n+1], p = new int[n+1], c = new int[n+1], t;

        // Prefill part of the array
        for (int i = 0; i <= n; i++) p[i] = i;

        // Iterate
        for (int j = 1; j <= m; j++) {
            char ch2 = s2.charAt(j-1);
            
            c[0] = j;

            for (int i = 1; i <= n; i++) {
                char ch1 = s1.charAt(i-1);
                
                // Minimum of cell to the left+1, to the top+1, diagonally left and up +cost
                c[i] = Math.min(Math.min(c[i-1] + dc, p[i] + ic),  p[i-1] + (ch1 == ch2 ? 0 : sc));

                // Transposition
                if (tc >= 0 && i > 1 && j > 1 && s1.charAt(i-2) == ch2 && s2.charAt(j-2) == ch1) {
                    c[i] = Math.min(c[i], pp[i-2] + tc);
                }
            }
            
            // copy current distance counts to 'previous row' distance counts
            // t = p; p = c; c = t;
            t = pp; pp = p; p = c; c = t;
        }

        // our last action in the above loop was to switch d and p, so p now 
        // actually has the most recent cost counts
        return p[n];
    }

    /**
     * This method is a very fast fuzzy matching algorithms for strings. It does not perform any comparisons,
     * only subtractions and additions and hashing. It is used mostly in databases to perform the 
     * most basic selection of good matches for slower string comparison.
     * @param text1
     * @param text2
     * @return a score between 0 and 1 where 1 means identical strings.
     */
    public static Double histogramMatch(String text1, String text2)
    {
        int neg = 0;
        int pos = 0;
        
        HashMap<Character,Integer> hist = new HashMap<Character,Integer>();
        
        for(int i=0;i<text1.length();i++)
        {
            Character letra = new Character(text1.charAt(i));
            if (hist.containsKey(letra))
                hist.put(letra, new Integer(hist.get(letra).intValue()+1));
            else
                hist.put(letra,new Integer(1));
        }
        for(int i=0;i<text2.length();i++)
        {
            Character letra = new Character(text2.charAt(i));
            if (hist.containsKey(letra))
                hist.put(letra, new Integer(hist.get(letra).intValue()-1));
            else
                hist.put(letra,new Integer(-1));
        }
        for (Character key:hist.keySet())
        {
            Integer val = hist.get(key);
            if (val.intValue()>0)
                pos = pos+val.intValue();
            else
                neg = neg + Math.abs(val.intValue());
        }
        
        int longest = Math.max(text1.length(),text2.length());
        return new Double(1-((double)(pos+neg)/(double)longest));
            
    }
    
    /**
     * This method produces N-grams based on some text. For example, the text: "my mom loves me" can be split into bi-grams like this
     * "my mom", "mom loves", "loves me".
     * @param text the string to divide in n-grams
     * @param n the "n" in ngrams (1 will divide words, 2 will produce bi-grams, 3 will produce 3-grams, etc.)
     * @return an array with n-grams.
     */
    public static String[] toNgrams(String text,int n){
    	String sep =" ";
    	String[] unigrams = split(text," ");
    	if(n==1)
    		return unigrams;
    	else if (unigrams.length<n)
    		return null;
    	int numNgrams = unigrams.length-(n-1);
    	String[] ngrams = new String[numNgrams];
    	for(int i=0;i<numNgrams;i++){
    		StringBuffer sb = new StringBuffer("");
    		for(int j=0;j<n;j++)
    			sb.append(sep+unigrams[i+j]);
    		ngrams[i] = sb.toString().substring(1);
    	}
    	return ngrams;
    }
    //~ HELPER(S) ------------------------------------------------------------

    private static final char[] HEXES = "0123456789abcdef".toCharArray();
   
    private static String hex(char ch) { return Integer.toHexString(ch).toUpperCase(); }

    private static String hex(byte[] bytes) {
        if (bytes == null) return null;
        final StringBuilder hex = new StringBuilder(2 * bytes.length);
        for (final byte b : bytes) hex.append(HEXES[(b & 0xF0) >> 4]).append(HEXES[(b & 0x0F)]);
        return hex.toString();
    }
    
    
    //~ INNER CLASS(ES) ------------------------------------------------------
    
    static class EntityMap {
        private Map<String,Integer> mapNameToValue = new HashMap<String,Integer>();
        private String[] mapValueToName = new String[256];
        public void add(String name, int value) {
            mapNameToValue.put(name, new Integer(value));
            mapValueToName[value] = name;
        }
        public String name(int value) { return mapValueToName[value]; }
        public int value(String name) {
            Object value = mapNameToValue.get(name);
            return value == null? -1 : ((Integer) value).intValue();
        }
    }

    public static class StringPool {
        private ConcurrentMap<String,String> map = new ConcurrentHashMap<String,String>(1024);
        public String getCanonicalVersion(String str) {
            String canon = map.get(str);
            if (canon == null) {
                canon = new String(str);
                map.put(str, str);
            }
            return canon;
        }
        public void clear() { map.clear(); }
    }
    
    
    //~ HELPER METHOD(S) -----------------------------------------------------

    /**
     * Check that fromIndex and toIndex are in range, and throw an
     * appropriate exception if they aren't.
     */
    private static void rangeCheck(int strLen, int fromIndex, int toIndex) {
        if (fromIndex > toIndex) throw new IllegalArgumentException("fromIndex("+fromIndex+") > toIndex(" + toIndex+")");
        if (fromIndex < 0) throw new ArrayIndexOutOfBoundsException(fromIndex);
        if (toIndex > strLen) throw new ArrayIndexOutOfBoundsException(toIndex);
    }    
    
    // ARRAY REPRESENTATION
    /**
     * If a String is representing a delimited array, this method will reverse it.
     * @param arr 
     * @param delimiter
     */
    public static String reverseBMIStringArray(String array,String delimiter){
    	String[] items = array.split(delimiter);
    	StringBuilder sb = new StringBuilder();
    	for(int i=items.length-1;i>=0;i--)
    		sb.append(delimiter+items[i]);
    	return sb.toString().substring(delimiter.length());
    }
    
    //~ MAIN(S) --------------------------------------------------------------

    public static void main(String[] args) {
        /*
        String[] arr;
        // arr = StringUtils.split("foo:and:boo", 'o', false);
        // arr = StringUtils.split("", ':', false);
        // arr = StringUtils.split("a::::::::::::::::::b", ':', false);
        // System.out.println(Arrays.toString(arr));
        System.out.println(StringUtils.endsWithIgnoreCase("Andres", "DRES"));
        System.out.println(StringUtils.endsWithIgnoreCase("Andres", "es"));
        System.out.println(StringUtils.endsWithIgnoreCase("Andres", "Xes"));
        System.out.println(StringUtils.endsWithIgnoreCase("Andres", "resX"));
        System.out.println(StringUtils.startsWithIgnoreCase("Andres", "XANDRE"));
        */
        
        // System.out.println(getHost("http://astrocenter.astrology.msn.com:80/msn/Default.aspx"));
        
        System.out.println(editDistance("ab", "ba"));
        System.out.println(histogramMatch("donde tienen la leche", "donde tnen leche"));
        System.out.println(histogramMatch("donde tienen la leche", "donde esta mi mama"));
        System.out.println(histogramMatch("el perro malo", "malo el perro")); // not good for these cases
        String[] bigrams = toNgrams("donde esta mi mama", 2);
        String[] trigrams = toNgrams("donde esta mi mama", 3);
        for(String w:bigrams)
        	System.out.println(w);
        for(String w:trigrams)
        	System.out.println(w);
        
    }
}

