package com.thirdeye.stocksmanager.services;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

public interface StocksListService {
   void updateStockList(MultipartFile file) throws Exception;
   Map<Long, String> getStockInfoById(List<Long> stockIds);
   Map<Long, String> getIdToStockInBatch(Long start, Long end);
   Map<Long, String> getAllStocksData();
   Integer getStocksSize();
}
