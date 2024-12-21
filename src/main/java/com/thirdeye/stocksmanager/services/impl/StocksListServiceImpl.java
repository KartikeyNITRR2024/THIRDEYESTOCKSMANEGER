package com.thirdeye.stocksmanager.services.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.thirdeye.stocksmanager.entity.Stocks;
import com.thirdeye.stocksmanager.externelcontrollers.Thirdeye_Guider_Connection;
import com.thirdeye.stocksmanager.repositories.StocksRepo;
import com.thirdeye.stocksmanager.services.StocksListService;
import com.thirdeye.stocksmanager.utils.ExcelReader;

import org.springframework.transaction.annotation.Transactional;

@Service
public class StocksListServiceImpl implements StocksListService {
	private Map<Long, String> idToStock = new HashMap<>();

    private static final Logger logger = LoggerFactory.getLogger(StocksListServiceImpl.class);
    
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    @Autowired
    private StocksRepo stocksRepo;
    
    @Autowired
    private Thirdeye_Guider_Connection thirdeye_Guider_Connection;
    
    @Value("${batchSizeToGetSetFromDatabase}")
    private Long batchSizeToGetSetFromDatabase;

    public void getStockListInBatches() throws Exception {
    	Map<Long, String> idToStock1 = new HashMap<>();
        logger.info("Starting to fetch stock list in batches.");
        try {
            long totalStocks = stocksRepo.count();
            int pageSize = batchSizeToGetSetFromDatabase.intValue();
            int totalPages = (int) Math.ceil((double) totalStocks / pageSize);
            for (int page = 0; page < totalPages; page++) {
                Pageable pageable = PageRequest.of(page, pageSize);
                Page<Stocks> stockListBatch = stocksRepo.findAll(pageable);
                for (Stocks stock : stockListBatch.getContent()) { 
                	idToStock1.put(stock.getId(), stock.getStockSymbol() + " " + stock.getMarketName());
                }
            }
            lock.writeLock().lock();
            idToStock.clear();
            idToStock = new HashMap<>(idToStock1);
            logger.info("Successfully fetched all {} stocks in batches.", totalStocks);
        } catch (Exception e) {
            logger.error("Error occurred while fetching stock list in batches: {}", e.getMessage(), e);
            throw new Exception("Failed to retrieve stock list in batches", e);
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    @Override
    public Map<Long, String> getIdToStockInBatch(Long start, Long end) {
    	lock.readLock().lock();
        Map<Long, String> getIdToStockBatch = new HashMap<>();
        try {
            for (Long i = start; i <= end; i++) {
                if (idToStock.containsKey(i)) {
                    getIdToStockBatch.put(i, idToStock.get(i));
                }
            }
        } finally {
            lock.readLock().unlock();
        }
        return getIdToStockBatch;
    }
    
    @Override
    public Integer getStocksSize() {
        lock.readLock().lock();
        try {
            return idToStock.size();
        } finally {
            lock.readLock().unlock();
        }
    }
    
    private void saveStocksInBatch(List<Stocks> stocksToSave)
    {
    	 stocksRepo.saveAll(stocksToSave);
         logger.info("Successfully saved {} new stocks.", stocksToSave.size());
    }

    @Override
	public Map<Long, String> getStockInfoById(List<Long> stockIds) {
		Map<Long, String> stocksInfo = new HashMap<>();
		for(Long id : stockIds)
		{
			stocksInfo.put(id, idToStock.get(id));
		}
		return stocksInfo;
	}

	@Override
	public Map<Long, String> getAllStocksData() {
		lock.readLock().lock();
        try {
            return idToStock;
        } finally {
            lock.readLock().unlock();
        }
	}
	
	@Override
    @Transactional(rollbackFor = {Exception.class, IOException.class})
    public void updateStockList(MultipartFile file) throws Exception {
        List<Stocks> stocksList = new ArrayList<>();
        List<Stocks> stocksToSave = new ArrayList<>();

        try {
            stocksList = ExcelReader.readStocksFromExcel(file);
        } catch (IOException e) {
            logger.error("Failed to read stocks from Excel file", e);
            throw new Exception("Failed to read stocks from Excel file", e);
        }

        lock.writeLock().lock();
        Long count = 0L;
        try {
            for (Stocks stock : stocksList) {
                Optional<Stocks> existingStock = stocksRepo.findByStockSymbolAndMarketName(stock.getStockSymbol(), stock.getMarketName());
                if (existingStock.isPresent()) {
                    logger.info("Stock with No: {} symbol: {} and market: {} already exists, skipping.", count, stock.getStockSymbol(), stock.getMarketName());
                } else {
                	count++;
                    stocksToSave.add(stock);
                    logger.info("Adding No: {}  stock: {} with symbol: {} and market: {}", count, stock.getStockName(), stock.getStockSymbol(), stock.getMarketName());
                    if(stocksToSave.size() >= batchSizeToGetSetFromDatabase)
                    {
                    	logger.info("Going to call function saveStocksInBatch for batch size ", stocksToSave.size());
                    	saveStocksInBatch(stocksToSave);
                    	stocksToSave.clear();
                    }
                }
            }
            if (!stocksToSave.isEmpty()) {
            	saveStocksInBatch(stocksToSave);
            	stocksToSave.clear();
                logger.info("Successfully saved {} new stocks.", count);
            } else {
                logger.info("No new stocks to save.");
            }
        } finally {
            lock.writeLock().unlock();
        }
        thirdeye_Guider_Connection.restartAllMicroservices();
    }
}
