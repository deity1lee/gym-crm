
INSERT INTO users (firstName, lastName, username, password, isActive) VALUES ('John', 'Do', 'John.Do', 'pass123456', TRUE);

INSERT INTO users (firstName, lastName, username, password, isActive) VALUES ('John3', 'Doe3', 'John.Doe3', 'pass123453', TRUE);

INSERT INTO trainingTypes (trainingTypeName) VALUES ('yoga');

INSERT INTO trainingTypes (trainingTypeName) VALUES ('cardio');

INSERT INTO trainees (user_id, dateOfBirth, address) VALUES (2, '1999-05-12', 'NY, USA');

INSERT INTO trainers (user_id, training_type_id) VALUES (1, 1);

INSERT INTO trainings (traineeId, trainerId, trainingName, trainingTypeId, trainingDate, trainingDuration) VALUES (2, 1, 'aaa', 1, '1999-05-12', 10);

INSERT INTO trainer_trainee (trainee_id, trainer_id) VALUES (2, 1);
