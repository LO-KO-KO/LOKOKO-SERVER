package com.lokoko.domain.product.repository;

import com.lokoko.domain.product.entity.Product;
import com.lokoko.domain.product.entity.ProductOption;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductOptionRepository extends JpaRepository<ProductOption, Long> {
    List<ProductOption> findByProduct(Product product);

    @Query("select p.optionName from ProductOption p where p.product = :product")
    List<String> findOptionNamesByProduct(Product product);
}
