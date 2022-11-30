package searchengine.dto.index;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import searchengine.config.Site;
import searchengine.dto.serachEngine.Lemma;
import searchengine.model.entities.*;
import searchengine.services.IndexService;
import searchengine.services.LemmaService;
import searchengine.services.PageService;
import searchengine.services.SiteService;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;


@Service
public class SiteIndexingLogic implements Runnable {
    private final PageService pageService;
    private final LemmaService lemmaService;
    private boolean isRunning;
    private final Site site;
    private final SiteService siteService;
    private final IndexService indexService;
    private String urlExtraPage;


    public SiteIndexingLogic(SiteService siteService, Site site, PageService pageService,
                             LemmaService lemmaService, IndexService indexService) {
        this.pageService = pageService;
        this.lemmaService = lemmaService;
        this.site = site;
        this.siteService = siteService;
        this.indexService = indexService;

    }


    @Override
    public void run() {
        doIndexing(isRunning);
        try {
            doIndexing(urlExtraPage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean doIndexing(boolean isRunning) {



        this.isRunning = isRunning;
        boolean doHib = false;

        try {

            ForkJoinPool forkJoinPool = ForkJoinPool.commonPool();
            SiteIndexing siteIndexing =  new SiteIndexing(siteService, pageService, lemmaService,
                    site.getUrl(), site.getUrl(), site, indexService);


            if (!isRunning) {

                siteIndexing.setIsRunning(false);
               forkJoinPool.execute(siteIndexing);

                doHib = true;

            } else {


                if(!forkJoinPool.isShutdown()){

//                    ForkJoinPool.commonPool().de

                    siteIndexing.setIsRunning(true);
                    forkJoinPool.shutdown();



                 }


                siteService.updateTimeAndTypeSiteEntityById(site.getOrder(), TypesOfIndexes.FAILED,
                        LocalDateTime.now(), "Индексация остановлена пользователем");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return doHib;
    }

    public boolean doIndexing(String urlExtraPage) throws IOException {

        this.urlExtraPage = urlExtraPage;
        boolean doHib = false;
        String url = urlExtraPage.replace(site.getUrl(), "");

        Connection.Response response = Jsoup.connect(urlExtraPage)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko)" +
                        " Chrome/104.0.5112.124 YaBrowser/22.9.5.710 Yowser/2.5 Safari/537.36")
                .timeout(new Random().nextInt(500, 1500))
                .referrer("https://Yandex.ru")
                .ignoreHttpErrors(true)
                .execute();

        Document document = response.parse();
        System.out.println(url);

        boolean exist = false;
        try {


            for (PageEntity p : pageService.getAllPages()) {
                if (p.getPath().equals(url)) {

                    pageService.updatePageEntityById(p.getId(), url, response.statusCode(), document.html());
                    siteService.updateTimeAndTypeSiteEntityById(site.getOrder(), TypesOfIndexes.INDEXING
                            , LocalDateTime.now(), "");
                    exist = true;
                    break;
                }

            }

            if (!exist) {
                PageEntity pageEntity = new PageEntity();
                pageEntity.setPath(url);
                pageEntity.setCode(response.statusCode());
                pageEntity.setContent(document.html());

                for (SiteEntity siteEntity : siteService.getAllSites()) {

                    if (siteEntity.getId() == site.getOrder()) {

                        pageEntity.setSite_id(siteEntity);
                    }
                }
                pageService.createPageEntity(pageEntity);


                Lemma lemFull = new Lemma(document.text());
                List<IndexEntity> indexEntities = new ArrayList<>();

                for (String s : lemFull.collectUniqueLemmas()) {

                    LemmaEntity lemma = new LemmaEntity();
                    lemma.setLemma(s.replaceAll("[\\d-]", "").trim());
                    for (SiteEntity siteEntity : siteService.getAllSites()) {

                        if (siteEntity.getId() == site.getOrder()) {

                            lemma.setSite_id(siteEntity);
                        }
                    }
                    lemma.setFrequency(1);
                    lemmaService.createlemmaEntity(lemma);

                    IndexEntity index = new IndexEntity();
                    index.setPage_id(pageEntity.getId());
                    index.setLemma_id(lemma.getId());
                    index.setRank(Integer.parseInt(s.replaceAll("[\\D-]", "")));
                    indexEntities.add(index);
                }


                indexService.createIndexEntityList(indexEntities);


            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        siteService.updateTimeAndTypeSiteEntityById(site.getOrder(), TypesOfIndexes.INDEXED
                , LocalDateTime.now(), "");

        return true;
    }
}