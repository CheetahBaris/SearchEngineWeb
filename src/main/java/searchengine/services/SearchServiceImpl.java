package searchengine.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import searchengine.dto.serachEngine.*;

import java.util.List;


@Service
public class SearchServiceImpl implements SearchService {
    private final PageService pageService;
    private final LemmaService lemmaService;
    private final IndexService indexService;
    private final SiteService siteService;
    private static final int DEFAULT_LIMIT = 20;


    @Autowired
    public SearchServiceImpl(PageService pageService, LemmaService lemmaService, IndexService indexService,
                             SiteService siteService ) {
        this.pageService = pageService;
        this.lemmaService = lemmaService;
        this.indexService = indexService;
        this.siteService = siteService;
     }

    @Override
    public SearchResponse searching(RequestString request) {
        SearchResponse response = new SearchResponse();
        Searcher searcher = new Searcher(pageService, lemmaService, indexService, siteService);

        if(request.getLimit() <= 0){

            request.setLimit(DEFAULT_LIMIT);
        }

         try {

            searcher.setInputLemmas(List.of(new Lemma(request.getQuery().trim().toLowerCase()).getLemmaSet()));
            String[] data = searcher.sorterOfRelevant();
            SearchData[] searchDataArr = new SearchData[request.getLimit()];

            for (int i = 0; i < request.getLimit(); i++) {

                String[] chunk = data[i].split(" \\| ");
                SearchData searchData = new SearchData();

                for (int j = 0; j < chunk.length; j++) {

                    searchData.setSite(chunk[0]);
                    searchData.setSiteName(chunk[1]);
                    searchData.setUri(chunk[2]);
 //                    if(chunk[3].toLowerCase().contains(request.getQuery().toLowerCase())){
//
//                        chunk[3] = chunk[3].toLowerCase().replace(request.getQuery().toLowerCase() ,
//                                "<b>"+request.getQuery().toLowerCase()+"</b>");
//                        searchData.setTitle(chunk[3]);
//                    }else {

                        searchData.setTitle(chunk[3]);
//                    }

                    searchData.setSnippet(chunk[4].substring(0,chunk[4].indexOf("||")-2).trim());
                    searchData.setRelevance(Float.parseFloat(chunk[4].substring(chunk[4].indexOf("||")+2).trim()));
                }
                searchDataArr[i] = searchData;
            }

            response.setResult(true);
            response.setCount(data.length);
            response.setData(searchDataArr);

        } catch (Exception ex) {
             SearchData[] searchData = new SearchData[1];
             SearchData s = new SearchData();
             s.setSiteName("Список пуст (");
             s.setTitle("_ - ;)");
             s.setSnippet(" ");
             searchData[0] = s;


             response.setResult(true);
             response.setCount(0);
             response.setData(searchData);
//             ex.printStackTrace();
        }

        return response;
    }

}
