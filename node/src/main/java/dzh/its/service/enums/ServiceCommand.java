package dzh.its.service.enums;

public enum ServiceCommand { //хранение доступных сервисных команд
    HELP("/help"), //все элементы - объекты этого класса - также можно добавлять поля, методы и конструкторы
    REGISTRATION("/registration"),
    CANCEL("/cancel"),
    START("/start");

    private final String value;

    ServiceCommand(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

    //перебор всех Enum-объектов класса и сравнение со строкой из параметров, цель - найти Enum-объект с таким же значением
    public static ServiceCommand fromValue(String v) {
        for (ServiceCommand c : ServiceCommand.values()) {
            if (v.equals(c.value)) {
                return c;
            }
        }
        return null;
    }
}