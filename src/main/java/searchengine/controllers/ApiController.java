package searchengine.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import searchengine.dto.index.DataForIndexing;
import searchengine.dto.index.IndexResponse;
import searchengine.dto.serachEngine.RequestString;
import searchengine.dto.serachEngine.SearchResponse;
import searchengine.model.DBConnection;
import searchengine.services.*;
import searchengine.dto.statics.StatisticsResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api")
public class ApiController {

    private final StatisticsService statisticsService;
    private final StartIndexService startIndexService;
    private final SearchService searchService;
    private boolean isRunning = false;


    @Autowired
    public ApiController(StatisticsService statisticsService, StartIndexService startIndexService,
                         SearchService searchService, DataForIndexing dataForIndexing) {

        this.statisticsService = statisticsService;
        this.searchService = searchService;
        this.startIndexService = startIndexService;
     }

    @GetMapping("/statistics")
    public ResponseEntity<StatisticsResponse> statistics() {
        try {

        return ResponseEntity.ok(statisticsService.getStatistics());

        }catch (Exception ex){
            StatisticsResponse statisticsResponse = new StatisticsResponse();
            statisticsResponse.setResult(false);
            return ResponseEntity.badRequest().body(statisticsResponse);
        }
    }

    @GetMapping("/startIndexing")
    public ResponseEntity<IndexResponse> startIndexing() {

        try {
            IndexResponse indexResponse = new IndexResponse();
            if (isRunning) {

                indexResponse.setError("Индексация уже запущена");
                indexResponse.setResult(false);
            } else {

                indexResponse.setResult(true);
                indexResponse.setError("");
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        startIndexService.getIndexing(false);

                    }
                };

                Thread thread = new Thread(runnable);
                thread.start();
                isRunning = true;
             }

            return ResponseEntity.ok(indexResponse);
        }catch (Exception ex){
            IndexResponse indexResponse = new IndexResponse();
            indexResponse.setResult(false);
            indexResponse.setError("Указанная страница не найдена");
            return ResponseEntity.badRequest().body(indexResponse);
        }
    }


    @GetMapping("/stopIndexing")
    public ResponseEntity<IndexResponse> stopIndexing() {
try {
        IndexResponse indexResponse = new IndexResponse();

        if (!isRunning) {

            indexResponse.setResult(false);
            indexResponse.setError("Индексация не запущена");
        } else {

            indexResponse.setResult(true);
            indexResponse.setError("");
            startIndexService.getIndexing(true);
            isRunning = false;

        }

        return ResponseEntity.ok(indexResponse);
    }catch (Exception ex){
        IndexResponse indexResponse = new IndexResponse();
        indexResponse.setResult(false);
        indexResponse.setError("Указанная страница не найдена");
        return ResponseEntity.badRequest().body(indexResponse);
    }

    }

    @PostMapping("/indexPage")
    public ResponseEntity<IndexResponse> indexPage(@RequestBody String urlExtraPage) {
        try {

         if(urlExtraPage.startsWith("url=http%3A%2F%2Fwww.") || urlExtraPage.startsWith("url=https%3A%2F%2Fwww.")){

            return ResponseEntity.ok(startIndexService.getIndexing(urlExtraPage));
        }else {

            IndexResponse response = new IndexResponse();
            response.setResult(false);
            response.setError("Неверный адресс");
            return  ResponseEntity.ok(response);
        }
    }catch (Exception ex){
        IndexResponse indexResponse = new IndexResponse();
        indexResponse.setResult(false);
        indexResponse.setError("Указанная страница не найдена");
        return ResponseEntity.badRequest().body(indexResponse);
    }
     }


    @GetMapping("/search")
    public ResponseEntity<SearchResponse> getSearch(@RequestParam("query") String query, @RequestParam("offset")
            int offset, @RequestParam("limit") int limit) {
        try {


            if (query.trim().isEmpty()) {

                SearchResponse response = new SearchResponse();
                response.setData(null);
                response.setCount(0);
                response.setResult(false);
                response.setError("Задан пустой поисковой запрос");
                return ResponseEntity.ok(response);
            } else {

                RequestString requestString = new RequestString();
                requestString.setQuery(query);
                requestString.setOffset(offset);
                requestString.setLimit(limit);
                return ResponseEntity.ok(searchService.searching(requestString));
            }
        } catch (Exception ex) {
            SearchResponse searchResponse = new SearchResponse();
            searchResponse.setResult(false);
            searchResponse.setError("Указанная страница не найдена");
            return ResponseEntity.badRequest().body(searchResponse);
        }
    }
}