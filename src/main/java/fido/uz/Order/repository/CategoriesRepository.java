package fido.uz.Order.repository;

import fido.uz.Order.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoriesRepository extends JpaRepository<Category, Long> {

    Category findByName(String name);
}
