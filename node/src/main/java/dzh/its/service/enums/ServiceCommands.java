package dzh.its.service.enums;

public enum ServiceCommands { //хранение доступных сервисных команд
    HELP("/help"), //все элементы - объекты этого класса - также можно добавлять поля, методы и конструкторы
    REGISTRATION("/registration"),
    CANCEL("/cancel"),
    START("/start");

    private final String cmd;

    ServiceCommands(String cmd) {
        this.cmd = cmd;
    }

    @Override
    public String toString() {
        return cmd;
    }

    public boolean equals(String cmd) { //сравнение двух команд
        return this.toString().equals(cmd);
    }
}