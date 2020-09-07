package com.huchenko.demo.service;

import com.huchenko.demo.domain.Picture;

import java.util.Set;

public interface ImageFetchService {
    Set<Picture> fetchAllPictures();

    void refreshCache();

}
