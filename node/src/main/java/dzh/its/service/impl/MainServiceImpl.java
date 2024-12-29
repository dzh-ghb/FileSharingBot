package dzh.its.service.impl;

import dzh.its.dao.RawDataDAO;
import dzh.its.entity.RawData;
import dzh.its.service.MainService;
import dzh.its.service.ProducerService;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
public class MainServiceImpl implements MainService {
    private final RawDataDAO rawDataDAO;
    private final ProducerService producerService;

    public MainServiceImpl(RawDataDAO rawDataDAO, ProducerService producerService) {
        this.rawDataDAO = rawDataDAO;
        this.producerService = producerService;
    }

    @Override
    public void processTextMessage(Update update) {
        saveRawData(update);

        Message message = update.getMessage();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId());
        sendMessage.setText("FROM NODE");
        producerService.produceAnswer(sendMessage); //передача сообщения от узла
    }

    private void saveRawData(Update update) { //создание объекта RawData
        RawData rawData = RawData.builder() //использование паттерна Builder
                .event(update)
                .build();
        rawDataDAO.save(rawData); //метод автоматически создается Spring через JpaRepository
    }
}