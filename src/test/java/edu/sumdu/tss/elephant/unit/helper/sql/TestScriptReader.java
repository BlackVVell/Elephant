
package edu.sumdu.tss.elephant.unit.helper.sql;

import java.io.StringReader;
import java.util.Random;

import edu.sumdu.tss.elephant.helper.sql.ScriptReader;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the script reader tool that breaks up SQL scripts in statements.
 */
public class TestScriptReader {


    @Test
    static String randomStatement(Random random) {
        StringBuilder buff = new StringBuilder();
        int len = random.nextInt(5);
        for (int i = 0; i < len; i++) {
            switch (random.nextInt(10)) {
                case 0: {
                    int l = random.nextInt(4);
                    String[] ch = { "\n", "\r", " ", "*", "a", "0", "$ " };
                    for (int j = 0; j < l; j++) {
                        buff.append(ch[random.nextInt(ch.length)]);
                    }
                    break;
                }
                case 1: {
                    buff.append('\'');
                    int l = random.nextInt(4);
                    String[] ch = { ";", "\n", "\r", "--", "//", "/", "-", "*",
                            "/*", "*/", "\"", "$ " };
                    for (int j = 0; j < l; j++) {
                        buff.append(ch[random.nextInt(ch.length)]);
                    }
                    buff.append('\'');
                    break;
                }
                case 2: {
                    buff.append('"');
                    int l = random.nextInt(4);
                    String[] ch = { ";", "\n", "\r", "--", "//", "/", "-", "*",
                            "/*", "*/", "\'", "$" };
                    for (int j = 0; j < l; j++) {
                        buff.append(ch[random.nextInt(ch.length)]);
                    }
                    buff.append('"');
                    break;
                }
                case 3: {
                    buff.append('-');
                    if (random.nextBoolean()) {
                        String[] ch = { "\n", "\r", "*", "a", " ", "$ " };
                        int l = 1 + random.nextInt(4);
                        for (int j = 0; j < l; j++) {
                            buff.append(ch[random.nextInt(ch.length)]);
                        }
                    } else {
                        buff.append('-');
                        String[] ch = { ";", "-", "//", "/*", "*/", "a", "$" };
                        int l = random.nextInt(4);
                        for (int j = 0; j < l; j++) {
                            buff.append(ch[random.nextInt(ch.length)]);
                        }
                        buff.append('\n');
                    }
                    break;
                }
                case 4: {
                    buff.append('/');
                    if (random.nextBoolean()) {
                        String[] ch = { "\n", "\r", "a", " ", "- ", "$ " };
                        int l = 1 + random.nextInt(4);
                        for (int j = 0; j < l; j++) {
                            buff.append(ch[random.nextInt(ch.length)]);
                        }
                    } else {
                        buff.append('*');
                        String[] ch = { ";", "-", "//", "/* ", "--", "\n", "\r", "a", "$" };
                        int l = random.nextInt(4);
                        int comments = 0;
                        for (int j = 0; j < l; j++) {
                            String s = ch[random.nextInt(ch.length)];
                            buff.append(s);
                            if (s.equals("/* ")) {
                                comments++;
                            }
                        }
                        while (comments-- >= 0) {
                            buff.append("*/");
                        }
                    }
                    break;
                }
                case 5: {
                    if (buff.length() > 0) {
                        buff.append(" ");
                    }
                    buff.append("$");
                    if (random.nextBoolean()) {
                        String[] ch = { "\n", "\r", "a", " ", "- ", "/ " };
                        int l = 1 + random.nextInt(4);
                        for (int j = 0; j < l; j++) {
                            buff.append(ch[random.nextInt(ch.length)]);
                        }
                    } else {
                        buff.append("$");
                        String[] ch = { ";", "-", "//", "/* ", "--", "\n", "\r", "a", "$ " };
                        int l = random.nextInt(4);
                        for (int j = 0; j < l; j++) {
                            buff.append(ch[random.nextInt(ch.length)]);
                        }
                        buff.append("$$");
                    }
                    break;
                }
                default:
            }
        }
        return buff.toString();
    }
    @Test
     void testCommon() {
        String s;
        ScriptReader source;

        s = "$$;$$;";
        source = new ScriptReader(new StringReader(s));
        assertEquals("$$;$$", source.readStatement());
        assertEquals(null, source.readStatement());
        source.close();

        s = "a;';';\";\";--;\n;/*;\n*/;//;\na;";
        source = new ScriptReader(new StringReader(s));
        assertEquals("a", source.readStatement());
        assertEquals("';'", source.readStatement());
        assertEquals("\";\"", source.readStatement());
        assertEquals("--;\n", source.readStatement());
        assertEquals("/*;\n*/", source.readStatement());
        assertEquals("//;\na", source.readStatement());
        assertEquals(null, source.readStatement());
        source.close();

        s = "/\n$ \n\n $';$$a$$ $\n;'";
        source = new ScriptReader(new StringReader(s));
        assertEquals("/\n$ \n\n $';$$a$$ $\n;'", source.readStatement());
        assertEquals(null, source.readStatement());
        source.close();

        s = "//";
        source = new ScriptReader(new StringReader(s));
        assertEquals("//", source.readStatement());
        assertTrue(source.isInsideRemark());
        assertFalse(source.isBlockRemark());
        source.close();

        // check handling of unclosed block comments
        s = "/*xxx";
        source = new ScriptReader(new StringReader(s));
        assertEquals("/*xxx", source.readStatement());
        assertTrue(source.isBlockRemark());
        source.close();

        s = "/*xxx*";
        source = new ScriptReader(new StringReader(s));
        assertEquals("/*xxx*", source.readStatement());
        assertTrue(source.isBlockRemark());
        source.close();

        s = "/*xxx* ";
        source = new ScriptReader(new StringReader(s));
        assertEquals("/*xxx* ", source.readStatement());
        assertTrue(source.isBlockRemark());
        source.close();

        s = "/*xxx/";
        source = new ScriptReader(new StringReader(s));
        assertEquals("/*xxx/", source.readStatement());
        assertTrue(source.isBlockRemark());
        source.close();

        // nested comments
        s = "/*/**/SCRIPT;*/";
        source = new ScriptReader(new StringReader(s));
        assertEquals("/*/**/SCRIPT;*/", source.readStatement());
        assertTrue(source.isBlockRemark());
        source.close();

        s = "/* /* */ SCRIPT; */";
        source = new ScriptReader(new StringReader(s));
        assertEquals("/* /* */ SCRIPT; */", source.readStatement());
        assertTrue(source.isBlockRemark());
        source.close();
    }

}