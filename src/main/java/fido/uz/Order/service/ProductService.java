package fido.uz.Order.service;

import fido.uz.Order.dto.ProductDto;
import fido.uz.Order.entity.Bot;
import fido.uz.Order.entity.Category;
import fido.uz.Order.entity.Product;
import fido.uz.Order.entity.User;
import fido.uz.Order.repository.BotsRepository;
import fido.uz.Order.repository.CategoriesRepository;
import fido.uz.Order.repository.ProductsRepository;
import fido.uz.Order.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

    // Get Product by ID
    public Product getProductById(Long id) {
        return productsRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
    }

    // Get All Products
    public List<Product> getAllProducts() {
        return productsRepository.findAll();
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
