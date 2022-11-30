package searchengine.services;


import searchengine.dto.index.IndexResponse;

public interface StartIndexService  {
       void getIndexing(boolean IsRunning);
       IndexResponse getIndexing(String urlExtraPage);

}
