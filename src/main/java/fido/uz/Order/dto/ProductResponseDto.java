package fido.uz.Order.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponseDto {


    @NotBlank
    @Size(max = 100)
    @Column(nullable = false)
    private String name;

    @Size(max = 255)
    private String description;

    @NotNull
    @Column(nullable = false)
    private BigDecimal price;

    private String imageUrl;

    private String categoryName;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false)
    private String botToken;

    @NotNull
    @Column(nullable = false)
    private Long userId;
}
