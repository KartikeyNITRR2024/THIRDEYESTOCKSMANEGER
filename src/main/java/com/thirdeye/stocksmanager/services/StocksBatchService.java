package com.thirdeye.stocksmanager.services;

import java.util.List;
import java.util.Map;

public interface StocksBatchService {
	Map<Long, String> getStocksForBatch(Integer machineNo);
	Map<Long, String> getStocksForBatchFromIds(List<Long> stockIds);
	
}
