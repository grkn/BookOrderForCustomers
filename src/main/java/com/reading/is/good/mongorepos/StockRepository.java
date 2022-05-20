package com.reading.is.good.mongorepos;

import com.reading.is.good.model.Stock;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface StockRepository extends MongoRepository<Stock, String> {
    Optional<Stock> findByStockDetailsBookBookId(String bookId);
}
