package ben.back_end.repository;

import ben.back_end.entity.Categories;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Categories, Integer> {
}
