package fido.uz.Order.controller;

import fido.uz.Order.dto.ProductDto;
import fido.uz.Order.dto.ProductResponseDto;
import fido.uz.Order.entity.Product;
import fido.uz.Order.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/products")
@RestController
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // CREATE
    @Operation(summary = "Create a new product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Product successfully created",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Product.class))),
            @ApiResponse(responseCode = "409", description = "Product already exists",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(mediaType = "application/json"))
    })
    @PostMapping("/create")
    public ResponseEntity<?> createProduct(@RequestBody ProductDto productDto) {
        try {
            Product product = productService.createProduct(productDto);
            return new ResponseEntity<>(product, HttpStatus.CREATED);
        } catch (IllegalArgumentException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
        } catch (Exception ex) {
            return new ResponseEntity<>("Invalid input data", HttpStatus.BAD_REQUEST);
        }
    }

    // READ - Get Product by ID
    @Operation(summary = "Get product by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Product.class))),
            @ApiResponse(responseCode = "404", description = "Product not found",
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Long id) {
        try {
            ProductResponseDto product = productService.getProductById(id);
            return new ResponseEntity<>(product, HttpStatus.OK);
        } catch (IllegalArgumentException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    // READ - Get All Products
    @Operation(summary = "Get all products")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Products retrieved",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Product.class)))
    })
    @GetMapping("/all")
    public ResponseEntity<List<ProductResponseDto>> getAllProducts() {
        List<ProductResponseDto> products = productService.getAllProducts();
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    // UPDATE
    @Operation(summary = "Update an existing product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product updated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Product.class))),
            @ApiResponse(responseCode = "404", description = "Product not found",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "409", description = "Product with this name already exists",
                    content = @Content(mediaType = "application/json"))
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @RequestBody ProductDto productDto) {
        try {
            Product updatedProduct = productService.updateProduct(id, productDto);
            return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
        } catch (IllegalArgumentException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            return new ResponseEntity<>("Product update failed", HttpStatus.CONFLICT);
        }
    }

    // DELETE
    @Operation(summary = "Delete a product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Product deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Product not found",
                    content = @Content(mediaType = "application/json"))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        try {
            productService.deleteProduct(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (IllegalArgumentException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}
