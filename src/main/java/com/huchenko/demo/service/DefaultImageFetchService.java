package com.huchenko.demo.service;


import com.huchenko.demo.domain.Picture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DefaultImageFetchService implements ImageFetchService {
    private final Logger log = LoggerFactory.getLogger(DefaultImageFetchService.class);

    private final RestTemplate restTemplate;
    private final AuthorizationService authorizationService;
    private Map<String, Set<Picture>> pictureCache;

    @Value("${application.picturesEndpoint.url}")
    private String picturesEndpoint;

    public DefaultImageFetchService(RestTemplate restTemplate, AuthorizationService authorizationService, Map<String, Set<Picture>> pictureCache) {
        this.restTemplate = restTemplate;
        this.authorizationService = authorizationService;
        this.pictureCache = pictureCache;
    }

    @Override
    public Set<Picture> fetchAllPictures() {
        HttpHeaders headers = new HttpHeaders();
        String token = authorizationService.getToken();
        headers.add("Authorization", "Bearer " + token);
        HttpEntity request = new HttpEntity(headers);

        String endpoint = picturesEndpoint + "images?page=";
        Set<Picture> pictures = new HashSet<>();
        boolean hasMorePictures;
        int page = 1;
        do {
            hasMorePictures = false;
            ResponseEntity<ResourceImageBunch> response = restTemplate.exchange(endpoint + page++, HttpMethod.GET, request, ResourceImageBunch.class);
            if (response.hasBody()) {
                Optional<ResourceImageBunch> resourceImageBunch = Optional.ofNullable(response.getBody());
                if (resourceImageBunch.isPresent()) {
                    hasMorePictures = resourceImageBunch.get().hasMore;
                    pictures.addAll(resourceImageBunch.get().pictures.parallelStream()
                        .map(picture -> fetchPicture(picture.getId(), token)).collect(Collectors.toSet()));
                }
            }
        } while (hasMorePictures);
        return pictures;

    }

    private Picture fetchPicture(String id, String token) {
        log.info("Fetch image with id={}", id);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);
        HttpEntity request = new HttpEntity(headers);

        String endpoint = picturesEndpoint + "images/" + id;
        ResponseEntity<Picture> response = restTemplate.exchange(endpoint, HttpMethod.GET, request, Picture.class);
        return response.getBody();
    }

    private static class ResourceImageBunch {
        private Set<Picture> pictures = new HashSet<>();
        private boolean hasMore;

        public Set<Picture> getPictures() {
            return pictures;
        }

        public void setPictures(Set<Picture> pictures) {
            this.pictures = pictures;
        }

        public boolean isHasMore() {
            return hasMore;
        }

        public void setHasMore(boolean hasMore) {
            this.hasMore = hasMore;
        }
    }

    @Scheduled(fixedRateString = "${application.cacheRefreshRate}")
    public void refreshCache() {
        log.info("Start refreshing cache");
        Set<Picture> pictures = fetchAllPictures();
        Map<String, Set<Picture>> newPictureCache = new HashMap<>();
        pictures.forEach(picture -> {
            Set<String> meta = new HashSet<>(Arrays.asList(picture.getTags().split(" ")));
            meta.add(picture.getAuthor());
            meta.add(picture.getCamera());
            meta.forEach(metaKey -> {
                if (metaKey != null) {
                    Set<Picture> cachedPictures = newPictureCache.getOrDefault(picture.getAuthor(), new HashSet<>());
                    cachedPictures.add(picture);
                    newPictureCache.putIfAbsent(metaKey, cachedPictures);
                }
            });
        });
        pictureCache.clear();
        pictureCache.putAll(newPictureCache);
        log.info("Cache refreshed");
    }
}
