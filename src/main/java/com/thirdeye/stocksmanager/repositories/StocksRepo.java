package com.thirdeye.stocksmanager.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import com.thirdeye.stocksmanager.entity.Stocks;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface StocksRepo extends JpaRepository<Stocks, Long> {
    Optional<Stocks> findByStockSymbolAndMarketName(String stockSymbol, String marketName);
    
    Page<Stocks> findAll(Pageable pageable);
}
