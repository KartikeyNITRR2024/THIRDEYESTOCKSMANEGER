package com.thirdeye.stocksmanager.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import com.thirdeye.stocksmanager.entity.Stocks;

public class ExcelReader {
	
	private static final Logger logger = LoggerFactory.getLogger(ExcelReader.class);
	
	private static final int STOCK_NAME_COL_INDEX = 0; 
    private static final int STOCK_SYMBOL_COL_INDEX = 1;
    private static final int MARKET_NAME_COL_INDEX = 2;

	public static List<Stocks> readStocksFromExcel(MultipartFile file) throws Exception {
		if (file.isEmpty()) {
            logger.error("Uploaded file is empty");
            throw new Exception("File is empty");
        }
		
        List<Stocks> stocksList = new ArrayList<>();
        
        try (XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream())) {
            logger.info("Processing uploaded Excel file");
            XSSFSheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();
            
            if (rows.hasNext()) {
                rows.next(); // Skip header row
            }
            
            int rowNum = 1;
            while (rows.hasNext()) {
                Row row = rows.next();
                rowNum++;

                if (row == null || row.getCell(STOCK_NAME_COL_INDEX) == null) {
                    logger.warn("Row {} is empty or missing required fields", rowNum);
                    continue;
                }

                Cell stockNameCell = row.getCell(STOCK_NAME_COL_INDEX);
                Cell stockSymbolCell = row.getCell(STOCK_SYMBOL_COL_INDEX);
                Cell marketNameCell = row.getCell(MARKET_NAME_COL_INDEX);

                if (stockNameCell == null || stockSymbolCell == null || marketNameCell == null) {
                    logger.error("Row {} is missing mandatory columns", rowNum);
                    throw new Exception(String.format("Row %d is missing stockname, stocksymbol, or marketname", rowNum));
                }
                
                String stockName = getCellValueAsString(stockNameCell);
                String stockSymbol = getCellValueAsString(stockSymbolCell);
                String marketName = getCellValueAsString(marketNameCell);
                
                Stocks newStock = new Stocks();
                newStock.setStockName(stockName);
                newStock.setStockSymbol(stockSymbol);
                newStock.setMarketName(marketName);
                stocksList.add(newStock);
                
                logger.info("Read stock: {} with symbol: {} and market: {}", stockName, stockSymbol, marketName);
            }
        } catch (IOException e) {
            logger.error("Error processing the file", e);
            throw new IOException("Error processing the file", e);
        }

        return stocksList;
    }

    public static String getCellValueAsString(Cell cell) {
        if (cell == null) {
            logger.warn("Cell is null, returning empty string.");
            return "";
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf(cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                logger.warn("Unknown cell type encountered, returning empty string.");
                return "";
        }
    }
}
