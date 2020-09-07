package com.huchenko.demo.service;

import com.huchenko.demo.domain.Picture;

import java.util.Optional;
import java.util.Set;

public interface ImageSearchService {
    Optional<Set<Picture>> searchByTerm(String term);
}
