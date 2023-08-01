package com.galaxyrun.util;

import org.junit.Assert;
import org.junit.Test;

// A few simple tests to ensure the basic functionality works. Non-exhaustive.
public class CircularBufferTest {

    private final int kDefaultCapacity = 10;

    @Test
    public void testSize() {
        CircularBuffer<Integer> buff = new CircularBuffer<>(kDefaultCapacity);
        Assert.assertEquals(0, buff.getSize());
        // Push until full.
        for (int i = 0; i < kDefaultCapacity; i++) {
            buff.push(i);
            Assert.assertEquals(i + 1, buff.getSize());
        }
        // Push some more. Size shouldn't change.
        for (int i = 0; i < kDefaultCapacity; i++) {
            buff.push(i);
            Assert.assertEquals(kDefaultCapacity, buff.getSize());
        }
        // Pop until empty.
        for (int i = 1; i <= kDefaultCapacity; i++) {
            buff.popFront();
            Assert.assertEquals(kDefaultCapacity - i, buff.getSize());
        }
    }

    @Test
    public void testIsEmpty() {
        CircularBuffer<Integer> buff = new CircularBuffer<>(kDefaultCapacity);
        Assert.assertTrue(buff.isEmpty());
        buff.push(0);
        Assert.assertFalse(buff.isEmpty());
    }

    @Test
    public void testIsFull() {
        CircularBuffer<Integer> buff = new CircularBuffer<>(kDefaultCapacity);
        for (int i = 0; i < kDefaultCapacity; i++) {
            Assert.assertFalse(buff.isFull());
            buff.push(i);
        }
        Assert.assertTrue(buff.isFull());
    }

    @Test
    public void testPush() {
        CircularBuffer<Integer> buff = new CircularBuffer<>(kDefaultCapacity);
        // Push until full. Front element will remain zero.
        for (int i = 0; i < kDefaultCapacity; i++) {
            buff.push(i);
            Assert.assertEquals(0, (int) buff.peekFront());
        }
        // Push over capacity. This time, we expect the front element to be
        // the element pushed `kDefaultCapacity` times ago.
        for (int i = 0; i < kDefaultCapacity; i++) {
            buff.push(i + kDefaultCapacity);
            Assert.assertEquals(i + 1, (int) buff.peekFront());
        }
    }

    @Test
    public void testPop() {
        CircularBuffer<Integer> buff = new CircularBuffer<>(kDefaultCapacity);
        for (int i = 0; i < kDefaultCapacity; i++) {
            buff.push(i);
        }
        for (int i = 0; i < kDefaultCapacity; i++) {
            Assert.assertEquals(i, (int) buff.popFront());
        }
    }

    @Test
    public void testPeek() {
        CircularBuffer<Integer> buff = new CircularBuffer<>(kDefaultCapacity);
        for (int i = 0; i < kDefaultCapacity; i++) {
            buff.push(i);
            Assert.assertEquals(0, (int) buff.peekFront());
        }
        // Push more elements. peekFront() should basically iterate through the buffer.
        for (int i = 0; i < kDefaultCapacity; i++) {
            buff.push(kDefaultCapacity + i);
            Assert.assertEquals(i + 1, (int) buff.peekFront());
        }
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testPopEmptyException() {
        CircularBuffer<Integer> buff = new CircularBuffer<>(kDefaultCapacity);
        buff.popFront();
    }

    @Test
    public void testIterator() {
        CircularBuffer<Integer> buff = new CircularBuffer<>(kDefaultCapacity);
        for (int i = 0; i < kDefaultCapacity; i++) {
            buff.push(i);
        }
        int i = 0;
        for (Integer elem : buff) {
            Assert.assertEquals(i, (int) elem);
            ++i;
        }
    }
}