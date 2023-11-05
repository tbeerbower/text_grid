/**
 * Copyright (c) 2022-2023 Tom Beerbower
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
 * Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.github.tbeerbower;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TextEffect {

    private static final Pattern DECODE_EFFECTS_PATTERN = Pattern.compile("\\\u001B\\[([\\d;]+)m");
    private static final Pattern DECODE_TEXT_PATTERN = Pattern.compile("\\\u001B\\[([\\d;]+)m(.*)\\\u001B\\[([\\d;]+)m");
    private static final String ESCAPE_FORMAT = "\u001B[%sm%s\u001B[0m";
    private static final Map<String, Code> codeMap = new HashMap<>();
    static {
        for (Code code : Code.values()) {
            codeMap.put(code.getCode(), code);
        }
    }

    public enum Code {
        RESET("0"),
        BOLD("1"),
        ITALIC("3"),
        UNDERLINE("4"),
        REVERSE("7"),
        CROSS_OUT("9"),
        BLACK("30"),
        RED("31"),
        GREEN("32"),
        YELLOW("33"),
        BLUE("34"),
        PURPLE("35"),
        CYAN("36"),
        WHITE("37"),
        BACKGROUND_BLACK("40"),
        BACKGROUND_RED("41"),
        BACKGROUND_GREEN("42"),
        BACKGROUND_YELLOW("43"),
        BACKGROUND_BLUE("44"),
        BACKGROUND_PURPLE("45"),
        BACKGROUND_CYAN("46"),
        BACKGROUND_WHITE("47");

        private final String code;

        Code(String code) {
            this.code = code;
        }
        public String getCode() {
            return code;
        }
    }

    private final Code[] codes;

    public TextEffect(Code... codes) {
        this.codes = codes;
    }

    public Code[] getCodes() {
        return codes;
    }

    public TextEffect join(Code... codes) {
        return new TextEffect(Stream.concat(Arrays.stream(this.codes), Arrays.stream(codes))
            .toArray(size -> (Code[]) Array.newInstance(this.codes.getClass().getComponentType(), size)));
    }

    public String apply(String text) {
        return String.format(ESCAPE_FORMAT,
            Arrays.stream(codes).map(Code::getCode).collect(Collectors.joining(";")),
            text);
    }

    public static TextEffect decode(String text) {
        TextEffect textEffect = new TextEffect();
        Matcher matcher = DECODE_EFFECTS_PATTERN.matcher(text);
        while (matcher.find()) {
            String[] codes = matcher.group(1).split(";");
            // get enum codes
            textEffect = textEffect.join(
                Arrays.stream(codes).filter(code -> codeMap.get(code) != Code.RESET).
                    map(code -> codeMap.get(code)).collect(Collectors.toList()).toArray(new Code[]{}));
        }
        return textEffect;
    }

    public static String decodeText(String text) {
        Matcher matcher = DECODE_TEXT_PATTERN.matcher(text);
        if (matcher.find()) {
            return matcher.group(2);
        }
        return text;
    }
}
