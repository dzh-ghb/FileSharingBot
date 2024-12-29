package dzh.its.entity.enums;

public enum UserState {
    BASIC_STATE, //ожидание ввода разрешенных команд или возврата сообщения о некорректном вводе
    WAIT_FOR_EMAIL_STATE //ожидание ввода почты или отмены регистрации
}