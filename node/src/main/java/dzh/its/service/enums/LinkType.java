package dzh.its.service.enums;

public enum LinkType { //хранение идентификаторов ресурсов для генерации ссылок
    GET_DOC("file/get-doc"), //urn для скачивания документа
    GET_PHOTO("file/get-photo"); //urn для скачивания фото

    private final String link;

    LinkType(String link) {
        this.link = link;
    }

    //если не переопределить метод, то будет возвращено имя Enum-объекта, а не часть ссылки (параметр)
    @Override
    public String toString() {
        return link;
    }
}