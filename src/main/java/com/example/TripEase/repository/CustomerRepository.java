package com.example.TripEase.repository;

import com.example.TripEase.Enum.Gender;
import com.example.TripEase.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer,Long> {

    List<Customer> findByGender(Gender gender);

    List<Customer> findByGenderAndAge(Gender gender, int age);

    @Query("select c from Customer c where c.gender = :gender and c.age > :age")// HQL - hibarnate Query language
    List<Customer> getAllByAgeAndGenderGreaterThan(@Param("gender") Gender gender ,@Param("age") int age);
}
