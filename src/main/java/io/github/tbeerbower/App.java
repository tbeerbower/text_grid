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
package io.github.tbeerbower;

import java.util.Arrays;

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


        TextEffect effectLY = new TextEffect(TextEffect.Code.BACKGROUND_WHITE, TextEffect.Code.BLACK);
        TextEffect effectLX = new TextEffect(TextEffect.Code.BACKGROUND_WHITE, TextEffect.Code.BLACK);
        TextEffect effectNe = new TextEffect(TextEffect.Code.BACKGROUND_GREEN);
        TextEffect effectMi = new TextEffect(TextEffect.Code.BACKGROUND_PURPLE);
        TextEffect effectWe = new TextEffect(TextEffect.Code.BACKGROUND_YELLOW);
        TextEffect effectSo = new TextEffect(TextEffect.Code.BACKGROUND_CYAN);
        TextEffect effectBl = new TextEffect(TextEffect.Code.CYAN);

        Bar[] bars = new Bar[] {
                new Bar("NoEast", 57, effectNe),
                new Bar("MIWest", 69, effectMi),
                new Bar("West--", 79, effectWe),
                new Bar("South-", 126, effectSo)
        };

        double max = 130.0;

        String [][] template = new String[15][bars.length * 3 + 3 ];

        for (int i = 0; i < template.length - 1; i++) {
            String[] row = template[i];
            double level = 1.0 - (i / (double)(template.length - 1));

            row[0] = "labelY";
            row[1] = String.format("val%02d", i);
            for (int j = 2; j < row.length - 1; j+=3) {
                row[j] = String.format("b%02d-%02d", i, j);
                boolean showBar = bars[j / 3].value / max >= level;
                row[j + 1] = showBar ? bars[j / 3].label : String.format("b%02d-%02d", i, j + 1);
                row[j + 2] = showBar ? bars[j / 3].label : String.format("b%02d-%02d", i, j + 2);
            }
            row[row.length - 1] = String.format("b%02d-%02d", i, row.length - 1);
        }
        for (int i = 0; i < template[0].length; i++) {
            template[template.length - 1][i] = "labelX";
        }

//        for(String[] row : template) {
//            System.out.println(Arrays.toString(row));
//        }


        builder = new TextGrid.Builder(template);

        builder.setVerticalCellPadding(0).setHorizontalCellPadding(0).
                setVerticalAlignment(TextGrid.VerticalAlign.CENTER).setHorizontalAlign(TextGrid.HorizontalAlign.CENTER).
                setHasBorder(true).setBorderCharSet(TextGrid.Builder.BorderCharSet.HOR);

        builder.putCell("labelY", effectLY, "Population in Millions".split("")).
                putCell("labelX", effectLX, "Regions");

        for (Bar bar : bars) {
            builder.putCell(bar.label, bar.effect, "");
        }
        for (int i = 0; i < template.length - 1; i++) {
            int value = (int) ((1.0 - (i / (double)(template.length - 1)) )* max);

            builder.putCell(String.format("val%02d", i), effectBl, String.format("%3d", value));
            for (int j = 0; j < template[i].length - 1; ++j) {
                builder.putCell(String.format("b%02d-%02d", i, j), effectBl, "");
            }
            builder.putCell(String.format("b%02d-%02d", i, template[i].length -1), effectBl, "");
        }


        grid = builder.generate();
        System.out.println(grid);
    }


    private static class Bar {
        private final String label;
        private final int value;
        private final TextEffect effect;

        public Bar(String label, int value, TextEffect effect) {
            this.label = label;
            this.value = value;
            this.effect = effect;
        }

        public String getLabel() {
            return label;
        }

        public int getValue() {
            return value;
        }

        public TextEffect getEffect() {
            return effect;
        }
    }
}
