package ru.novus.gaishnik.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CarNumber {
    @Id
    private String number;

    private Instant generatedAt;

    public CarNumber(String number) {
        this.number = number;
        this.generatedAt = Instant.now();
    }
}
