package com.thirdeye.stocksmanager.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.thirdeye.stocksmanager.entity.ConfigUsed;

@Repository
public interface ConfigUsedRepo extends JpaRepository<ConfigUsed, Long> {
}
