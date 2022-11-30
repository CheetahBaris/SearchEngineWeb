package searchengine.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import searchengine.model.entities.FieldEntity;
import searchengine.model.repositories.FieldEntityCrudRepository;

import java.util.List;

@Service
public class FieldService {

    FieldEntityCrudRepository fieldRepository;

    @Autowired
    public FieldService(FieldEntityCrudRepository fieldRepository) {
        this.fieldRepository = fieldRepository;
    }


    public List<FieldEntity> getAllFields(){

        return (List<FieldEntity>) fieldRepository.findAll();
    }

    public void deleteAllFieldsEntity(){
        fieldRepository.deleteAll();
    }
    public void deleteFieldEntityById(int id){
        FieldEntity field = fieldRepository.findById(id).get();
        fieldRepository.delete(field);
    }

    public FieldEntity updateFieldEntityById(int id, String name  ){
        FieldEntity field = fieldRepository.findById(id).get();
        field.setName(name);
        fieldRepository.save(field);
        return field;
    }

    public FieldEntity readFieldEntityById(int id){
        return fieldRepository.findById(id).get();
    }

    public void createFieldEntity(FieldEntity field ){

        fieldRepository.save(field);
    }


}
