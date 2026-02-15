package org.example.mapper;

import org.example.dto.TrainerProfileResponse;
import org.example.entities.Trainer;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TrainerMapper {

    public TrainerProfileResponse toResponse(Trainer trainer) {
        TrainerProfileResponse response = new TrainerProfileResponse();

        response.setFirstName(trainer.getFirstName());
        response.setLastName(trainer.getLastName());
        response.setTrainingType(trainer.getTrainingType());
        response.setActive(trainer.isActive());

        List<TrainerProfileResponse.TraineeInfo> traineesInfo =
                trainer.getTrainees().stream()
                        .map(t -> new TrainerProfileResponse.TraineeInfo(
                                t.getUsername(),
                                t.getFirstName(),
                                t.getLastName()
                        ))
                        .toList();

        response.setTrainees(traineesInfo);

        return response;
    }
}
