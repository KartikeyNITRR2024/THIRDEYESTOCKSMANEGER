package com.thirdeye.stocksmanager.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.thirdeye.stocksmanager.entity.MicroservicesInfo;

@Repository
public interface MicroservicesInfoRepo extends JpaRepository<MicroservicesInfo, Long> {
	MicroservicesInfo getByMicroserviceName(String microserviceName);
}
