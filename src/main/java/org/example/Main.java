package org.example;

import org.example.config.AppConfig;
import org.example.facade.Facade;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(AppConfig.class);

        Facade facade = context.getBean(Facade.class);

        System.out.println("All trainees loaded from file:");
        facade.showAllTrainees();
        System.out.println("All trainers loaded from file:");
        facade.showAllTrainers();
        System.out.println("All trainings loaded from file:");
        facade.showAllTrainings();

        context.close();
    }
}
