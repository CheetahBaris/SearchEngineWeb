package searchengine.services;

import searchengine.dto.serachEngine.RequestString;
import searchengine.dto.serachEngine.SearchResponse;

public interface SearchService {
    SearchResponse searching(RequestString query);
}
