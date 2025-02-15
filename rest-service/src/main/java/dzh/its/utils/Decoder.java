package dzh.its.utils;

import lombok.RequiredArgsConstructor;
import org.hashids.Hashids;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class Decoder { //класс для шифрования/дешифрования
    private final Hashids hashids; //объект класса из библиотеки для шифрования

    //дешифрование хеша в число
    public Long idOf(String value) {
        long[] res = hashids.decode(value); //метод декодирования (дешифрования) из библиотеки Hashids
        if (res != null && res.length > 0) {
            return res[0];
        }
        return null;
    }
}