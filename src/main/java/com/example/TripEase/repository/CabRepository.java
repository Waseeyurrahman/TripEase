package com.example.TripEase.repository;

import com.example.TripEase.model.Cab;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CabRepository extends JpaRepository<Cab, Integer> {

    @Query(value = "SELECT * FROM cab WHERE available = true ORDER BY RAND() LIMIT 1", nativeQuery = true)
    Cab getAvailableCabRandomly();

}
