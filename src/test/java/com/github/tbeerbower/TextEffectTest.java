package com.github.tbeerbower;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;

public class TextEffectTest {
    @Test
    public void apply() {
        TextEffect textEffect = new TextEffect(TextEffect.Code.RED, TextEffect.Code.BACKGROUND_BLACK,
            TextEffect.Code.UNDERLINE, TextEffect.Code.CROSS_OUT);
        String text = textEffect.apply("HELLO WORLD!!!!");
        TextEffect decodedTextEffect = TextEffect.decode(text);
        Assert.assertEquals(new HashSet(Arrays.asList(textEffect.getCodes())),
            new HashSet(Arrays.asList(decodedTextEffect.getCodes())));
    }

    @Test
    public void decodeText() {
        TextEffect textEffect = new TextEffect(TextEffect.Code.RED, TextEffect.Code.BACKGROUND_BLACK,
                TextEffect.Code.UNDERLINE, TextEffect.Code.CROSS_OUT);
        String text = textEffect.apply("HELLO WORLD!!!!");
        String decodedText = TextEffect.decodeText(text);
        Assert.assertEquals("HELLO WORLD!!!!", decodedText);
    }


}
