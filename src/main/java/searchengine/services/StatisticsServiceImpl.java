package searchengine.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import searchengine.config.Site;
import searchengine.config.SitesList;
import searchengine.dto.statics.DetailedStatisticsItem;
import searchengine.dto.statics.StatisticsData;
import searchengine.dto.statics.StatisticsResponse;
import searchengine.dto.statics.TotalStatistics;
import searchengine.model.entities.LemmaEntity;
import searchengine.model.entities.PageEntity;
import searchengine.model.entities.TypesOfIndexes;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
 public class StatisticsServiceImpl implements StatisticsService {

    private final SitesList sites;
    private final PageService pageService;
    private final SiteService siteService;
    private final LemmaService lemmaService;

    @Autowired
    public StatisticsServiceImpl(SitesList sites, PageService pageService, SiteService siteService,
                                 LemmaService lemmaService) {
        this.sites = sites;
        this.pageService = pageService;
        this.siteService = siteService;
        this.lemmaService = lemmaService;
    }

    @Override
    public StatisticsResponse getStatistics() {

        TotalStatistics total = new TotalStatistics();
        total.setSites(sites.getSites().size());
        total.setIndexing(true);

        List<DetailedStatisticsItem> detailed = new ArrayList<>();
        List<Site> sitesList = sites.getSites();

        for (int i = 0; i < sitesList.size(); i++) {
            long timeStamp = 0;
            long pages = 0;
            long lemmas = 0;
            TypesOfIndexes types;
            String errMessage = "";


            Site site = sitesList.get(i);
            DetailedStatisticsItem item = new DetailedStatisticsItem();
            item.setName(site.getName());
            item.setUrl(site.getUrl());


            if (!pageService.getAllPages().isEmpty()) {

                List<PageEntity> pageList = pageService.getAllPages();


                for (int j = 0; j < pageList.size(); j++) {

                    if (pageList.get(j).getSite_id().getId() == i + 1) {
                        pages++;
//                        pages = pageService.getMaxPageBySiteId(i + 1);
//                        System.out.println(pages);

                    }
                }
            }

            if (!lemmaService.getAllLemmas().isEmpty()) {

                List<LemmaEntity> lemmaEntities = lemmaService.getAllLemmas();

                for (int j = 0; j < lemmaEntities.size(); j++) {

                    if (lemmaEntities.get(j).getSite_id().getId() == i + 1) {

                        if (i == 0) {
                            lemmas = lemmaEntities.get(j).getId();
                        } else {
                            lemmas = lemmaEntities.get(j).getId() - total.getLemmas();
                        }


//                         lemmas = lemmaService.getMaxLemmaEntityOfSite(i+1);
//                        System.out.println(lemmas);
                        //
                    }
                }
            }

            if (!siteService.getAllSites().isEmpty()) {
                Timestamp timestamp;
                TypesOfIndexes t;
                String errMess;

                try {
                    timestamp = Timestamp.valueOf(siteService.getAllSites().get(i).getStatus_time()
                            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")));
                    t = siteService.getAllSites().get(i).getStatus();
                    errMess = siteService.getAllSites().get(i).getLast_error();

                } catch (Exception ex) {
                    timestamp = Timestamp.valueOf(LocalDateTime.now()
                            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")));
                    t = TypesOfIndexes.FAILED;
                    errMess = "Нет данных";

                }


                timeStamp = timestamp.getTime();
                types = t;
                errMessage = errMess;

            } else {

                types = TypesOfIndexes.FAILED;
            }

            item.setPages(pages);
            item.setLemmas(lemmas);
            item.setStatus(types);
            item.setError(errMessage);
            item.setStatusTime(timeStamp);
            total.setPages(total.getPages() + pages);
            total.setLemmas(total.getLemmas() + lemmas);
            detailed.add(item);
        }

        StatisticsResponse response = new StatisticsResponse();
        StatisticsData data = new StatisticsData();
        data.setTotal(total);
        data.setDetailed(detailed);
        response.setStatistics(data);
        response.setResult(true);

        return response;
    }
}