package ru.novus.gaishnik.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.novus.gaishnik.service.CarNumberService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/number")
public class CarNumberController {
    private final CarNumberService carNumberService;

    @Value("${car.number.suffix:116 RUS}")
    private String numberSuffix;

    @GetMapping("/next")
    public String getNextNumber() {
        return carNumberService.getUnseenNextNumber() + " " + numberSuffix;
    }

    @GetMapping("/random")
    public String getRandomNumber() {
        return carNumberService.getUnseenRandomNumber() + " " + numberSuffix;
    }

}
