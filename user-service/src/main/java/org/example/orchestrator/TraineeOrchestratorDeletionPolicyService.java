package org.example.orchestrator;

import org.example.policy.TraineeDeletionPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TraineeOrchestratorDeletionPolicyService {

    private TraineeDeletionPolicy deletionPolicy;

    @Autowired
    public void setDeletionPolicy(TraineeDeletionPolicy deletionPolicy) {
        this.deletionPolicy = deletionPolicy;
    }

    public void deleteTraineeByUsername(String username) {
        deletionPolicy.deleteTrainee(username);
    }
}
