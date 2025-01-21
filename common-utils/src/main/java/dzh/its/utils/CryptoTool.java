package dzh.its.utils;

import org.hashids.Hashids;

public class CryptoTool { //класс для шифрования/дешифрования
    private final Hashids hashids; //объект класса из библиотеки для шифрования

    public CryptoTool(String salt) { //параметр - ключ шифрования (salt)
        int minHashLength = 10; //минимальная длина генерируемого хеша
        this.hashids = new Hashids(salt, minHashLength); //передача ключа и длины хеша в объект Hashids
    }

    //хеширование числа (идентификатора)
    public String hashOf(Long value) {
        return hashids.encode(value); /*метод кодирования из библиотеки Hashids
        (по факту происходит шифрование, а не кодирование,
        т.к. используется ключ шифрования - salt и никто не будет знать реальный id)*/
    }

    //дешифрование хеша в число
    public Long idOf(String value) {
        long[] res = hashids.decode(value); //метод декодирования (дешифрования) из библиотеки Hashids
        if (res != null && res.length > 0) {
            return res[0];
        }
        return null;
    }
}