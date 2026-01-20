package com.example.TripEase.repository;

import com.example.TripEase.model.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Integer> {

    @Query(value = "select *  from driver  where cab_id = :cabId ", nativeQuery = true)
    Driver getDriverByCabId(@Param("cabId") int cabId);
}
