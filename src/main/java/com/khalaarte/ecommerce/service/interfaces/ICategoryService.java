package com.khalaarte.ecommerce.service.interfaces;

import com.khalaarte.ecommerce.model.Category;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface ICategoryService {
    Optional<Category> getCategoryById(Long id);
    Optional<Category> findByName(String name);
    List<Category> getAllCategories();

    Category createCategory(Category category);
    Category updateCategory(Category category);

    void deleteCategoryById(Long id);
}
