package searchengine.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import searchengine.model.entities.IndexEntity;
import searchengine.model.repositories.IndexEntityCrudRepository;

import java.util.List;

@Service
public class IndexService {

    IndexEntityCrudRepository indexRepository;

    @Autowired
    public IndexService(IndexEntityCrudRepository indexRepository) {
        this.indexRepository = indexRepository;
    }


    public List<IndexEntity> getAllIndexes() {

        return (List<IndexEntity>) indexRepository.findAll();
    }

    public void deleteAllIndexEntity() {
        indexRepository.deleteAll();
    }

    public void deleteIndexEntityById(int id) {
        IndexEntity field = indexRepository.findById(id).get();
        indexRepository.delete(field);
    }

    public IndexEntity updateIndexEntityById(int id, int rank) {
        IndexEntity index = indexRepository.findById(id).get();
        index.setRank(rank);
        indexRepository.save(index);
        return index;
    }

    public IndexEntity readIndexEntityById(int id) {
        return indexRepository.findById(id).get();
    }

    public void createIndexEntity(IndexEntity index) {

        indexRepository.save(index);
    }

    public void createIndexEntityList(List<IndexEntity> indexList) {
        indexRepository.saveAll(indexList);

    }
    public void createListIndexEntityList(List<List<IndexEntity>> indexList) {

        for(int i=0;i<indexList.size();i++){

            indexRepository.saveAll(indexList.get(i));

        }

    }
 }
