package com.huchenko.demo.web.rest;

import com.huchenko.demo.domain.Picture;
import com.huchenko.demo.service.ImageSearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

/**
 * REST controller for managing image resources.
 */
@RestController
@RequestMapping("/api")
public class ImageSearchResource {

    private final Logger log = LoggerFactory.getLogger(ImageSearchResource.class);


    private final ImageSearchService imageSearchService;

    public ImageSearchResource(ImageSearchService imageSearchService) {
        this.imageSearchService = imageSearchService;
    }


    @GetMapping("/search/{searchTerm}")
    public ResponseEntity<Set<Picture>> search(@PathVariable String searchTerm) {
        log.debug("REST request to search images by term {}", searchTerm);
        return imageSearchService.searchByTerm(searchTerm)
            .map(response -> ResponseEntity.ok().body(response))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
