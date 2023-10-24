package ru.practicum.shareit.id_generator;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class IdGenerator {
    private int generatedId = 0;

    public int generateId() {
        return ++generatedId;
    }
}
