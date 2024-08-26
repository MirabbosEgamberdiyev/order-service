package fido.uz.Order.service;

import fido.uz.Order.dto.ProductDto;
import fido.uz.Order.dto.ProductResponseDto;
import fido.uz.Order.entity.Bot;
import fido.uz.Order.entity.Category;
import fido.uz.Order.entity.Product;
import fido.uz.Order.entity.User;
import fido.uz.Order.repository.BotsRepository;
import fido.uz.Order.repository.CategoriesRepository;
import fido.uz.Order.repository.ProductsRepository;
import fido.uz.Order.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductsRepository productsRepository;
    private final CategoriesRepository categoriesRepository;
    private final BotsRepository botsRepository;
    private final UserRepository userRepository;

    @Autowired
    public ProductService(ProductsRepository productsRepository, CategoriesRepository categoriesRepository, BotsRepository botsRepository, UserRepository userRepository) {
        this.productsRepository = productsRepository;
        this.categoriesRepository = categoriesRepository;
        this.botsRepository = botsRepository;
        this.userRepository = userRepository;
    }
    private User fetchUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with ID " + userId + " not found"));
    }


    // Create
    public Product createProduct(ProductDto productDto) {
        Category category = categoriesRepository.findById(productDto.getCategory_id())
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        Bot bot = botsRepository.findByBotToken(productDto.getBotToken());
        if (bot == null) {
            throw new IllegalArgumentException("Bot not found");
        }
        User user = fetchUserById(productDto.getUserId());


        Product product = new Product();
        product.setName(productDto.getName());
        product.setDescription(productDto.getDescription());
        product.setPrice(productDto.getPrice());
        product.setImageUrl(productDto.getImageUrl());
        product.setCategory(category);
        product.setBotToken(productDto.getBotToken());
        product.setUserId(productDto.getUserId());

        return productsRepository.save(product);
    }

    @Transactional
    public ProductResponseDto getProductById(Long id) {
        Product product = productsRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id " + id));
        return convertToDto(product);
    }


    @Transactional()
    public List<ProductResponseDto> getAllProducts() {
        List<Product> products = productsRepository.findAll();
        return products.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private ProductResponseDto convertToDto(Product product) {
        ProductResponseDto dto = new ProductResponseDto();
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setImageUrl(product.getImageUrl());
        dto.setBotToken(product.getBotToken());
        dto.setUserId(product.getUserId());

        // Fetch category name to avoid lazy initialization issues
        dto.setCategoryName(product.getCategory() != null ? product.getCategory().getName() : "No category");

        return dto;
    }



    // Update
    public Product updateProduct(Long id, ProductDto productDto) {
        Product product = productsRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        Category category = categoriesRepository.findById(productDto.getCategory_id())
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        Bot bot = botsRepository.findByBotToken(productDto.getBotToken());
        if (bot == null) {
            throw new IllegalArgumentException("Bot not found");
        }

        product.setName(productDto.getName());
        product.setDescription(productDto.getDescription());
        product.setPrice(productDto.getPrice());
        product.setImageUrl(productDto.getImageUrl());
        product.setCategory(category);
        product.setBotToken(productDto.getBotToken());
        product.setUserId(productDto.getUserId());

        return productsRepository.save(product);
    }

    // Delete
    public void deleteProduct(Long id) {
        Product product = productsRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        productsRepository.delete(product);
    }
}
