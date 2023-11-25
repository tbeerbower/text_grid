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

import io.github.tbeerbower.TextEffect;
import io.github.tbeerbower.TextGrid;


public class App {
    public static void main(String[] args) {
        System.out.println(TextGrid.Builder.displayBorderChars());

        TextEffect effectA = new TextEffect(TextEffect.Code.BACKGROUND_BLUE);
        TextEffect effectB = new TextEffect(TextEffect.Code.BACKGROUND_RED);
        TextEffect effectC = new TextEffect(TextEffect.Code.BACKGROUND_GREEN);
        TextEffect effectD = new TextEffect(TextEffect.Code.BACKGROUND_PURPLE);
        TextEffect effectE = new TextEffect(TextEffect.Code.BACKGROUND_YELLOW);
        TextEffect effectF = new TextEffect(TextEffect.Code.BACKGROUND_CYAN);
        TextEffect effectG = new TextEffect(TextEffect.Code.CYAN);
        TextEffect effectH = new TextEffect(TextEffect.Code.GREEN);

        TextGrid.Builder builder = new TextGrid.Builder( new String [][] {
                {"a", "a", "b", "c"},
                {"a", "a", "d", "c"},
                {"e", "e", "e", "c"},
                {"g", "g", "h", "h"},
                {"f", "f", "f", "f"}});

        builder.setVerticalCellPadding(1).setHorizontalCellPadding(2).
                setVerticalAlignment(TextGrid.VerticalAlign.TOP).setHorizontalAlign(TextGrid.HorizontalAlign.CENTER).
                setHasBorder(true).setBorderCharSet(TextGrid.Builder.BorderCharSet.XASCII_2);

        builder.putCell("a", effectA,"This", "is", "A").
                putCell("b", effectB,"B").
                putCell("c", effectC,"This", "is", "C").
                putCell("d", effectD,"D").
                putCell("e", effectE,"This is E").
                putCell("g", effectG,"This is G").
                putCell("h", effectH,"This is H").
                putCell("f", effectF,"This is FFFFF");

        TextGrid grid = builder.generate();
        System.out.println(grid);



        TextEffect effectLY = new TextEffect(TextEffect.Code.BACKGROUND_BLUE);
        TextEffect effectLX = new TextEffect(TextEffect.Code.BACKGROUND_RED);
        TextEffect effectNe = new TextEffect(TextEffect.Code.BACKGROUND_GREEN);
        TextEffect effectMi = new TextEffect(TextEffect.Code.BACKGROUND_PURPLE);
        TextEffect effectWe = new TextEffect(TextEffect.Code.BACKGROUND_YELLOW);
        TextEffect effectSo = new TextEffect(TextEffect.Code.BACKGROUND_CYAN);
        TextEffect effectBl = new TextEffect(TextEffect.Code.CYAN);


        int nePop = 57;
        int miPop = 69;
        int wePop = 79;
        int soPop = 126;
        double max = 130.0;

        String [][] template = new String[15][14];
        String yLabel = "Population";

        for (int i = 0; i < template.length - 1; i++) {
            String[] row = template[i];
            double level = 1.0 - (i / (double)(template.length - 1));

            row[0] = "ly";
            row[1] = "b1";
            row[2] = "b1";
            row[3] = nePop / max  >= level ? "ne" : "b2";
            row[4] = "b3";
            row[5] = "b3";
            row[6] = miPop / max  >= level ? "mi" : "b4";
            row[7] = "b5";
            row[8] = "b5";
            row[9] = wePop / max  >= level ? "we" : "b6";
            row[10] = "b7";
            row[11] = "b7";
            row[12] = soPop / max  >= level ? "so" : "b8";
            row[13] = "b9";
        }
        for (int i = 0; i < template[0].length; i++) {
            template[template.length - 1][i] = "lx";
        }

        builder = new TextGrid.Builder(template);

        builder.setVerticalCellPadding(0).setHorizontalCellPadding(0).
                setVerticalAlignment(TextGrid.VerticalAlign.CENTER).setHorizontalAlign(TextGrid.HorizontalAlign.CENTER).
                setHasBorder(true).setBorderCharSet(TextGrid.Builder.BorderCharSet.HOR);

        builder.putCell("ly", effectLY, "P", "o", "p", "u", "l", "a", "t", "i", "o", "n").
                putCell("lx", effectLX, "Regions").
                putCell("ne", effectNe, "NE", String.format("%3d ",nePop)).
                putCell("mi", effectMi, "MI", String.format("%3d ",miPop)).
                putCell("we", effectWe, "WE", String.format("%3d ",wePop)).
                putCell("so", effectSo, "SO", String.format("%3d ",soPop)).
                putCell("b1", effectBl, "").
                putCell("b2", effectBl, "").
                putCell("b3", effectBl, "").
                putCell("b4", effectBl, "").
                putCell("b5", effectBl, "").
                putCell("b6", effectBl, "").
                putCell("b7", effectBl, "").
                putCell("b8", effectBl, "").
                putCell("b9", effectBl, "");


        grid = builder.generate();
        System.out.println(grid);


    }
}
