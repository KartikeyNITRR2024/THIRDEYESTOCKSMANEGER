package com.thirdeye.stocksmanager.controllers;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thirdeye.stocksmanager.services.impl.StocksBatchServiceImpl;
import com.thirdeye.stocksmanager.utils.AllMicroservicesData;

@RestController
@RequestMapping("/api/stocksbatch")
public class StocksBatchController {
	
    
    @Autowired
	AllMicroservicesData allMicroservicesData;
    
    @Autowired
    private StocksBatchServiceImpl stocksBatchServiceImpl;

    private static final Logger logger = LoggerFactory.getLogger(StocksListController.class);

    @GetMapping("/{uniqueId}/stocksbatch/{machineno}")
    public ResponseEntity<Map<Long, String>> getStockBatch(@PathVariable("uniqueId") Integer pathUniqueId, @PathVariable("machineno") Integer machineNo) {
        logger.info("Received request to get stock Batch with uniqueId: {}", pathUniqueId);
        if (!pathUniqueId.equals(allMicroservicesData.current.getMicroserviceUniqueId())) {
            logger.warn("Unique ID mismatch: expected {}, but got {}", allMicroservicesData.current.getMicroserviceUniqueId(), pathUniqueId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        try {
        	Map<Long, String> batchData = stocksBatchServiceImpl.getStocksForBatch(machineNo);
            logger.info("Stock list updated successfully for uniqueId: {}", allMicroservicesData.current.getMicroserviceUniqueId());
            return new ResponseEntity<>(batchData, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("An unexpected error occurred for uniqueId: {}", allMicroservicesData.current.getMicroserviceUniqueId(), e);
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }
    
    @GetMapping("/{uniqueId}/allstocks")
    public ResponseEntity<Map<Long, String>> getAllStock(@PathVariable("uniqueId") Integer pathUniqueId) {
        logger.info("Received request to get stock Batch with uniqueId: {}", pathUniqueId);
        if (!pathUniqueId.equals(allMicroservicesData.current.getMicroserviceUniqueId())) {
            logger.warn("Unique ID mismatch: expected {}, but got {}", allMicroservicesData.current.getMicroserviceUniqueId(), pathUniqueId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        try {
        	Map<Long, String> allStockData = stocksBatchServiceImpl.getAllStocksForBatch();
            logger.info("Stock list updated successfully for uniqueId: {}", allMicroservicesData.current.getMicroserviceUniqueId());
            return new ResponseEntity<>(allStockData, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("An unexpected error occurred for uniqueId: {}", allMicroservicesData.current.getMicroserviceUniqueId(), e);
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }
    
    @PostMapping("/{uniqueId}/stocksbatchfromids")
    public ResponseEntity<Map<Long, String>> getStockBatchFromIds(@PathVariable("uniqueId") Integer pathUniqueId, @RequestBody List<Long> stockIds) {
        logger.info("Received request to get stock Batch with uniqueId: {}", pathUniqueId);
        if (!pathUniqueId.equals(allMicroservicesData.current.getMicroserviceUniqueId())) {
            logger.warn("Unique ID mismatch: expected {}, but got {}", allMicroservicesData.current.getMicroserviceUniqueId(), pathUniqueId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        try {
        	Map<Long, String> batchData = stocksBatchServiceImpl.getStocksForBatchFromIds(stockIds);
            logger.info("Stock list updated successfully for uniqueId: {}", allMicroservicesData.current.getMicroserviceUniqueId());
            return new ResponseEntity<>(batchData, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("An unexpected error occurred for uniqueId: {}", allMicroservicesData.current.getMicroserviceUniqueId(), e);
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }
}

