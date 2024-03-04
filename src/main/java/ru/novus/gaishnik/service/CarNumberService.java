package ru.novus.gaishnik.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.novus.gaishnik.entity.CarNumber;
import ru.novus.gaishnik.repository.CarNumberRepository;

import java.util.*;

@Service
@RequiredArgsConstructor
public class CarNumberService {

    private final CarNumberRepository carNumberRepository;
    private final Set<String> seenNumbers = new HashSet<>();
    // Russian letters used in car numbers
    private static final char[] letters = "АВЕКМНОРСТУХ".toCharArray();
    private static final int MAX_CAR_NUMBERS = 1000 * letters.length * letters.length * letters.length;
    private Map<Character, Character> nextLetters;
    private final Random random = new Random();

    /**
     * Returns the next number making sure it was not seen before and saves it to DB
     * */
    public synchronized String getUnseenNextNumber() {
        if (MAX_CAR_NUMBERS <= seenNumbers.size()) {
            resetHistory();
        }
        final Optional<CarNumber> lastNumber = carNumberRepository.findTopByOrderByGeneratedAtDesc();
        String number;
        if (lastNumber.isEmpty()) {
            number = getRandomNumber();
            carNumberRepository.save(new CarNumber(number));
            return number;
        }

        String nextNumber = getNextNumber(lastNumber.get().getNumber());
        while (seenNumbers.contains(nextNumber)) {
            nextNumber = getNextNumber(nextNumber);
        }
        carNumberRepository.save(new CarNumber(nextNumber));
        seenNumbers.add(nextNumber);
        return nextNumber;
    }


    /**
     * Returns a random car number making sure it was not seen before and saves it to DB
     * */
    public synchronized String getUnseenRandomNumber() {
        if (MAX_CAR_NUMBERS <= seenNumbers.size()) {
            resetHistory();
        }
        String number = getRandomNumber();
        while (seenNumbers.contains(number)) {
            number = getRandomNumber();
        }
        carNumberRepository.save(new CarNumber(number));
        seenNumbers.add(number);
        return number;
    }

    /**
     * Clears the database car numbers and local cache
     * */
    private void resetHistory() {
        carNumberRepository.deleteAll();
        seenNumbers.clear();
    }

    /**
     * @param number in format 'A123BC'
     * @return next number in format 'A123BC'
     */
    public String getNextNumber(String number) {
        int num = Integer.parseInt(number.substring(1, 4));
        StringBuilder symbols = new StringBuilder(number.charAt(0) + number.substring(4));

        if (num == 999) {
            num = 0;

            // Change letters in the car number
            for (int i = symbols.length() - 1; i >= 0; --i) {
                boolean needContinue = symbols.charAt(i) == letters[letters.length - 1];
                symbols.setCharAt(i, nextLetters.get(symbols.charAt(i)));
                if (!needContinue) break;
            }
        } else ++num;

        return symbols.charAt(0) + String.format("%03d", num) + symbols.substring(1);
    }

    public String getRandomNumber() {
        return letters[random.nextInt(letters.length)] +
            String.format("%03d", random.nextInt(1000)) +
            letters[random.nextInt(letters.length)] +
            letters[random.nextInt(letters.length)];
    }

    @PostConstruct
    private void init() {
        this.nextLetters = new HashMap<>();
        for (int i = 0; i < letters.length - 1; ++i) {
            nextLetters.put(letters[i], letters[i + 1]);
        }
        nextLetters.put(letters[letters.length - 1], letters[0]);

        carNumberRepository.findAll().forEach(carNumber -> seenNumbers.add(carNumber.getNumber()));
    }
}
