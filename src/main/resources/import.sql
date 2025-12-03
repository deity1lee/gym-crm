INSERT INTO users (firstName, lastName, username, password, isActive) VALUES ('John', 'Do', 'John.Do', 'pass123456', TRUE);
INSERT INTO users (firstName, lastName, username, password, isActive) VALUES ('John3', 'Doe3', 'John.Doe3', 'pass123453', TRUE);
INSERT INTO users (firstName, lastName, username, password, isActive) VALUES ('Lucy', 'Lee', 'Lucy.Lee', 'pass111222', TRUE);
INSERT INTO users (firstName, lastName, username, password, isActive) VALUES ('Emily', 'Brown', 'Emily.Brown', 'aStrong123', TRUE);
INSERT INTO users (firstName, lastName, username, password, isActive) VALUES ('Joe', 'Perry', 'Joe.Perry', 'Tass123453', TRUE);

INSERT INTO trainingtypes (trainingTypeName) VALUES ('yoga');
INSERT INTO trainingtypes (trainingTypeName) VALUES ('fitness');
INSERT INTO trainingtypes (trainingTypeName) VALUES ('pilates');
INSERT INTO trainingtypes (trainingTypeName) VALUES ('cardio');

INSERT INTO trainees (user_id, dateOfBirth, address) VALUES (2, '1999-05-12', 'NY, USA');
INSERT INTO trainees (user_id, dateOfBirth, address) VALUES (3, '2000-05-12', 'Berlin, Germany');

INSERT INTO trainers (user_id, training_type_id) VALUES (1, 1);
INSERT INTO trainers (user_id, training_type_id) VALUES (4, 2);
INSERT INTO trainers (user_id, training_type_id) VALUES (5, 3);

INSERT INTO trainings (traineeId, trainerId, trainingName, trainingTypeId, trainingDate, trainingDuration) VALUES (2, 1, 'aaa', 2, '2020-05-12', 10);
INSERT INTO trainings (traineeId, trainerId, trainingName, trainingTypeId, trainingDate, trainingDuration) VALUES (3, 1, 'bbb', 1, '2020-05-12', 20);

INSERT INTO trainer_trainee (trainee_id, trainer_id) VALUES (2, 1);
INSERT INTO trainer_trainee (trainee_id, trainer_id) VALUES (3, 5);
