package com.hospital.repository;

import java.util.List;

public interface Searchable<T> {
    List<T> search(String keyword);
}
