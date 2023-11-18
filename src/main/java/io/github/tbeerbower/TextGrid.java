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

import java.awt.geom.Area;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.util.stream.IntStream.range;
import static java.util.stream.Stream.of;

public class TextGrid {

    public enum HorizontalAlign {
        LEFT, CENTER, RIGHT
    }

    public enum VerticalAlign {
        TOP, CENTER, BOTTOM
    }

    private static final int MIN_CELL_WIDTH = 1;
    private static final int MIN_CELL_HEIGHT = 1;
    private static final int DEFAULT_MAX_CELL_WIDTH = 40;
    private static final int DEFAULT_MAX_CELL_HEIGHT = 12;

    private final List<String[]> displayLines;

    private TextGrid(List<String[]> displayLines) {
        this.displayLines = displayLines;
    }

    public void show(PrintStream out) {
        for (String[] lines : displayLines) {
            Arrays.stream(lines).forEach(out::println);
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (String[] lines : displayLines) {
            for (String line : lines) {
                builder.append(line).append('\n');
            }
        }
        return builder.toString();
    }

    // ***** inner class : Builder ********************************************

    public static class Builder {

        private static final CellText[] EMPTY_CELL_TEXTS = {};


        public enum BorderCharSet {
            BASIC(new char[]{'+', '+', '+', '+', '+', '+', '+', '+', '+', '-', '|'}),
            HASH(new char[]{'#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#'}),
            HOR(new char[]{'-', '-', '-', '-', '-', '-', '-', '-', '-', '-', ' '}),
            HOR2(new char[]{'=', '=', '=', '=', '=', '=', '=', '=', '=', '=', ' '}),
            VERT(new char[]{'|', '|', '|', '|', '|', '|', '|', '|', '|', ' ', '|'}),
            XASCII_2(new char[]{0x256C, 0x2563, 0x2569, 0x255D, 0x2560, 0x255A, 0x2566, 0x2557, 0x2554, 0x2550, 0x2551}),
            XASCII_HOR2(new char[]{0x2550, 0x2550, 0x2550, 0x2550, 0x2550, 0x2550, 0x2550, 0x2550, 0x2550, 0x2550, ' '}),
            XASCII_VERT2(new char[]{0x2551, 0x2551, 0x2551, 0x2551, 0x2551, 0x2551, 0x2551, 0x2551, 0x2551, ' ', 0x2551});
            private final char[] chars;

            BorderCharSet(char[] chars) {
                this.chars = chars;
            }

            private String get(int index) {
                return String.valueOf(chars[index]);
            }
        }

        private static final int[] CORNER_CODES = {
                0, 1, 0, 0, 2, 3, 2, 0,
                0, 1, 0, 0, 0, 0, 0, 3,
                0, 0, 4, 0, 2, 2, 5, 0,
                0, 0, 4, 0, 0, 0, 0, 5,
                0, 1, 0, 0, 0, 1, 0, 0,
                6, 7, 6, 0, 0, 0, 0, 7,
                0, 0, 4, 0, 0, 0, 4, 0,
                6, 6, 8, 0, 0, 0, 0, 8,
        };

        private final List<Cell> cells = new ArrayList<>();
        private final Map<String, Cell> cellMap = new HashMap<>();
        private VerticalAlign verticalAlign = VerticalAlign.CENTER;
        private HorizontalAlign horizontalAlign = HorizontalAlign.CENTER;
        private final boolean enableColumnAttributes;
        private final boolean enableRowAttributes;
        private final int[] cellWidths;
        private int[] cellHeights;
        private int maxCellWidth = DEFAULT_MAX_CELL_WIDTH;
        private int maxCellHeight = DEFAULT_MAX_CELL_HEIGHT;
        private final int gridWidth;
        private int gridHeight;
        private int verticalCellPadding = 0;
        private int horizontalCellPadding = 0;
        private boolean hasBorder = true;
        private TextEffect fillEffect = null;
        private BorderCharSet borderCharSet = BorderCharSet.BASIC;

        private String[][] template = null;


        public Builder(String[][] template) {
            this(template[0].length, false, false, false);
            this.template = template;
        }


        public Builder(int gridWidth) {
            this(gridWidth, true);
        }

        public Builder(int gridWidth, boolean hasBorder) {
            this(gridWidth, false, false, hasBorder);
        }

        public Builder(int gridWidth, boolean enableRowAttributes, boolean enableColumnAttributes, boolean hasBorder) {
            this.gridWidth = gridWidth;
            this.enableRowAttributes = enableRowAttributes;
            this.enableColumnAttributes = enableColumnAttributes;
            this.cellHeights = new int[]{MIN_CELL_HEIGHT};
            this.cellWidths = new int[gridWidth];
            this.hasBorder = hasBorder;
            IntStream.range(0, gridWidth).forEach(i -> this.cellWidths[i] = MIN_CELL_WIDTH);
        }

        public Builder putCell(String label, Cell cell) {
            cellMap.put(label, cell);
            adjustCellDimensions(0, 0, cell.getTextLines());
            return this;
        }

        public Builder putCell(String label, CellText... textLines) throws IllegalArgumentException {
            return putCell(label, new Cell(textLines));
        }

        public Builder putCell(String label, TextEffect effect, CellText... textLines) throws IllegalArgumentException {
            return putCell(label, new Cell(effect, textLines));
        }

        public Builder putCell(String label, String... textLines) throws IllegalArgumentException {
            return putCell(label, Arrays.stream(textLines).map(CellText::new).toArray(CellText[]::new));
        }

        public Builder putCell(String label, TextEffect effect, String... textLines) throws IllegalArgumentException {
            return putCell(label, effect, Arrays.stream(textLines).map(CellText::new).toArray(CellText[]::new));
        }

        public Builder addCell(Cell cell) {
            int row = cells.size() / gridWidth;
            int col = cells.size() % gridWidth;
            adjustCellDimensions(row, col, cell.getTextLines());
            cells.add(cell);
            return this;
        }

        public Builder addCell(CellText... textLines) {
            return addCell(new Cell(textLines));
        }

        public Builder addCell(TextEffect fillEffect, CellText... textLines) {
            return addCell(new Cell(fillEffect, textLines));
        }

        public Builder addCell(String... textLines) {
            return addCell(Arrays.stream(textLines).map(CellText::new).toArray(CellText[]::new));
        }

        public Builder addCell(TextEffect fillEffect, String... textLines) {
            return addCell(fillEffect, Arrays.stream(textLines).map(CellText::new).toArray(CellText[]::new));
        }

        public Builder setCell(int row, int col, Cell cell) throws IllegalArgumentException {
            if (col >= gridWidth) {
                throw new IllegalArgumentException(String.format("Grid width is %d.  Valid values for col are 0 - %d.", gridWidth, gridWidth - 1));
            }
            adjustCellDimensions(row, col, cell.getTextLines());
            int index = row * gridWidth + col;
            if (index >= cells.size()) {
                IntStream.rangeClosed(cells.size(), index).forEach(i ->
                    cells.add(i, new Cell()));
            }
            cells.set(index, cell);
            return this;
        }

        public Builder setCell(int row, int col, CellText... textLines) throws IllegalArgumentException {
            return setCell(row, col, new Cell(textLines));
        }

        public Builder setCell(int row, int col, String... textLines) throws IllegalArgumentException {
            return setCell(row, col, Arrays.stream(textLines).map(CellText::new).toArray(CellText[]::new));
        }

        public Builder setMaxCellWidth(int maxCellWidth) {
            this.maxCellWidth = maxCellWidth;
            return this;
        }

        public Builder setMaxCellHeight(int maxCellHeight) {
            this.maxCellHeight = maxCellHeight;
            return this;
        }

        public Builder setVerticalAlignment(VerticalAlign verticalAlign) {
            this.verticalAlign = verticalAlign;
            return this;
        }

        public Builder setHorizontalAlign(HorizontalAlign horizontalAlign) {
            this.horizontalAlign = horizontalAlign;
            return this;
        }

        public Builder setVerticalCellPadding(int verticalCellPadding) {
            this.verticalCellPadding = verticalCellPadding;
            return this;
        }

        public Builder setHorizontalCellPadding(int horizontalCellPadding) {
            this.horizontalCellPadding = horizontalCellPadding;
            return this;
        }

        public Builder setHasBorder(boolean hasBorder) {
            this.hasBorder = hasBorder;
            return this;
        }

        public Builder setFillEffect(TextEffect fillEffect) {
            this.fillEffect = fillEffect;
            return this;
        }

        public Builder setBorderCharSet(BorderCharSet borderCharSet) {
            this.borderCharSet = borderCharSet;
            return this;
        }




        private static class GridArea {

            private int baseLineIndex;

            private String[] lines;

            private int horizontalFr;
            private int verticalFr;

            private boolean firstRow;
            private boolean lastRow;
            private boolean firstCol;
            private boolean lastCol;

            private int currentIndex;


            @Override
            public String toString() {
                return "GridArea{" +
                        "baseLineIndex=" + baseLineIndex +
                        ", horizontalFr=" + horizontalFr +
                        ", verticalFr=" + verticalFr +
                        ", lines=" + Arrays.toString(lines) +
                        '}';
            }
        }






        public TextGrid generate() {



            if (template != null) {
                gridHeight = template.length;
                int paddedCellWidth = getPaddedCellWidth(0);
                int paddedCellHeight = getPaddedCellHeight(0);

                Map<String, GridArea> areaMap = new HashMap<>();

                for (int i = 0; i < template.length; ++i) {
                    String[] templateLine = template[i];
                    for (int j = 0; j < templateLine.length; ++j) {
                        String templateLabel = templateLine[j];
                        GridArea area = areaMap.get(templateLabel);
                        if (area == null) {
                            area = new GridArea();
                            area.baseLineIndex = i;
                            area.horizontalFr = 0;
                            area.verticalFr = 1;
                            for (int ii = i + 1; ii < template.length && template[ii][j].equals(templateLabel); ++ii) {
                                area.verticalFr++;
                            }
                            Cell cell = cellMap.get(templateLabel);
                            areaMap.put(templateLabel, area);
                        }
                        if (i == area.baseLineIndex) {
                            area.horizontalFr++;
                        }
                        if ( j == 0 ) {
                            area.firstCol = true;
                        }
                        if (j == templateLine.length - 1) {
                            area.lastCol = true;
                        }
                        if( i == 0 ) {
                            area.firstRow = true;
                        }
                        if (i == template.length -1) {
                            area.lastRow = true;
                        }
                    }
                }
                for (Map.Entry<String, GridArea> entry : areaMap.entrySet()) {
                    Cell cell = cellMap.get(entry.getKey());
                    GridArea area = entry.getValue();

                    int row = area.lastRow ? gridHeight - 1 : area.firstRow ? 0 : 1;
                    int col = area.lastCol ? gridWidth - 1 : area.firstCol ? 0 : 1;
                    area.lines = generateCell(row, col, paddedCellHeight * area.verticalFr + area.verticalFr - 1, paddedCellWidth * area.horizontalFr + area.horizontalFr - 1, cell.getTextLines(), cell.getFillEffect());

                }


                List<String> gridLineList = new ArrayList<>();
                for (int i = 0; i < template.length; ++i) {


                    String[] templateLine = template[i];

                   // System.out.printf("i=%d, row=%d, templateLine=%s %n", i, row, Arrays.toString(templateLine));

                    int linesPerRow = 0;
                    for (int j = 0; j < templateLine.length; ++j) {
                        String templateLabel = templateLine[j];
                        GridArea area = areaMap.get(templateLabel);
                        int linesLeft = area.lines.length - area.currentIndex;
                        if (linesPerRow == 0 || linesLeft < linesPerRow) {
                            linesPerRow = linesLeft;
                        }
                    }


                    for (int rowLine = 0; rowLine < linesPerRow; ++rowLine ) {
                        StringBuilder sb = new StringBuilder();

                        String lastLabel = "";
                        for (int j = 0; j < templateLine.length; ++j) {
                            String templateLabel = templateLine[j];
                            if (!templateLabel.equals(lastLabel)) {
                                GridArea area = areaMap.get(templateLabel);
                                //           System.out.printf("j=%d, templateLabel=%s, areaRow=%d, area=%s %n", j, templateLabel, areaRow, area);
                                sb.append(area.lines[area.currentIndex++]);
                                lastLabel = templateLabel;
                            }
                        }
                        gridLineList.add(sb.toString());
                    }
                }



                return new TextGrid(Collections.singletonList(gridLineList.toArray(new String[]{})));

  //              return new TextGrid(range(0, gridLines).mapToObj(this::generateGridLine).collect(Collectors.toList()));

//                List<String[]> lines = new ArrayList<>();
//                return new TextGrid(lines);
            }



            gridHeight = cells.size() / gridWidth + (cells.size() % gridWidth == 0 ? 0 : 1);
            return new TextGrid(range(0, gridHeight).mapToObj(this::generateCellRow).collect(Collectors.toList()));
        }

        /**
         * Utility to generate a table of all the border character sets.
         */
        public static String displayBorderChars() {
            TextEffect nameEffects = new TextEffect(TextEffect.Code.BACKGROUND_PURPLE, TextEffect.Code.BLACK);
            TextEffect nameBorderEffects = new TextEffect(TextEffect.Code.BACKGROUND_PURPLE, TextEffect.Code.WHITE);
            TextEffect charEffects = new TextEffect(TextEffect.Code.BACKGROUND_CYAN, TextEffect.Code.BLACK);
            TextEffect charBorderEffects = new TextEffect(TextEffect.Code.BACKGROUND_CYAN, TextEffect.Code.WHITE);
            Builder builder = new Builder(BorderCharSet.BASIC.chars.length + 1, false, true, true).
                    setBorderCharSet(BorderCharSet.XASCII_HOR2).setHorizontalCellPadding(1);
            for (BorderCharSet set : BorderCharSet.values()) {
                builder.addCell(nameBorderEffects, nameEffects.apply(set.name()));
                for (char c : set.chars) {
                    builder.addCell(charBorderEffects, charEffects.apply(String.valueOf(c)));
                }
            }
            return builder.generate().toString();
        }

        // ***** Helper Methods ***********************************************

        private String[] generateCellRow(int row) {
            int height = getPaddedCellHeight(row);
            return range(0, gridWidth).mapToObj(col -> generateCell(row, col,
                            height, getPaddedCellWidth(col), getCellText(row * gridWidth + col),
                            getCellFillEffect(row * gridWidth + col)))
                .reduce((cell1, cell2) -> range(0, hasBorder ? (height + (row == 0 ? 2 : 1)) : height).mapToObj(i ->
                    format("%s%s", cell1[i], cell2[i])).toArray(String[]::new))
                .orElse(new String[0]);
        }

        private String[] generateCell(int row, int col, int height, int width, CellText[] cellText, TextEffect cellFillEffect) {
            boolean isLastCol = col == gridWidth - 1;
            if (hasBorder) {
                return generateCellWithBorders(row, col, isLastRow(row), isLastCol, height, width, cellText, cellFillEffect);
            }
            Stream<String> centerStream = range(0, height).mapToObj(textRow ->
                    format("%s", getDisplayText(cellText, row, textRow, width, cellFillEffect)));

            return (of(centerStream)).flatMap(Function.identity()).toArray(String[]::new);
        }

        private String[] generateCellWithBorders(int row, int col, boolean isLastRow, boolean isLastCol, int height, int width, CellText[] cellText, TextEffect cellFillEffect) {
            boolean isFirstRow = row == 0;
            boolean ifFirstCol = col == 0;

            String topLeftChar = cornerChar(cellFillEffect, true, true, isFirstRow, isLastRow, ifFirstCol, isLastCol);
            String bottomLeftChar = cornerChar(cellFillEffect, false, true, isFirstRow, isLastRow, ifFirstCol, isLastCol);
            String topRightChar = cornerChar(cellFillEffect, true, false, isFirstRow, isLastRow, ifFirstCol, isLastCol);
            String bottomRightChar = cornerChar(cellFillEffect, false, false, isFirstRow, isLastRow, ifFirstCol, isLastCol);
            String verticalChar = verticalChar(cellFillEffect);
            String horizontalChar = horizontalChar(cellFillEffect);

            String top = format("%s%s%s", topLeftChar, horizontalChar.repeat(width), isLastCol ? topRightChar : "");
            String bottom = format("%s%s%s", bottomLeftChar, horizontalChar.repeat(width), isLastCol ? bottomRightChar : "");
            Stream<String> centerStream = range(0, height).mapToObj(textRow ->
                    format("%s%s%s", verticalChar, getDisplayText(cellText, row, textRow, width, cellFillEffect),
                            isLastCol ? verticalChar : ""));

            return (isFirstRow ?
                    of(of(top), centerStream, of(bottom)) : of(centerStream, of(bottom))).
                    flatMap(Function.identity()).toArray(String[]::new);
        }

        private String verticalChar(TextEffect cellFillEffect) {
            return applyEffect(cellFillEffect, borderCharSet.get(10));
        }

        private String horizontalChar(TextEffect cellFillEffect) {
            return applyEffect(cellFillEffect, borderCharSet.get(9));
        }

        private String cornerChar(TextEffect cellFillEffect, boolean... bits) {
            int key = 0;
            for (boolean bit : bits) {
                key = (key << 1) + (bit ? 1 : 0);
            }
            return applyEffect(cellFillEffect, String.valueOf(borderCharSet.get(CORNER_CODES[key])));
        }

        private String applyEffect(TextEffect cellFillEffect, String cc) {
            TextEffect effect = cellFillEffect == null ? fillEffect : cellFillEffect;
            return effect == null ? cc : effect.apply(cc);
        }

        private CellText[] getCellText(int index) {
            return index < cells.size() ? cells.get(index).getTextLines() : EMPTY_CELL_TEXTS;
        }

        private TextEffect getCellFillEffect(int index) {
            return index < cells.size() ? cells.get(index).getFillEffect() : null;
        }

        private int getCellHeight(int row) {
            return cellHeights[enableRowAttributes ? row : 0];
        }

        private int getPaddedCellHeight(int row) {
            return getCellHeight(row) + verticalCellPadding * 2;
        }

        private int getPaddedCellWidth(int col) {
            return cellWidths[enableColumnAttributes ? col : 0] + horizontalCellPadding * 2;
        }

        private void adjustCellDimensions(int row, int col, CellText[] textLines) {
            row = enableRowAttributes ? row : 0;
            final int finalCol = enableColumnAttributes ? col : 0;
            Arrays.stream(textLines).forEachOrdered(cellText ->
                cellWidths[finalCol] = Math.min(maxCellWidth, Math.max(cellWidths[finalCol], cellText.getRawText().length())));
            int heights = cellHeights.length;
            if (heights <= row) {
                cellHeights = Arrays.copyOf(cellHeights, row + 1);
                IntStream.rangeClosed(heights, row).forEach(i -> cellHeights[i] = MIN_CELL_HEIGHT);
            }
            cellHeights[row] = Math.min(maxCellHeight, Math.max(cellHeights[row], textLines.length));
        }

        private String getDisplayText(CellText[] cellTexts, int gridRow, int textRow, int width, TextEffect cellFillEffect) {
            if (cellTexts.length < getCellHeight(gridRow) && verticalAlign != VerticalAlign.TOP) {
                textRow -= verticalAlign == VerticalAlign.CENTER ?
                    Math.ceil((getCellHeight(gridRow) - cellTexts.length + verticalCellPadding) / 2.0) :
                    getCellHeight(gridRow) - cellTexts.length + verticalCellPadding;
            } else {
                textRow -= verticalCellPadding;
            }
            TextEffect effect = cellFillEffect == null ? fillEffect : cellFillEffect;
            String fill = effect == null ? " ".repeat(width) : effect.apply(" ".repeat(width));

            return (textRow >= 0 && textRow < getCellHeight(gridRow) && textRow < cellTexts.length) ?
                getDisplayText(cellTexts[textRow], width, cellFillEffect) : fill;
        }

        private String getDisplayText(CellText cellText, int width, TextEffect cellFillEffect) {
            int textWidth = width - horizontalCellPadding * 2;
            TextEffect effect = cellFillEffect == null ? fillEffect : cellFillEffect;
            String fill = effect == null ? " ".repeat(horizontalCellPadding) :
                    effect.apply(" ".repeat(horizontalCellPadding));
            return String.format("%s%s%s", fill, cellText.getDisplayText(textWidth, horizontalAlign, horizontalChar(cellFillEffect), cellFillEffect), fill);
        }

        private boolean isLastRow(int row) {
            return row == gridHeight - 1;
        }
    }

    // ***** inner class : Cell ***********************************************

    public static class Cell {
        private final CellText[] textLines;
        private final TextEffect fillEffect;

        public Cell(CellText... textLines) {
            this(null, textLines);
        }
        public Cell(TextEffect fillEffect, CellText... textLines) {
            this.fillEffect = fillEffect;
            this.textLines = textLines;
        }

        public CellText[] getTextLines() {
            return textLines;
        }

        public TextEffect getFillEffect() {
            return fillEffect;
        }
    }
    
    // ***** inner class : CellText *******************************************

    public static class CellText {

        private final String text;
        private final TextEffect effect;

        public CellText(String text) {
            this(TextEffect.decodeText(text), TextEffect.decode(text));
        }

        public CellText(String text, TextEffect effect) {
            this.text = text;
            this.effect = effect;
        }
        public String getRawText() {
            return text;
        }

        public String getDisplayText(int width, HorizontalAlign horizontalAlign, String horizontalChar, TextEffect cellFillEffect) {
            String displayText = horizontalAlign == HorizontalAlign.CENTER ? centerString(text, width) :
                format(format("%%%s%d.%ds", horizontalAlign == HorizontalAlign.LEFT ? horizontalChar : "", width, width), text);
            TextEffect cellEffect = effect == null || effect.getCodes().length == 0 ? cellFillEffect : effect;

            return cellEffect == null ? displayText : cellEffect.apply(displayText);
        }

        private static String centerString(String s, int width) {
            return width > s.length() ?
                format(format("%%-%ds", width), format(format("%%%ds", s.length() + (width - s.length()) / 2), s)) :
                s.substring(0, width);
        }
    }
}
