package fido.uz.Order.configuration;


import fido.uz.Order.exception.CustomizedRequestException;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.Arrays;

@AllArgsConstructor
@NoArgsConstructor

public enum AcceptLanguage {

    EN("en"),
    RU("ru"),
    UZC("uzc"),
    UZL("uzl");


    private String value;



    @Override
    public String toString() {
        return String.valueOf(value);
    }


    public static AcceptLanguage fromValue(String text) throws CustomizedRequestException {
        for (AcceptLanguage b : values()) {
            if (String.valueOf(b.value).equals(text)) {
                return b;
            }
        }
//        return AcceptLanguage.EN;
        throw new CustomizedRequestException(
                "Not acceptable language in Accept-Language header case. Allowed values are " + Arrays.toString(values()), 2, 406);
    }


}
