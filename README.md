# text_grid

The TextGrid utility is a Java project that provides a versatile and user-friendly way to create and display text grids in a command-line interface (CLI). It is designed to help developers and users easily generate, customize, and visualize text grids with various formatting options. The project mainly consists of two core classes: `TextGrid` and `TextEffect`, with the `TextGrid` class having several inner classes to facilitate grid creation and customization.

## Project Capabilities

### TextGrid Class
The `TextGrid` class is the core component of this project, offering the following features:

- **Builder Pattern**: It provides a fluent builder pattern through the `Builder` inner class, making it simple to create a grid step by step with customized settings.

- **Customization Options**: You can set various grid parameters, such as width, cell dimensions, border characters, text alignment, and padding.

- **Adding Content**: The builder allows you to add content to the grid by adding cells, making it easy to populate the grid with your data.

- **Display**: The `TextGrid` class can be displayed in the CLI, providing an elegant and visually appealing representation of your data.

### TextEffect Class
The `TextEffect` class is used for applying text effects to individual cells or lines of text within cells. It allows you to customize the appearance of text, including setting colors, bold, underline, and other styling options.

### Example Usage
Here's an example of how to use the `TextGrid.Builder` to create a text grid:

```java
TextGrid.Builder builder = new TextGrid.Builder(3)
    .setMaxCellHeight(5)
    .setMaxCellWidth(7)
    .setHorizontalAlign(TextGrid.HorizontalAlign.CENTER)
    .setVerticalAlignment(TextGrid.VerticalAlign.CENTER)
    .setHorizontalCellPadding(2)
    .setVerticalCellPadding(1);

builder = builder
    .addCell("Apple", "Red", "Green")
    .addCell("Banana", "Yellow")
    .addCell("Grape", "Purple")
    // Add more cells as needed
    .addCell("Zucchini", "Green")
    .addCell("Eggplant", "Purple")
    .addCell("Squash", "Green", "Yellow");

TextGrid grid = builder.generate();
System.out.println(grid);
```

The above code creates a text grid with the specified settings and content, and it displays the grid in the CLI, creating a visually appealing layout for your data.
```
+-----------+-----------+-----------+
|           |           |           |
|           |           |           |
|   Apple   |  Banana   |   Grape   |
|    Red    |  Yellow   |  Purple   |
|   Green   |           |           |
|           |           |           |
|           |           |           |
+-----------+-----------+-----------+
|           |           |           |
|           |           |           |
|  Orange   |  Pineapp  |   Kiwi    |
|  Orange   |   Green   |   Green   |
|           |           |           |
|           |           |           |
|           |           |           |
+-----------+-----------+-----------+
|           |           |           |
|           |  Tomato   |           |
|  Cherry   |    Red    |  Carrot   |
|    Red    |  Yellow   |  Orange   |
|           |   Green   |           |
|           |  Purple   |           |
|           |           |           |
+-----------+-----------+-----------+
|           |           |           |
|           |           |           |
|  Lettuce  |   Bean    |    Pea    |
|   Green   |   Green   |   Green   |
|           |           |           |
|           |           |           |
|           |           |           |
+-----------+-----------+-----------+
|           |           |           |
|           |           |           |
|  Zucchin  |  Eggplan  |  Squash   |
|   Green   |  Purple   |   Green   |
|           |           |  Yellow   |
|           |           |           |
|           |           |           |
+-----------+-----------+-----------+
```

Here's another example using some additional features:
```java
TextGrid grid = new TextGrid.Builder(2).
    setHorizontalAlign(TextGrid.HorizontalAlign.RIGHT).
    setVerticalAlignment(TextGrid.VerticalAlign.TOP).
    setHorizontalCellPadding(1).setVerticalCellPadding(0).
    setBorderCharSet(TextGrid.Builder.BorderCharSet.XASCII_2).
    addCell("one", "two", "three", "Inconsequential", "Hypothetically").
    addCell("four", "five").
    addCell("six", "seven", "eight").
    addCell("nine", "ten").
    generate();

    System.out.println(grid);
```
The above code creates a text grid with the text aligned to the top right and a extended ascii border character set.

```
╔═════════════════╦═════════════════╗
║             one ║            four ║
║             two ║            five ║
║           three ║                 ║
║ Inconsequential ║                 ║
║  Hypothetically ║                 ║
╠═════════════════╬═════════════════╣
║             six ║            nine ║
║           seven ║             ten ║
║           eight ║                 ║
║                 ║                 ║
║                 ║                 ║
╚═════════════════╩═════════════════╝
```
The TextGrid utility is a valuable tool for creating interactive and neatly formatted text grids, suitable for various CLI applications, such as data visualization, reports, or any other use case where structured text presentation is essential.
