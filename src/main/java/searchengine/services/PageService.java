package searchengine.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import searchengine.model.entities.PageEntity;
import searchengine.model.repositories.PageEntityCrudRepository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;
import java.util.Map;

@Service
public class PageService {

    PageEntityCrudRepository pageRepository;

       @Autowired
    public PageService(PageEntityCrudRepository pageRepository ) {
         this.pageRepository = pageRepository;
    }


    public List<PageEntity> getAllPages(){

          return (List<PageEntity>) pageRepository.findAll();
    }

    public String[] getAllPaths(){
           List<PageEntity> pageEntities =  (List<PageEntity>) pageRepository.findAll();
           String[] paths = new String[pageEntities.size()];

        for(int i=0; i<pageEntities.size();i++){
               paths[i] = pageEntities.get(i).getPath();
           }

        return paths;
     }
//    public int getMaxPageBySiteId(int site_id){
//           String hql= "FROM page where site_id = " + site_id;
//           entityManager.getEntityManagerFactory().createEntityManager(Map.of(PageEntity.class, Integer.class));
//
//        Query query = entityManager.createQuery(hql);
//        int c = query.getMaxResults();
//
//           return c;
//    }



    public PageEntity getPageEntityById(int id){
           return pageRepository.findById(id).get();
    }


    public void deleteAllPageEntity(){
         pageRepository.deleteAll();
    }
    public void deletePageEntityById(int id){
        PageEntity page = pageRepository.findById(id).get();
           pageRepository.delete(page);
    }

    public PageEntity updatePageEntityById(int id, String path, int code, String content ){
        PageEntity page = pageRepository.findById(id).get();
         page.setPath(path);
        page.setCode(code);
        page.setContent(content);
        pageRepository.save(page);
        return page;
    }

    public PageEntity readPageEntityById(int id){
            return pageRepository.findById(id).get();
    }

    public synchronized void createPageEntity(PageEntity page ){

           pageRepository.save(page);
    }


}
