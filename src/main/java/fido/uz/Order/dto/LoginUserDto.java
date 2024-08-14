package fido.uz.Order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.antlr.v4.runtime.misc.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginUserDto {

    @NotNull
    private String email;

    @NotNull
    private String password;

}