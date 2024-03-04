package ru.novus.gaishnik.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.novus.gaishnik.entity.CarNumber;

import java.util.Optional;

@Repository
public interface CarNumberRepository extends JpaRepository<CarNumber, String> {
    Optional<CarNumber> findTopByOrderByGeneratedAtDesc();
}
