package searchengine.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import searchengine.model.entities.LemmaEntity;
import searchengine.model.repositories.LemmaEntityCrudRepository;

import javax.persistence.EntityManager;
import java.util.List;

@Service
public class LemmaService {

    LemmaEntityCrudRepository lemmaRepository;

    EntityManager entityManager;
    @Autowired
    public LemmaService(LemmaEntityCrudRepository lemmaRepository, EntityManager entityManager) {
        this.entityManager = entityManager;
        this.lemmaRepository = lemmaRepository;
     }




    public List<LemmaEntity> getAllLemmas(){
        return (List<LemmaEntity>) lemmaRepository.findAll();
    }

    public long getMaxLemmaEntityOfSite(int sitesId){

        return entityManager.createQuery("from lemma where site_id = "+ sitesId).getResultList().size();

    }
    public LemmaEntity getLemmaEntityById(int id){

        return lemmaRepository.findById(id).get();
    }

    public void deleteAllLemmaEntity(){
        lemmaRepository.deleteAll();
    }
    public void deleteLemmaEntityById(int id){
        LemmaEntity lemma = lemmaRepository.findById(id).get();
        lemmaRepository.delete(lemma);
    }

    public LemmaEntity updateLemmaEntityById(int id, int frequency  ){
        LemmaEntity lemma = lemmaRepository.findById(id).get();
        lemma.setFrequency(frequency);
        lemmaRepository.save(lemma);
        return lemma;
    }

    public LemmaEntity readlemmaEntityById(int id){
        return lemmaRepository.findById(id).get();
    }

    public void createlemmaEntity(LemmaEntity lemma ){

        lemmaRepository.save(lemma);
    }
    public void createlemmaEntityList(List<LemmaEntity> lemmaList ){

             lemmaRepository.saveAll(lemmaList);
    }
}
