package fido.uz.Order.service;

import fido.uz.Order.dto.CategoryDto;
import fido.uz.Order.entity.Bot;
import fido.uz.Order.entity.Category;
import fido.uz.Order.entity.User;
import fido.uz.Order.repository.BotsRepository;
import fido.uz.Order.repository.CategoriesRepository;
import fido.uz.Order.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

    // Create
    public Category createCategory(CategoryDto category) {
        User user = userRepository.findById(category.getUserId()).orElseThrow(() -> new IllegalArgumentException("User not found"));
        Bot bot = botsRepository.findByBotToken(category.getBotToken());
        if (bot == null) {
            throw new IllegalArgumentException("Bot not found");
        }
        Category exist = categoriesRepository.findByName(category.getName());
        if (exist != null) {
            throw new IllegalArgumentException("Category already exists");
        }
        Category categoryEntity = new Category();
        categoryEntity.setName(category.getName());
        categoryEntity.setBotToken(category.getBotToken());
        categoryEntity.setUserId(user.getId());
        return categoriesRepository.save(categoryEntity);
    }

    // Read - Get Category by ID
    public Category getCategoryById(Long categoryId) {
        return categoriesRepository.findById(categoryId).orElseThrow(() -> new IllegalArgumentException("Category not found"));
    }

    // Read - Get All Categories
    public List<Category> getAllCategories() {
        return categoriesRepository.findAll();
    }

    // Update
    public Category updateCategory(Long categoryId, CategoryDto categoryDto) {
        Category category = categoriesRepository.findById(categoryId).orElseThrow(() -> new IllegalArgumentException("Category not found"));

        User user = userRepository.findById(categoryDto.getUserId()).orElseThrow(() -> new IllegalArgumentException("User not found"));
        Bot bot = botsRepository.findByBotToken(categoryDto.getBotToken());
        if (bot == null) {
            throw new IllegalArgumentException("Bot not found");
        }

        Category existingCategory = categoriesRepository.findByName(categoryDto.getName());
        if (existingCategory != null && !existingCategory.getId().equals(categoryId)) {
            throw new IllegalArgumentException("Category name already exists");
        }

        category.setName(categoryDto.getName());
        category.setBotToken(categoryDto.getBotToken());
        category.setUserId(user.getId());
        return categoriesRepository.save(category);
    }

    // Delete
    public void deleteCategory(Long categoryId) {
        Category category = categoriesRepository.findById(categoryId).orElseThrow(() -> new IllegalArgumentException("Category not found"));
        categoriesRepository.delete(category);
    }
}
