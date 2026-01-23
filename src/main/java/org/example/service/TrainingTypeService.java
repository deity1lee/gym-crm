package org.example.service;

import org.example.dao.TrainingTypeDao;
import org.example.entities.TrainingType;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
@Service
public class TrainingTypeService {

    private final TrainingTypeDao trainingTypeDao;

    public TrainingTypeService(TrainingTypeDao trainingTypeDao){
        this.trainingTypeDao = trainingTypeDao;
    }

    public List<TrainingType> getAllTrainingTypes() {
        return trainingTypeDao.findAll();
    }

    public TrainingType getTrainingTypeById(Long trainingTypeId) {
        return trainingTypeDao.findById(trainingTypeId)
                .orElseThrow(() -> new IllegalArgumentException("No such training type with provided id"));
    }
}
