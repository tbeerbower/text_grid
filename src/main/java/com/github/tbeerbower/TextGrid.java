package com.github.tbeerbower;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.lang.String.*;
import static java.util.stream.IntStream.*;
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


    // ***** inner class : Builder ********************************************

    public static class Builder {

        private static final CellText[] EMPTY_CELL_TEXTS = {};

        private final List<CellText[]> cellTextArrays = new ArrayList<>();
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

        public Builder(int gridWidth) {
            this(gridWidth, false, false);
        }

        public Builder(int gridWidth, boolean enableRowAttributes, boolean enableColumnAttributes) {
            this.gridWidth = gridWidth;
            this.enableRowAttributes = enableRowAttributes;
            this.enableColumnAttributes = enableColumnAttributes;
            this.cellHeights = new int[]{MIN_CELL_HEIGHT};
            this.cellWidths = new int[gridWidth];
            IntStream.range(0, gridWidth).forEach(i -> this.cellWidths[i] = MIN_CELL_WIDTH);
        }

        public Builder addCell(CellText... textLines) {
            int row = cellTextArrays.size() / gridWidth;
            int col = cellTextArrays.size() % gridWidth;
            adjustCellDimensions(row, col, textLines);
            cellTextArrays.add(textLines);
            return this;
        }

        public Builder addCell(String... textLines) {
            return addCell(Arrays.stream(textLines).map(BasicCellText::new).toArray(CellText[]::new));
        }

        public Builder setCell(int row, int col, CellText... textLines) throws IllegalArgumentException {
            if (col >= gridWidth) {
                throw new IllegalArgumentException(String.format("Grid width is %d.  Valid values for col are 0 - %d.", gridWidth, gridWidth - 1));
            }
            adjustCellDimensions(row, col, textLines);
            int index = row * gridWidth + col;
            if (index >= cellTextArrays.size()) {
                IntStream.rangeClosed(cellTextArrays.size(), index).forEach(i ->
                    cellTextArrays.add(i, new TextGrid.CellText[]{}));
            }
            cellTextArrays.set(index, textLines);
            return this;
        }

        public Builder setCell(int row, int col, String... textLines) throws IllegalArgumentException {
            return setCell(row, col, Arrays.stream(textLines).map(BasicCellText::new).toArray(CellText[]::new));
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

        public TextGrid generate() {
            gridHeight = cellTextArrays.size() / gridWidth + (cellTextArrays.size() % gridWidth == 0 ? 0 : 1);
            return new TextGrid(range(0, gridHeight).mapToObj(this::generateCellRow).collect(Collectors.toList()));
        }

        private String[] generateCellRow(int row) {
            int height = getPaddedCellHeight(row);
            return range(0, gridWidth).mapToObj(col -> generateCell(row, col, getCellText(row * gridWidth + col), height, getPaddedCellWidth(col)))
                .reduce((cell1, cell2) -> range(0, height + (isLastRow(row) ? 2 : 1)).mapToObj(i ->
                    format("%s%s", cell1[i], cell2[i])).toArray(String[]::new))
                .orElse(new String[0]);
        }

        private String[] generateCell(int row, int col, CellText[] cellText, int height, int width) {
            boolean isLastCol = col == gridWidth - 1;
            String topAndBottom = format("+%s%s", "-".repeat(width), isLastCol ? "+" : "");
            Stream<String> centerStream = range(0, height).mapToObj(textRow ->
                format("|%s%s", getDisplayText(cellText, row, textRow, width), isLastCol ? "|" : ""));

            return (isLastRow(row) ?
                of(of(topAndBottom), centerStream, of(topAndBottom)) :
                of(of(topAndBottom), centerStream)).flatMap(Function.identity()).toArray(String[]::new);
        }

        private CellText[] getCellText(int index) {
            return index < cellTextArrays.size() ? cellTextArrays.get(index) : EMPTY_CELL_TEXTS;
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

        private String getDisplayText(CellText[] cellTexts, int gridRow, int textRow, int width) {
            if (cellTexts.length < getCellHeight(gridRow) && verticalAlign != VerticalAlign.TOP) {
                textRow -= verticalAlign == VerticalAlign.CENTER ?
                    Math.ceil((getCellHeight(gridRow) - cellTexts.length + verticalCellPadding) / 2.0) :
                    getCellHeight(gridRow) - cellTexts.length + verticalCellPadding;
            } else {
                textRow -= verticalCellPadding;
            }
            return (textRow >= 0 && textRow < getCellHeight(gridRow) && textRow < cellTexts.length) ?
                getDisplayText(cellTexts[textRow], width) : " ".repeat(width);
        }

        private String getDisplayText(CellText cellText, int width) {
            int textWidth = width - horizontalCellPadding * 2;
            return String.format("%s%s%s", " ".repeat(horizontalCellPadding),
                cellText.getDisplayText(textWidth, horizontalAlign), " ".repeat(horizontalCellPadding));
        }

        private boolean isLastRow(int row) {
            return row == gridHeight - 1;
        }
    }


    // ***** inner interface : CellText ***************************************

    public interface CellText {
        String getRawText();

        String getDisplayText(int width, HorizontalAlign horizontalAlign);
    }


    // ***** inner class : BasicCellText **************************************

    public static class BasicCellText implements CellText {

        private final String text;

        public BasicCellText(String text) {
            this.text = text;
        }

        @Override
        public String getRawText() {
            return text;
        }

        @Override
        public String getDisplayText(int width, HorizontalAlign horizontalAlign) {
            return horizontalAlign == HorizontalAlign.CENTER ? centerString(text, width) :
                format(format("%%%s%d.%ds", horizontalAlign == HorizontalAlign.LEFT ? "-" : "", width, width), text);
        }

        private static String centerString(String s, int width) {
            return width > s.length() ?
                format(format("%%-%ds", width), format(format("%%%ds", s.length() + (width - s.length()) / 2), s)) :
                s.substring(0, width);
        }
    }
}
