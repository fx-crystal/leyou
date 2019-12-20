package com.leyou.search.controller;

import com.leyou.search.pojo.SearchRequest;
import com.leyou.search.pojo.SearchResult;
import com.leyou.search.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class SearchController {
    @Autowired
    private SearchService searchService;
    @PostMapping("page")
    public ResponseEntity<SearchResult> search(@RequestBody SearchRequest searchRequest){
        SearchResult searchResult=searchService.search(searchRequest);
        if(searchResult==null||searchResult.getItems().size()==0){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return ResponseEntity.ok(searchResult);
    }

}
