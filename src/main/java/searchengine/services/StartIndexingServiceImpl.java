package searchengine.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import searchengine.config.Site;
import searchengine.config.SitesList;
import searchengine.dto.index.IndexResponse;
import searchengine.dto.index.DataForIndexing;
import searchengine.dto.index.SiteIndexingLogic;
import searchengine.model.DBConnection;
import searchengine.model.entities.SiteEntity;
import searchengine.model.entities.TypesOfIndexes;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;


@Service
 public class StartIndexingServiceImpl implements StartIndexService {

    private final SitesList sites;
    private final PageService pageService;
    private final LemmaService lemmaService;
    private final SiteService siteService;
    private final DataForIndexing dataForIndexing;
    private final IndexService indexService;


    @Autowired
    public StartIndexingServiceImpl(SitesList sites, PageService pageService, LemmaService lemmaService
            , SiteService siteService, IndexService indexService, DataForIndexing dataForIndexing) {
        this.pageService = pageService;
        this.lemmaService = lemmaService;
        this.sites = sites;
        this.siteService = siteService;
        this.dataForIndexing = dataForIndexing;
        this.indexService = indexService;
    }


    public void getIndexing(boolean isRunning) {

        dataForIndexing.setIndexPage(false);

        Thread[] threads = new Thread[sites.getSites().size()];


        DBConnection.getConnection();

        for (int i = 0; i < threads.length; i++) {

            SiteEntity siteEntity = new SiteEntity();
            siteEntity.setStatus(TypesOfIndexes.INDEXING);
            siteEntity.setName(sites.getSites().get(i).getName());
            siteEntity.setUrl(sites.getSites().get(i).getUrl());
            siteEntity.setLast_error("");
            siteEntity.setStatus_time(LocalDateTime.now());
            siteService.createSiteEntity(siteEntity);

            threads[i] = new Thread( new SiteIndexingLogic(siteService, sites.getSites().get(i)
                    , pageService, lemmaService, indexService).doIndexing(isRunning)+"-"+i);
        }

        Arrays.stream(threads).forEach(Thread::start);

    }


    @Override
    public IndexResponse getIndexing(String urlExtraPage) {
        dataForIndexing.setIndexPage(true);

        urlExtraPage = URLDecoder.decode(urlExtraPage, StandardCharsets.UTF_8);

        if(urlExtraPage.startsWith("url=")){

            urlExtraPage = urlExtraPage.replaceFirst("url=","");
        }

        IndexResponse response = new IndexResponse();
        Site site = new Site();
        boolean isCorrect = false;

        for (Site s : sites.getSites()) {

            if (urlExtraPage.contains(s.getUrl())) {

                site = s;
                isCorrect = true;
                break;
            }
        }

        try {

            if (isCorrect) {

                new Thread(String.valueOf(new SiteIndexingLogic(siteService, site
                        , pageService, lemmaService,indexService).doIndexing(urlExtraPage))).start();
                response.setResult(true);
                response.setError("");
            } else {

                response.setResult(false);
                response.setError("Данная страница находится за пределами сайтов, " +
                        "указанных в конфигурационном файле");
            }
        } catch (Exception ex) {

            ex.printStackTrace();
        }

        return response;
    }
}
