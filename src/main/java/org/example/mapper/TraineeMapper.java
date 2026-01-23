package org.example.mapper;

import org.example.dto.TraineeProfileResponse;
import org.example.entities.Trainee;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TraineeMapper {

    public TraineeProfileResponse toResponse(Trainee trainee) {
        TraineeProfileResponse response = new TraineeProfileResponse();

        response.setUsername(trainee.getUsername());
        response.setFirstName(trainee.getFirstName());
        response.setLastName(trainee.getLastName());
        response.setDateOfBirth(trainee.getDateOfBirth());
        response.setAddress(trainee.getAddress());
        response.setActive(trainee.isActive());

        List<TraineeProfileResponse.TrainerInfo> trainersInfo =
                trainee.getTrainers().stream()
                        .map(t -> new TraineeProfileResponse.TrainerInfo(
                                t.getUsername(),
                                t.getFirstName(),
                                t.getLastName(),
                                t.getTrainingType()
                        ))
                        .toList();

        response.setTrainers(trainersInfo);

        return response;
    }
}
