package com.thirdeye.stocksmanager.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.thirdeye.stocksmanager.entity.ConfigTable;

@Repository
public interface ConfigTableRepo extends JpaRepository<ConfigTable, Long> {
}
