package trainee;

import org.example.dao.TraineeDao;
import org.example.entities.Trainee;
import org.example.service.TraineeService;
import org.example.storage.InMemoryStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TraineeServiceIntegrationTest {

    private TraineeService traineeService;
    private TraineeDao traineeDao;
    private InMemoryStorage storage;

    @BeforeEach
    void setUp() {
        storage = new InMemoryStorage();
        traineeDao = new TraineeDao();
        traineeDao.setStorage(storage);

        traineeService = new TraineeService();
        traineeService.setTraineeDao(traineeDao);

        Trainee existing = new Trainee(1L, "John", "Smith", "1998-02-15", "New York");
        traineeDao.save(existing);
    }

    @Test
    void testCreateTraineeAppendsNumberIfDuplicateName() {
        Trainee newTrainee = new Trainee(2L, "John", "Smith", "1999-06-30", "Boston");

        traineeService.createTrainee(newTrainee);

        assertEquals("John.Smith2", newTrainee.getUsername());
        assertTrue(storage.getTraineeMap().containsKey(2L));
    }
}

