package com.thirdeye.stocksmanager.utils;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.thirdeye.stocksmanager.repositories.ConfigUsedRepo;
import com.thirdeye.stocksmanager.entity.ConfigUsed;
import com.thirdeye.stocksmanager.entity.ConfigTable;
import com.thirdeye.stocksmanager.repositories.ConfigTableRepo;
import com.thirdeye.stocksmanager.services.impl.StocksListServiceImpl;

import jakarta.annotation.PostConstruct;

@Component 
public class PropertyLoader {
    public Integer noOfMachine;
    private Long configId;

    private static final Logger logger = LoggerFactory.getLogger(PropertyLoader.class);

    @Autowired
    private ConfigTableRepo configTableRepo;
    
    @Autowired
    private ConfigUsedRepo configUsedRepo;

    public void updatePropertyLoader() {
        try {
        	logger.info("Fetching currently config used.");
            ConfigUsed configUsed = configUsedRepo.findById(1L).get();
            configId = configUsed.getId();
            logger.debug("Fetching configuration for configId: {}", configId);
            Optional<ConfigTable> configTable = configTableRepo.findById(configId);
            if (configTable.isPresent()) {
                noOfMachine = configTable.get().getNoOfMachineForLiveMarket();
                logger.info("Number of machines loaded: {}", noOfMachine);
            } else {
                logger.warn("No configuration found for configId: {}", configId);
            }
        } catch (Exception e) {
            logger.error("An error occurred while fetching configuration: {}", e.getMessage(), e);
        }
    }
}
