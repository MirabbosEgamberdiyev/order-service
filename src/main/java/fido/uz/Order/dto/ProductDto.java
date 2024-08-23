package fido.uz.Order.dto;

import fido.uz.Order.entity.Category;
import jakarta.persistence.*;
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
public class ProductDto {


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

    private Long category_id;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false)
    private String botToken;

    @NotNull
    @Column(nullable = false)
    private Long userId;
}
