package com.github.tbeerbower;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;


import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.fail;

public class TextGridTest {

    private static final String SET_CELL_GRID =
        "+-----------------+-----------------+-----------------+-----------------+" +  System.lineSeparator() +
            "|             one |                 |                 |                 |" +  System.lineSeparator() +
            "|             two |                 |                 |                 |" +  System.lineSeparator() +
            "|           three |                 |                 |                 |" +  System.lineSeparator() +
            "| Inconsequential |                 |                 |                 |" +  System.lineSeparator() +
            "|  Hypothetically |                 |                 |                 |" +  System.lineSeparator() +
            "+-----------------+-----------------+-----------------+-----------------+" +  System.lineSeparator() +
            "|                 |                 |                 |                 |" +  System.lineSeparator() +
            "|                 |                 |                 |                 |" +  System.lineSeparator() +
            "|                 |                 |                 |                 |" +  System.lineSeparator() +
            "|                 |            four |                 |                 |" +  System.lineSeparator() +
            "|                 |            five |                 |                 |" +  System.lineSeparator() +
            "+-----------------+-----------------+-----------------+-----------------+" +  System.lineSeparator() +
            "|                 |                 |                 |                 |" +  System.lineSeparator() +
            "|                 |                 |                 |                 |" +  System.lineSeparator() +
            "|                 |                 |             six |                 |" +  System.lineSeparator() +
            "|                 |                 |           seven |                 |" +  System.lineSeparator() +
            "|                 |                 |           eight |                 |" +  System.lineSeparator() +
            "+-----------------+-----------------+-----------------+-----------------+" +  System.lineSeparator() +
            "|                 |                 |                 |                 |" +  System.lineSeparator() +
            "|                 |                 |                 |                 |" +  System.lineSeparator() +
            "|                 |                 |                 |                 |" +  System.lineSeparator() +
            "|                 |                 |                 |            nine |" +  System.lineSeparator() +
            "|                 |                 |                 |             ten |" +  System.lineSeparator() +
            "+-----------------+-----------------+-----------------+-----------------+" +  System.lineSeparator();

    private static final String BOTTOM_RIGHT_GRID =
        "+-----------------+-----------------+" +  System.lineSeparator() +
            "|             one |                 |" +  System.lineSeparator() +
            "|             two |                 |" +  System.lineSeparator() +
            "|           three |                 |" +  System.lineSeparator() +
            "| Inconsequential |            four |" +  System.lineSeparator() +
            "|  Hypothetically |            five |" +  System.lineSeparator() +
            "+-----------------+-----------------+" +  System.lineSeparator() +
            "|                 |                 |" +  System.lineSeparator() +
            "|                 |                 |" +  System.lineSeparator() +
            "|             six |                 |" +  System.lineSeparator() +
            "|           seven |            nine |" +  System.lineSeparator() +
            "|           eight |             ten |" +  System.lineSeparator() +
            "+-----------------+-----------------+" +  System.lineSeparator();

    private static final String TOP_RIGHT_GRID =
        "+-----------------+-----------------+" +  System.lineSeparator() +
            "|             one |            four |" +  System.lineSeparator() +
            "|             two |            five |" +  System.lineSeparator() +
            "|           three |                 |" +  System.lineSeparator() +
            "| Inconsequential |                 |" +  System.lineSeparator() +
            "|  Hypothetically |                 |" +  System.lineSeparator() +
            "+-----------------+-----------------+" +  System.lineSeparator() +
            "|             six |            nine |" +  System.lineSeparator() +
            "|           seven |             ten |" +  System.lineSeparator() +
            "|           eight |                 |" +  System.lineSeparator() +
            "|                 |                 |" +  System.lineSeparator() +
            "|                 |                 |" +  System.lineSeparator() +
            "+-----------------+-----------------+" +  System.lineSeparator();

    private static final String BOTTOM_LEFT_GRID =
        "+-----------------+-----------------+" +  System.lineSeparator() +
            "| one             |                 |" +  System.lineSeparator() +
            "| two             |                 |" +  System.lineSeparator() +
            "| three           |                 |" +  System.lineSeparator() +
            "| Inconsequential | four            |" +  System.lineSeparator() +
            "| Hypothetically  | five            |" +  System.lineSeparator() +
            "+-----------------+-----------------+" +  System.lineSeparator() +
            "|                 |                 |" +  System.lineSeparator() +
            "|                 |                 |" +  System.lineSeparator() +
            "| six             |                 |" +  System.lineSeparator() +
            "| seven           | nine            |" +  System.lineSeparator() +
            "| eight           | ten             |" +  System.lineSeparator() +
            "+-----------------+-----------------+" +  System.lineSeparator();

    private static final String TOP_LEFT_GRID =
        "+-----------------+-----------------+" +  System.lineSeparator() +
            "| one             | four            |" +  System.lineSeparator() +
            "| two             | five            |" +  System.lineSeparator() +
            "| three           |                 |" +  System.lineSeparator() +
            "| Inconsequential |                 |" +  System.lineSeparator() +
            "| Hypothetically  |                 |" +  System.lineSeparator() +
            "+-----------------+-----------------+" +  System.lineSeparator() +
            "| six             | nine            |" +  System.lineSeparator() +
            "| seven           | ten             |" +  System.lineSeparator() +
            "| eight           |                 |" +  System.lineSeparator() +
            "|                 |                 |" +  System.lineSeparator() +
            "|                 |                 |" +  System.lineSeparator() +
            "+-----------------+-----------------+" +  System.lineSeparator();

    private static final String COL_ATTR_ENABLED_GRID =
        "+-----------------+------+" +  System.lineSeparator() +
            "| one             | four |" +  System.lineSeparator() +
            "| two             | five |" +  System.lineSeparator() +
            "| three           |      |" +  System.lineSeparator() +
            "| Inconsequential |      |" +  System.lineSeparator() +
            "| Hypothetically  |      |" +  System.lineSeparator() +
            "+-----------------+------+" +  System.lineSeparator() +
            "| six             | nine |" +  System.lineSeparator() +
            "| seven           | ten  |" +  System.lineSeparator() +
            "| eight           |      |" +  System.lineSeparator() +
            "|                 |      |" +  System.lineSeparator() +
            "|                 |      |" +  System.lineSeparator() +
            "+-----------------+------+" +  System.lineSeparator();

    private static final String ROW_ATTR_ENABLED_GRID =
        "+-----------------+-----------------+" +  System.lineSeparator() +
            "| one             | four            |" +  System.lineSeparator() +
            "| two             | five            |" +  System.lineSeparator() +
            "| three           |                 |" +  System.lineSeparator() +
            "| Inconsequential |                 |" +  System.lineSeparator() +
            "| Hypothetically  |                 |" +  System.lineSeparator() +
            "+-----------------+-----------------+" +  System.lineSeparator() +
            "| six             | nine            |" +  System.lineSeparator() +
            "| seven           | ten             |" +  System.lineSeparator() +
            "| eight           |                 |" +  System.lineSeparator() +
            "+-----------------+-----------------+" +  System.lineSeparator();

    private static final String NUMBER_GRID =
        "+------+------+------+------+" +  System.lineSeparator() +
            "|Cell0 |Cell1 |Cell2 |Cell3 |" +  System.lineSeparator() +
            "+------+------+------+------+" +  System.lineSeparator() +
            "|Cell4 |Cell5 |Cell6 |Cell7 |" +  System.lineSeparator() +
            "+------+------+------+------+" +  System.lineSeparator() +
            "|Cell8 |Cell9 |Cell10|Cell11|" +  System.lineSeparator() +
            "+------+------+------+------+" +  System.lineSeparator() +
            "|Cell12|Cell13|Cell14|Cell15|" +  System.lineSeparator() +
            "+------+------+------+------+" +  System.lineSeparator() +
            "|Cell16|      |      |      |" +  System.lineSeparator() +
            "+------+------+------+------+" +  System.lineSeparator();

    private static final String FRUIT_VEGGIE_GRID =
        "+-----------+-----------+-----------+" +  System.lineSeparator() +
            "|           |           |           |" +  System.lineSeparator() +
            "|           |           |           |" +  System.lineSeparator() +
            "|   Apple   |  Banana   |   Grape   |" +  System.lineSeparator() +
            "|    Red    |  Yellow   |  Purple   |" +  System.lineSeparator() +
            "|   Green   |           |           |" +  System.lineSeparator() +
            "|           |           |           |" +  System.lineSeparator() +
            "|           |           |           |" +  System.lineSeparator() +
            "+-----------+-----------+-----------+" +  System.lineSeparator() +
            "|           |           |           |" +  System.lineSeparator() +
            "|           |           |           |" +  System.lineSeparator() +
            "|  Orange   |  Pineapp  |   Kiwi    |" +  System.lineSeparator() +
            "|  Orange   |   Green   |   Green   |" +  System.lineSeparator() +
            "|           |           |           |" +  System.lineSeparator() +
            "|           |           |           |" +  System.lineSeparator() +
            "|           |           |           |" +  System.lineSeparator() +
            "+-----------+-----------+-----------+" +  System.lineSeparator() +
            "|           |           |           |" +  System.lineSeparator() +
            "|           |  Tomato   |           |" +  System.lineSeparator() +
            "|  Cherry   |    Red    |  Carrot   |" +  System.lineSeparator() +
            "|    Red    |  Yellow   |  Orange   |" +  System.lineSeparator() +
            "|           |   Green   |           |" +  System.lineSeparator() +
            "|           |  Purple   |           |" +  System.lineSeparator() +
            "|           |           |           |" +  System.lineSeparator() +
            "+-----------+-----------+-----------+" +  System.lineSeparator() +
            "|           |           |           |" +  System.lineSeparator() +
            "|           |           |           |" +  System.lineSeparator() +
            "|  Lettuce  |   Bean    |    Pea    |" +  System.lineSeparator() +
            "|   Green   |   Green   |   Green   |" +  System.lineSeparator() +
            "|           |           |           |" +  System.lineSeparator() +
            "|           |           |           |" +  System.lineSeparator() +
            "|           |           |           |" +  System.lineSeparator() +
            "+-----------+-----------+-----------+" +  System.lineSeparator() +
            "|           |           |           |" +  System.lineSeparator() +
            "|           |           |           |" +  System.lineSeparator() +
            "|  Zucchin  |  Eggplan  |  Squash   |" +  System.lineSeparator() +
            "|   Green   |  Purple   |   Green   |" +  System.lineSeparator() +
            "|           |           |  Yellow   |" +  System.lineSeparator() +
            "|           |           |           |" +  System.lineSeparator() +
            "|           |           |           |" +  System.lineSeparator() +
            "+-----------+-----------+-----------+" +  System.lineSeparator();

    @Test
    public void show() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(out);

        TextGrid.Builder builder = new TextGrid.Builder(4);
        for (int i = 0; i < 17; ++i) {
            builder = builder.addCell(new TextGrid.CellText("Cell" + i));
        }
        builder.generate().show(printStream);

        Assert.assertEquals(NUMBER_GRID, out.toString());
    }

    @Test
    public void builder() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(out);

        TextGrid.Builder builder = new TextGrid.Builder(3).setMaxCellHeight(5).setMaxCellWidth(7).
            setHorizontalAlign(TextGrid.HorizontalAlign.CENTER).setVerticalAlignment(TextGrid.VerticalAlign.CENTER).
            setHorizontalCellPadding(2).setVerticalCellPadding(1);
        builder = builder.
            addCell("Apple", "Red", "Green").addCell("Banana", "Yellow").addCell("Grape", "Purple").
            addCell("Orange", "Orange").
            addCell("Pineapple", "Green").addCell("Kiwi", "Green").addCell("Cherry", "Red").
            addCell("Tomato", "Red", "Yellow", "Green", "Purple", "Brown").
            addCell("Carrot", "Orange").addCell("Lettuce", "Green").addCell("Bean", "Green").addCell("Pea", "Green").
            addCell("Zucchini", "Green").addCell("Eggplant", "Purple").addCell("Squash", "Green", "Yellow");
        builder.generate().show(printStream);

        Assert.assertEquals(FRUIT_VEGGIE_GRID, out.toString());
    }

    @Test
    public void setCell() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(out);

        new TextGrid.Builder(4).
            setHorizontalAlign(TextGrid.HorizontalAlign.RIGHT).
            setVerticalAlignment(TextGrid.VerticalAlign.BOTTOM).
            setHorizontalCellPadding(1).setVerticalCellPadding(0).
            setCell(0, 0, "one", "two", "three", "Inconsequential", "Hypothetically").
            setCell(1, 1,"four", "five").
            setCell(2, 2, "six", "seven", "eight").
            setCell(3, 3, "nine", "ten").
            generate().show(printStream);

        Assert.assertEquals(SET_CELL_GRID, out.toString());
    }

    @Test
    public void setCell_outOfBounds() throws Exception {
        try {
            new TextGrid.Builder(4).
                setCell(0, 0, "one", "two", "three").
                setCell(1, 1,"four", "five").
                setCell(2, 2, "six", "seven", "eight").
                setCell(3, 4, "nine", "ten");
            fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    public void topLeft() {
        ByteArrayOutputStream out =
            getTextGridOut(TextGrid.HorizontalAlign.LEFT, TextGrid.VerticalAlign.TOP, false, false);
        Assert.assertEquals(TOP_LEFT_GRID, out.toString());
    }

    @Test
    public void bottomLeft() {
        ByteArrayOutputStream out =
            getTextGridOut(TextGrid.HorizontalAlign.LEFT, TextGrid.VerticalAlign.BOTTOM, false, false);
        Assert.assertEquals(BOTTOM_LEFT_GRID, out.toString());
    }

    @Test
    public void topRight() {
        ByteArrayOutputStream out =
            getTextGridOut(TextGrid.HorizontalAlign.RIGHT, TextGrid.VerticalAlign.TOP, false, false);
        Assert.assertEquals(TOP_RIGHT_GRID, out.toString());
    }

    @Test
    public void bottomRight() {
        ByteArrayOutputStream out =
            getTextGridOut(TextGrid.HorizontalAlign.RIGHT, TextGrid.VerticalAlign.BOTTOM, false, false);
        Assert.assertEquals(BOTTOM_RIGHT_GRID, out.toString());
    }

    @Test
    public void topLeft_enableColumnAttributes() {
        ByteArrayOutputStream out =
            getTextGridOut(TextGrid.HorizontalAlign.LEFT, TextGrid.VerticalAlign.TOP, false, true);
        Assert.assertEquals(COL_ATTR_ENABLED_GRID, out.toString());
    }

    @Test
    public void topLeft_enableRowAttributes() {
        ByteArrayOutputStream out =
            getTextGridOut(TextGrid.HorizontalAlign.LEFT, TextGrid.VerticalAlign.TOP, true, false);
        Assert.assertEquals(ROW_ATTR_ENABLED_GRID, out.toString());
    }

    private ByteArrayOutputStream getTextGridOut(TextGrid.HorizontalAlign right, TextGrid.VerticalAlign bottom,
                                                 boolean enableRowAttributes, boolean enableColumnAttributes) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(out);

        new TextGrid.Builder(2, enableRowAttributes, enableColumnAttributes, true).
            setHorizontalAlign(right).
            setVerticalAlignment(bottom).
            setHorizontalCellPadding(1).setVerticalCellPadding(0).
            addCell("one", "two", "three", "Inconsequential", "Hypothetically").
            addCell("four", "five").
            addCell("six", "seven", "eight").
            addCell("nine", "ten").
            generate().show(printStream);
        return out;
    }
}