package com.reading.is.good.service;

import com.reading.is.good.dto.BookPatchDto;
import com.reading.is.good.exception.NotFoundException;
import com.reading.is.good.model.Book;
import com.reading.is.good.model.Detail;
import com.reading.is.good.model.Stock;
import com.reading.is.good.mongorepos.StockRepository;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;

public class BookServiceTest {

    private static final String BOOK_ID = "anyBookId";
    private static final String BOOK_NAME = "anyBookName";
    private static final String UPDATED_BOOK_NAME = "updatedBookName";
    private static final BigDecimal PRICE = BigDecimal.valueOf(1);
    private static final BigDecimal UPDATED_PRICE = BigDecimal.valueOf(2);
    private static final int QUANTITY = 1;
    private static final int UPDATED_QUANTITY = 2;

    @InjectMocks
    private BookService bookService;

    @Mock
    private StockRepository stockRepository;

    private Book book;
    private Stock stock;
    private BookPatchDto bookPatchDto;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);

        book = Book.builder()
                .bookId(BOOK_ID)
                .name(BOOK_NAME)
                .price(PRICE)
                .build();

        stock = Stock.builder()
                .stockDetails(Detail.builder()
                        .quantity(QUANTITY)
                        .book(book)
                        .build())
                .build();

        bookPatchDto = BookPatchDto.builder()
                .name(UPDATED_BOOK_NAME)
                .quantity(UPDATED_QUANTITY)
                .price(UPDATED_PRICE)
                .build();
    }

    @Test
    void givenBookDto_whenCreateBookThatAlreadyExistInStock_thenReturnCreateBookInStock() {
        // Given
        Mockito.when(stockRepository.findByStockDetailsBookBookId(BOOK_ID)).thenReturn(Optional.of(stock));
        // When
        Book result = bookService.createBook(book);

        // Then
        Assertions.assertThat(result).isNotNull();
        Mockito.verify(stockRepository).save(stock);
        Assertions.assertThat(stock.getStockDetails().getQuantity()).isGreaterThan(QUANTITY);
    }

    @Test
    void givenBookDto_whenCreateBookThatDoesNotExistInStock_thenReturnCreateBookInStock() {
        // Given
        Mockito.when(stockRepository.findByStockDetailsBookBookId(BOOK_ID)).thenReturn(Optional.empty());
        // When
        Book result = bookService.createBook(book);

        // Then
        Assertions.assertThat(result).isNotNull();
        Mockito.verify(stockRepository).save(Mockito.any(Stock.class));
    }

    @Test
    void givenBookPatchDto_whenUpdateBookQuantityPriceName_thenReturnUpdatedBookInStock() {
        // Given
        Mockito.when(stockRepository.findByStockDetailsBookBookId(BOOK_ID)).thenReturn(Optional.of(stock));
        // When
        Detail detail = bookService.updateStockForBook(bookPatchDto, BOOK_ID);
        // Then
        Mockito.verify(stockRepository).save(stock);
        Assertions.assertThat(detail.getQuantity()).isEqualTo(UPDATED_QUANTITY);
        Assertions.assertThat(detail.getBook().getPrice()).isEqualTo(UPDATED_PRICE);
        Assertions.assertThat(detail.getBook().getName()).isEqualTo(UPDATED_BOOK_NAME);
    }

    @Test
    void givenBookPatchDto_whenUpdateBookQuantityPriceNameThatDoesNotExist_thenThrowsNotFoundException() {
        // Given
        Mockito.when(stockRepository.findByStockDetailsBookBookId(BOOK_ID)).thenReturn(Optional.empty());
        // When
        ThrowableAssert.ThrowingCallable throwingCallable = () -> bookService.updateStockForBook(bookPatchDto, BOOK_ID);
        // Then
        Assertions.assertThatThrownBy(throwingCallable).isInstanceOf(NotFoundException.class);
    }
}
