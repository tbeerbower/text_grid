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

public class App {
    public static void main(String[] args) {
 //       System.out.println(TextGrid.Builder.displayBorderChars());

        TextEffect effectA = new TextEffect(TextEffect.Code.BACKGROUND_BLUE);
        TextEffect effectB = new TextEffect(TextEffect.Code.BACKGROUND_RED);
        TextEffect effectC = new TextEffect(TextEffect.Code.BACKGROUND_GREEN);
        TextEffect effectD = new TextEffect(TextEffect.Code.BACKGROUND_PURPLE);
        TextEffect effectE = new TextEffect(TextEffect.Code.BACKGROUND_YELLOW);
        TextEffect effectF = new TextEffect(TextEffect.Code.BACKGROUND_CYAN);

        TextGrid.Builder builder = new TextGrid.Builder( new String [][] {
                {"a", "a", "b", "c"},
                {"a", "a", "d", "c"},
                {"e", "e", "e", "c"},
                {"f", "f", "f", "f"}});

        builder.setVerticalCellPadding(0).setHorizontalCellPadding(0).
                setVerticalAlignment(TextGrid.VerticalAlign.CENTER).setHorizontalAlign(TextGrid.HorizontalAlign.CENTER).
                setHasBorder(true);

        builder.putCell("a", effectA,"This", "is", "A").
                putCell("b", effectB,"B").
                putCell("c", effectC,"This", "is", "C").
                putCell("d", effectD,"D").
                putCell("e", effectE,"This is E").
                putCell("f", effectF,"This is FFFFF");

        TextGrid grid = builder.generate();
        System.out.println(grid);


    }
}
