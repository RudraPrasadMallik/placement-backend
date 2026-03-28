package com.example.demo.repository;

import com.example.demo.model.PlacementStatistic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlacementStatisticRepository extends JpaRepository<PlacementStatistic, Long> {
}
