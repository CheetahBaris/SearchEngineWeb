package searchengine.dto.index;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Document;
import searchengine.config.Site;
import searchengine.dto.serachEngine.Lemma;
import searchengine.model.entities.*;
import searchengine.services.IndexService;
import searchengine.services.LemmaService;
import searchengine.services.PageService;
import searchengine.services.SiteService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.RecursiveAction;

 public class SiteIndexing extends RecursiveAction {

    private final String way;
    private final String extension;
    private final Site site;
    private final SiteService siteService;
    private final PageService pageService;
    private final LemmaService lemmaService;
    private final IndexService indexService;
    private static Vector<String> allLinks = new Vector<>();
    private boolean isRunning;


    public SiteIndexing(SiteService siteService, PageService pageService, LemmaService lemmaService,
                        String way, String extension, Site site, IndexService indexService) {

        this.pageService = pageService;
        this.lemmaService = lemmaService;
        this.siteService = siteService;
        this.indexService = indexService;
        this.way = way;
        this.extension = extension;
        this.site = site;
     }



    @Override
    public void compute() {


            getChildren();





            if (getPool().getActiveThreadCount() == 1
                    && getPool().getQueuedTaskCount() == 0
                    && getPool().getQueuedSubmissionCount() == 0) {

                siteService.updateTimeAndTypeSiteEntityById(site.getOrder(),
                        TypesOfIndexes.INDEXED, LocalDateTime.now(), "");

                getPool().shutdown();
            }





    }

     public void getChildren() {


        try {

            Connection.Response response = Jsoup.connect(way)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko)" +
                            " Chrome/104.0.5112.124 YaBrowser/22.9.5.710 Yowser/2.5 Safari/537.36")
                    .timeout(new Random().nextInt(1000, 2000))
                    .referrer("https://Yandex.ru")
                    .ignoreHttpErrors(true)
                    .execute();

            Document document = response.parse();
            Elements element = document.select("a");
            ArrayList<String> newLinks = new ArrayList<>(element.eachAttr("abs:href"));




            for (int i = 0; i < newLinks.size(); i++) {

                if(!isRunning) {


                    if (!newLinks.get(i).isEmpty() && newLinks.get(i).startsWith(extension)
                            && !allLinks.contains(newLinks.get(i)) && !newLinks.get(i)
                            .contains("#") && !newLinks.get(i).endsWith(".jpg")
                            && !newLinks.get(i).endsWith(".pdf")) {

                        SiteIndexing task = new SiteIndexing(siteService, pageService, lemmaService,
                                newLinks.get(i), extension, site, indexService);
                        task.fork();

                        allLinks.add(newLinks.get(i));

                    }
                }else {
                    getPool().shutdownNow();
                }
            }

            connect(response, document);


        } catch (IOException e) {


            PageEntity pageEntity = new PageEntity();
            String name = way.replace(extension, "");
            pageEntity.setPath(name.isEmpty() ? "/" : name);
            pageEntity.setCode(500);

            for (SiteEntity siteEntity : siteService.getAllSites()) {
                if (siteEntity.getId() == site.getOrder()) {
                    pageEntity.setSite_id(siteEntity);
                }
            }

            pageEntity.setContent("ERROR");
            pageService.createPageEntity(pageEntity);

            e.printStackTrace();
        }

    }

    public void connect(Connection.Response response, Document document) throws IOException {


        PageEntity pageEntity = new PageEntity();
        String name = way.replace(extension, "");
        pageEntity.setPath(name.isEmpty() ? "/" : name);
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


        siteService.updateTimeAndTypeSiteEntityById(site.getOrder(), TypesOfIndexes.INDEXING
                , LocalDateTime.now(), "");

    }
    public void  setIsRunning(boolean isRunning){
        this.isRunning= isRunning;
    }
}
