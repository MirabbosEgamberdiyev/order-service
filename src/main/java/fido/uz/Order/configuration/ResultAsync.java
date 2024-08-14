package fido.uz.Order.configuration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(value = {"responseBody"})
public class ResultAsync {

    @Schema(required = true, description = "Код результата")
    @NotNull
    private Integer code;

    @Schema(required = true, description = "Сообщение")
    @NotNull
    @NotBlank
    private String msg;

    @Schema(description = "Сообщение из базы")
    private String oraMsg;
    public ResultAsync(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }



}
