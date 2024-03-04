package ru.novus.gaishnik.service;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

@RunWith(SpringRunner.class)
@SpringBootTest
class CarNumberServiceTest {
    @Autowired
    private CarNumberService service;

    @Test
    void getNextNumber() {
        assertEquals("А001АА", service.getNextNumber("А000АА"));
        assertEquals("А000ВТ", service.getNextNumber("А999ВС"));
        assertEquals("К000АА", service.getNextNumber("Е999ХХ"));
    }

    @Test
    void testConcurrentNext() {
        Set<String> numbers1 = new HashSet<>();
        Runnable task1 = () -> {
            for (int i = 0; i < 100; i++) {
                final String nextNumber = service.getUnseenNextNumber();
                numbers1.add(nextNumber);
            }
        };
        Thread thread1 = new Thread(task1);

        Set<String> numbers2 = new HashSet<>();
        Runnable task2 = () -> {
            for (int i = 0; i < 100; i++) {
                final String nextNumber = service.getUnseenNextNumber();
                numbers2.add(nextNumber);
            }
        };
        Thread thread2 = new Thread(task2);

        thread1.start();
        thread2.start();

        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(numbers1.size() + " " + numbers2.size());
        numbers1.retainAll(numbers2);
        assertEquals(0, numbers1.size());
    }

    @Test
    void getManyRandomNumbers(){
        Set<String> numbers = new HashSet<>();
        for (int i = 0; i < 1000 * 12 * 12; i++) {
            numbers.add(service.getUnseenRandomNumber());
        }
        assertEquals(1000 * 12 * 12, numbers.size());
    }

    @Test
    void getOverflowedNumbers(){
        Set<String> numbers = new HashSet<>();
        Set<String> doubled = new HashSet<>();
        for (int i = 0; i < 1000 * 12 * 12 * 12 + 1000; i++) {
            final String number = service.getUnseenRandomNumber();
            if (doubled.contains(number))
                fail("Doubled number: " + number);
            if (!numbers.add(number))
                doubled.add(number);
        }
        assertEquals(1000, doubled.size());
    }
}