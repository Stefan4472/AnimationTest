package com.galaxyrun.engine.background;

import android.graphics.Color;

import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

// TODO: turns out this doesn't work because we use android.Color. We would need to use
//   a mocking framework like Mockito or Robolectric. See https://stackoverflow.com/a/49548427
public class ColorGeneratorTest {
    @Test
    public void testSolidColor() {
        int[] res = ColorGenerator.makeSolidColor(Color.BLACK, 10);
        assertEquals(10, res.length);
        for (int color : res) {
            assertEquals(Color.BLACK, color);
        }
    }

    @Test
    public void testTransitionLengthTwo() {
        int[] res = ColorGenerator.makeTransition(Color.WHITE, Color.BLACK, 2);
        assertEquals(2, res.length);
        assertEquals(Color.WHITE, res[0]);
        assertEquals(Color.BLACK, res[1]);
    }

    @Test
    public void testTransition() {
        // TODO: a test that checks the transitions when end-start is not evenly divisible by n.
        int startColor = Color.argb(0, 0, 0, 0);
        int endColor = Color.argb(100, 100, 100, 100);
        int[] res = ColorGenerator.makeTransition(startColor, endColor, 25);
        assertEquals(25, res.length);
        for (int i = 0; i < 25; i++) {
            int expectedColor = Color.argb(i*4, i*4, i*4, i*4);
            assertEquals(expectedColor, res[i]);
        }
    }
}