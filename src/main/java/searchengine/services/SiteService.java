package searchengine.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
 import searchengine.model.entities.SiteEntity;
import searchengine.model.entities.TypesOfIndexes;
import searchengine.model.repositories.SiteEntityCrudRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SiteService {


        SiteEntityCrudRepository  siteRepository;

        @Autowired
        public SiteService(SiteEntityCrudRepository  siteRepository) {
            this.siteRepository = siteRepository;
        }


        public List<SiteEntity> getAllSites(){

            return (List<SiteEntity>) siteRepository.findAll();
        }

        public void deleteAllSiteEntity(){
            siteRepository.deleteAll();
        }
        public void deleteSiteEntityById(int id){
            SiteEntity site = siteRepository.findById(id).get();
            siteRepository.delete(site);
        }
    public SiteEntity updateTimeAndTypeSiteEntityById(int id, TypesOfIndexes type, LocalDateTime dateTime, String err){
        SiteEntity site = siteRepository.findById(id).get();
        site.setStatus(type);
        site.setStatus_time(dateTime);
        site.setLast_error(err);
        siteRepository.save(site);
        return site;
    }

        public SiteEntity updateSiteEntityById(int id, String name  ){
            SiteEntity site = siteRepository.findById(id).get();
            site.setName(name);
            siteRepository.save(site);
            return site;
        }

        public SiteEntity readSiteEntityById(int id){
            return siteRepository.findById(id).get();
        }

        public void createSiteEntity(SiteEntity site ){

            siteRepository.save(site);
        }

    }
