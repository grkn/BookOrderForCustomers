package com.reading.is.good.service;

import com.reading.is.good.dto.BookPatchDto;
import com.reading.is.good.exception.NotFoundException;
import com.reading.is.good.model.Book;
import com.reading.is.good.model.Detail;
import com.reading.is.good.model.Stock;
import com.reading.is.good.mongorepos.StockRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookService {

    private final static Logger LOGGER = LoggerFactory.getLogger(BookService.class);

    private final StockRepository stockRepository;

    /**
     * Creates book by given book dto
     * @param book
     * @return created book
     */
    public Book createBook(Book book) {
        Optional<Stock> stockOptional = stockRepository.findByStockDetailsBookBookId(book.getBookId());

        stockOptional.ifPresentOrElse(stock -> {
            LOGGER.trace("Book exists in stock so update operation will begin with book name is {}", book.getName());
            updateQuantityOfExistingBook(stock);
            LOGGER.trace("Increment of book quantity finished successfully with {}", stock);
        }, () -> {
            LOGGER.trace("Book does NOT exist in stock so persist operation starts");
            createStockWithBook(book);
            LOGGER.trace("Persist of book quantity finished successfully");
        });

        return book;
    }

    /**
     * Patching quantity, price and name fields of book in stock
     * @param bookPatchDto
     * @param bookId
     * @return
     */
    public Detail updateStockForBook(BookPatchDto bookPatchDto, String bookId) {
        Optional<Stock> stockOptional = stockRepository.findByStockDetailsBookBookId(bookId);
        if (stockOptional.isPresent()) {
            Detail oldDetail = stockOptional.get().getStockDetails();

            if (null != oldDetail) {
                oldDetail.setQuantity(bookPatchDto.getQuantity() != null ? bookPatchDto.getQuantity() : oldDetail.getQuantity());
                Book newBook = Book.builder()
                        .bookId(bookId)
                        .price(bookPatchDto.getPrice() != null ? bookPatchDto.getPrice() : oldDetail.getBook().getPrice())
                        .name(bookPatchDto.getName() != null ? bookPatchDto.getName() : oldDetail.getBook().getName())
                        .build();
                oldDetail.setBook(newBook);
            }

            stockRepository.save(stockOptional.get());
            return oldDetail;
        } else {
            throw new NotFoundException("Book can not found in the stock. Please create it first to update");
        }
    }

    /**
     * New Stock is created with given book and quantity is one
     * @param book
     */
    private void createStockWithBook(Book book) {
        Stock newStock = Stock.builder()
                .stockDetails(Detail.builder()
                        .book(Book.builder()
                                .bookId(book.getBookId())
                                .name(book.getName())
                                .price(book.getPrice()).build())
                        .quantity(1)
                        .build())
                .build();

        stockRepository.save(newStock);
    }

    /**
     * Create book with increasing quantity one in stock
     * @param stock
     */
    private void updateQuantityOfExistingBook(Stock stock) {
        Detail detail = stock.getStockDetails();
        detail.setQuantity(detail.getQuantity() + 1);
        stockRepository.save(stock);
    }
}
