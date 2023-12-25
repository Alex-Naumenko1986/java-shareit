package ru.practicum.shareit.pageable;


import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.pageable.exception.InvalidPageableArgumentException;

public class CustomPageable implements Pageable {

    private Integer from;
    private Integer size;
    private Sort sort;

    public CustomPageable(Integer from, Integer size, Sort sort) {
        if (from < 0) throw new InvalidPageableArgumentException("Parameter from shouldn't be less then zero");
        if (size < 1) throw new InvalidPageableArgumentException("Parameter size should be greater then zero");
        this.from = from;
        this.size = size;
        this.sort = sort;
    }

    @Override
    public int getPageNumber() {
        return 0;
    }

    @Override
    public int getPageSize() {
        return size;
    }

    @Override
    public long getOffset() {
        return from;
    }

    @Override
    public Sort getSort() {
        return sort;
    }

    @Override
    public Pageable next() {
        return this;
    }

    @Override
    public Pageable previousOrFirst() {
        return this;
    }

    @Override
    public Pageable first() {
        return this;
    }

    @Override
    public Pageable withPage(int pageNumber) {
        return this;
    }

    @Override
    public boolean hasPrevious() {
        return false;
    }
}
