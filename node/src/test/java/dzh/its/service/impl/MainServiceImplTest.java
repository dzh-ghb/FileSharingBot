package dzh.its.service.impl;

import dzh.its.dao.RawDataDAO;
import dzh.its.entity.RawData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MainServiceImplTest {
    @Autowired
    private RawDataDAO rawDataDAO;

    @Test
    public void testSaveRawData() {
        Update update = new Update();
        Message msg = new Message();
        msg.setText("benzotest");
        update.setMessage(msg);

        RawData rawData = RawData.builder() //сохранение апдейта в RawData
                .event(update)
                .build();
        Set<RawData> testData = new HashSet<>(); //сохранение объекта RawData в hash-коллекцию

        testData.add(rawData); //сохранение значения объекта RawData в коллекцию
        rawDataDAO.save(rawData); //сохранение значения объекта RawData в БД,
        //в этот момент идентификатору устанавливается значение

        //проверка - найдет ли метод contains объект в коллекции
        Assert.isTrue(testData.contains(rawData), "Сущность не найдена в коллекции");
    }
}