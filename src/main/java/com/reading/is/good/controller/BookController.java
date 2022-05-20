package com.reading.is.good.controller;

import com.reading.is.good.constant.ChallengeConstant;
import com.reading.is.good.dto.BookPatchDto;
import com.reading.is.good.model.Book;
import com.reading.is.good.model.Detail;
import com.reading.is.good.model.Order;
import com.reading.is.good.model.Stock;
import com.reading.is.good.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping(ChallengeConstant.BASE_URL)
public class BookController {

    private final BookService bookService;

    @PostMapping(value = "/book")
    @PreAuthorize(value = "hasRole('ROLE_USER')")
    public ResponseEntity<Book> createBook(@Valid @RequestBody Book book) {
        return new ResponseEntity<>(bookService.createBook(book), HttpStatus.CREATED);
    }

    @PatchMapping(value = "/book/{bookId}")
    @PreAuthorize(value = "hasRole('ROLE_USER')")
    public ResponseEntity<Detail> updateStockWithBookAndQuantity(@Valid @RequestBody BookPatchDto bookPatchDto, @PathVariable String bookId) {
        return new ResponseEntity<>(bookService.updateStockForBook(bookPatchDto, bookId), HttpStatus.OK);
    }
}
