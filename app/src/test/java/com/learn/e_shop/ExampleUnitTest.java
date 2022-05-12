package com.learn.e_shop;

import org.junit.Test;

import static org.junit.Assert.*;

import com.learn.e_shop.Model.Cart;
import com.learn.e_shop.Threads.DateThread;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void date_isCorrect() throws  Exception {
        Cart cart_time_test = new Cart();
        String date = cart_time_test.getCurrentDate();
        assertEquals("not equals", "May 08, 2022", date);
    }

    @Test
    public void date_is_correct() throws Exception {
        DateThread dateThread = new DateThread();
        dateThread.start();
        String date = dateThread.getDate();
        assertEquals("The date is not correct", "May 08, 2022", date);
    }
}