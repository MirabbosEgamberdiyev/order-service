package fido.uz.Order.service;

import fido.uz.Order.dto.CategoryDto;
import fido.uz.Order.dto.CategoryResponseDto;
import fido.uz.Order.entity.Bot;
import fido.uz.Order.entity.Category;
import fido.uz.Order.entity.Product;
import fido.uz.Order.entity.User;
import fido.uz.Order.repository.BotsRepository;
import fido.uz.Order.repository.CategoriesRepository;
import fido.uz.Order.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoriesService {

    private final CategoriesRepository categoriesRepository;
    private final UserRepository userRepository;
    private final BotsRepository botsRepository;

    @Autowired
    public CategoriesService(CategoriesRepository categoriesRepository, UserRepository userRepository, BotsRepository botsRepository) {
        this.categoriesRepository = categoriesRepository;
        this.userRepository = userRepository;
        this.botsRepository = botsRepository;
    }

    // Helper Method: Fetch User
    private User fetchUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with ID " + userId + " not found"));
    }

    // Helper Method: Fetch Bot
    private Bot fetchBotByToken(String botToken) {
        return botsRepository.findByBotToken(botToken);
    }

    // Create Category
    @Transactional
    public Category createCategory(CategoryDto categoryDto) {
        User user = fetchUserById(categoryDto.getUserId());
        Bot bot = fetchBotByToken(categoryDto.getBotToken());

        if (bot == null) {
            throw new EntityNotFoundException("Bot with token " + categoryDto.getBotToken() + " not found");
        }

        if (categoriesRepository.findByName(categoryDto.getName()) != null) {
            throw new IllegalArgumentException("Category with name " + categoryDto.getName() + " already exists");
        }

        Category categoryEntity = new Category();
        categoryEntity.setName(categoryDto.getName());
        categoryEntity.setBotToken(categoryDto.getBotToken());
        categoryEntity.setUserId(user.getId());

        return categoriesRepository.save(categoryEntity);
    }


    public CategoryResponseDto getCategoryById(Long categoryId) {
        Optional<Category> category = categoriesRepository.findById(categoryId);
        CategoryResponseDto categoryResponseDto = new CategoryResponseDto();
        categoryResponseDto.setId(category.get().getId());
        categoryResponseDto.setName(category.get().getName());
        categoryResponseDto.setBotToken(category.get().getBotToken());
        Hibernate.initialize(category.get().getProducts()); // Initialize products
        categoryResponseDto.setProductNames(category.get().getProducts().stream().map(Product::getName).collect(Collectors.toList()));
        categoryResponseDto.setUserId(fetchUserById(category.get().getUserId()).getId());
        return categoryResponseDto;
    }


    public List<CategoryResponseDto> getAllCategories() {
        List<Category> categories = categoriesRepository.findAll();
        List<CategoryResponseDto> categoryResponseDtos = new ArrayList<>();

        for (Category category : categories) {
            // Initialize the products collection
            Hibernate.initialize(category.getProducts());

            // Convert to DTO
            CategoryResponseDto categoryResponseDto = new CategoryResponseDto();
            categoryResponseDto.setId(category.getId());
            categoryResponseDto.setName(category.getName());
            categoryResponseDto.setBotToken(category.getBotToken());
            categoryResponseDto.setProductNames(category.getProducts().stream().map(Product::getName).collect(Collectors.toList()));
            categoryResponseDto.setUserId(fetchUserById(category.getUserId()).getId());

            categoryResponseDtos.add(categoryResponseDto);
        }

        return categoryResponseDtos;
    }
    private CategoryResponseDto convertToDto(Category category) {
        CategoryResponseDto categoryDto = new CategoryResponseDto();
        categoryDto.setId(category.getId());
        categoryDto.setName(category.getName());
        categoryDto.setBotToken(category.getBotToken());
        categoryDto.setUserId(category.getUserId());
        // Convert product entities to a list of product names
        categoryDto.setProductNames(category.getProducts().stream()
                .map(Product::getName)
                .collect(Collectors.toList()));

        return categoryDto;
    }

    // Update Category
    @Transactional
    public Category updateCategory(Long categoryId, CategoryDto categoryDto) {
        CategoryResponseDto categoryResponseDto = getCategoryById(categoryId);
        Category category = categoriesRepository.findById(categoryId).get();
        if (category == null) {
            throw new EntityNotFoundException("Category with ID " + categoryId + " not found");
        }

        User user = fetchUserById(categoryDto.getUserId());
        Bot bot = fetchBotByToken(categoryDto.getBotToken());

        if (bot == null) {
            throw new EntityNotFoundException("Bot with token " + categoryDto.getBotToken() + " not found");
        }

        Category existingCategory = categoriesRepository.findByName(categoryDto.getName());
        if (existingCategory != null && !existingCategory.getId().equals(categoryId)) {
            throw new IllegalArgumentException("Category with name " + categoryDto.getName() + " already exists");
        }

        category.setName(categoryDto.getName());
        category.setBotToken(categoryDto.getBotToken());
        category.setUserId(user.getId());

        return categoriesRepository.save(category);
    }

    // Delete Category
    @Transactional
    public void deleteCategory(Long categoryId) {
        CategoryResponseDto category = getCategoryById(categoryId);
        if (category == null) {
            throw new EntityNotFoundException("Category with ID " + categoryId + " not found");
        }
        categoriesRepository.deleteById(categoryId);
    }
}
