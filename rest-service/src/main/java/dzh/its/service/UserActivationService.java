package dzh.its.service;

public interface UserActivationService { //для обработки запросов на активацию юзеров
    boolean activation(String cryptoUserId); //в параметрах - зашифрованный идентификатор юзера
}