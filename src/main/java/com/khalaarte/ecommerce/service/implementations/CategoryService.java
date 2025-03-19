package com.khalaarte.ecommerce.service.implementations;

import com.khalaarte.ecommerce.model.Category;
import com.khalaarte.ecommerce.repository.ICategoryRepository;
import com.khalaarte.ecommerce.service.interfaces.ICategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService implements ICategoryService {

    @Autowired
    private ICategoryRepository categoryRepository;

    @Override
    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    @Override
    public Optional<Category> findByName(String name) {
        return categoryRepository.findByName(name);
    }


    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }

    @Override
    public Category updateCategory(Category category) {
        if (category.getId() == null) {
            throw new IllegalArgumentException("Category ID cannot be null for update");
        }
        Category existingCategory = categoryRepository.findById(category.getId())
                .orElseThrow(() -> new RuntimeException("Category not found."));

        existingCategory.setName(category.getName());
        return categoryRepository.save(existingCategory);
    }

    @Override
    public void deleteCategoryById(Long id) {
        categoryRepository.deleteById(id);
    }
}
