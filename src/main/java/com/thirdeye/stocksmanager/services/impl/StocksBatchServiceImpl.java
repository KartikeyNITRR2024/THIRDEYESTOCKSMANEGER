package com.thirdeye.stocksmanager.services.impl;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.thirdeye.stocksmanager.services.StocksBatchService;
import com.thirdeye.stocksmanager.utils.PropertyLoader;

@Service
public class StocksBatchServiceImpl implements StocksBatchService {
    
    @Autowired
    StocksListServiceImpl stocksListServiceImpl;
    
    @Autowired
    PropertyLoader propertyLoader;
    
    @Value("${startingIdOfStocks}")
    private Integer startingIdOfStocks;
    
    private static final Logger logger = LoggerFactory.getLogger(StocksBatchServiceImpl.class);
    
    @Override
    public Map<Long, String> getStocksForBatch(Integer machineNo) {
        try {
            Integer stocksSize = stocksListServiceImpl.getStocksSize();
            Integer totalMachines = propertyLoader.noOfMachine;

            if (totalMachines <= 0) {
                logger.error("Total machines must be greater than zero.");
                throw new IllegalArgumentException("Total machines must be greater than zero.");
            }

            Integer noOfStocksInBatch = (stocksSize / totalMachines) + ((stocksSize % totalMachines == 0) ? 0 : 1);
            Integer startingPos = startingIdOfStocks+(noOfStocksInBatch * (machineNo - 1)) + 1;
            Integer endPos = startingIdOfStocks+Math.min(stocksSize, noOfStocksInBatch * machineNo);

            logger.info("Fetching stocks for batch: Machine No: {}, Starting Pos: {}, End Pos: {}", machineNo, startingPos, endPos);
            return stocksListServiceImpl.getIdToStockInBatch((long) startingPos, (long) endPos);
        } catch (Exception e) {
            logger.error("Error occurred while fetching stocks for batch: Machine No: {}", machineNo, e);
            throw new RuntimeException("Failed to fetch stocks for batch.", e);
        }
    }
    
    public Map<Long, String> getAllStocksForBatch() {
        try {
            logger.info("Fetching all stocks");
            return stocksListServiceImpl.getAllStocksData();
        } catch (Exception e) {
            logger.error("Error occurred while fetching all stock.", e);
            throw new RuntimeException("Error occurred while fetching all stock.", e);
        }
    }

    @Override
	public Map<Long, String> getStocksForBatchFromIds(List<Long> stockIds) {
    	try {
    	    return stocksListServiceImpl.getStockInfoById(stockIds);
    	} catch (Exception e) {
            logger.error("Error occurred while fetching stocks by ids", e);
            throw new RuntimeException("Failed to fetch stocks by ids.", e);
        }
	}
}
