package com.thirdeye.stocksmanager.controllers;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.thirdeye.stocksmanager.annotation.AdminRequired;
import com.thirdeye.stocksmanager.services.impl.StocksListServiceImpl;
import com.thirdeye.stocksmanager.utils.AllMicroservicesData;

@RestController
@RequestMapping("/api/stockslists")
public class StocksListController {
    
    @Autowired
	AllMicroservicesData allMicroservicesData;
    
    @Autowired
    private StocksListServiceImpl stocksListServiceImpl;

    private static final Logger logger = LoggerFactory.getLogger(StocksListController.class);

    @PostMapping("/{uniqueId}/updatestocklist")
    @AdminRequired
    public ResponseEntity<String> updateStockList(@PathVariable("uniqueId") Integer pathUniqueId, @RequestParam("file") MultipartFile file) {
        logger.info("Received request to update stock list with uniqueId: {}", pathUniqueId);
        if (!pathUniqueId.equals(allMicroservicesData.current.getMicroserviceUniqueId())) {
            logger.warn("Unique ID mismatch: expected {}, but got {}", allMicroservicesData.current.getMicroserviceUniqueId(), pathUniqueId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid uniqueId");
        }
        try {
            stocksListServiceImpl.updateStockList(file);
            logger.info("Stock list updated successfully for uniqueId: {}", allMicroservicesData.current.getMicroserviceUniqueId());
            return new ResponseEntity<>("", HttpStatus.CREATED);
            
        } catch (IOException e) {
            logger.error("IO Exception while processing the file for uniqueId: {}", allMicroservicesData.current.getMicroserviceUniqueId(), e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("An unexpected error occurred for uniqueId: {}", allMicroservicesData.current.getMicroserviceUniqueId(), e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
