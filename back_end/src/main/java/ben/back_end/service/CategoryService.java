package ben.back_end.service;

import ben.back_end.entity.Categories;
import ben.back_end.entity.vo.CategoryListElementVO;
import ben.back_end.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    public List<CategoryListElementVO>  getAllCategories() {
        List<Categories> categoriesList =  categoryRepository.findAll();
        return categoriesList.stream()
                .map(c -> new CategoryListElementVO(
                        c.getCatId(),
                        c.getCatName(),
                        c.getDescription()
                )).toList();
    }
}
