package org.example.policy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Profile("prod")
@Service
public class ProdTrainerDeletionPolicy implements TraineeDeletionPolicy{

    @Override
    public void deleteTrainee(String username) {
        throw new UnsupportedOperationException(
                "Deleting trainee is NOT allowed in production"
        );
    }
}
