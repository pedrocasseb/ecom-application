package com.app.ecom.service;

import com.app.ecom.dto.ProductRequest;
import com.app.ecom.dto.ProductResponse;
import com.app.ecom.model.Product;
import com.app.ecom.respository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    public ProductResponse createProduct(ProductRequest productRequest) {
        Product product = new Product();
        updateProductsFromRequest(product, productRequest);
        product.setActive(true);
        Product savedProduct = productRepository.save(product);
        return mapToProductResponse(savedProduct);
    }

    public ProductResponse updateProduct(Long id, ProductRequest productRequest) {
        Product product = productRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
        updateProductsFromRequest(product, productRequest);
        productRepository.save(product);
        return mapToProductResponse(product);
    }

    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::mapToProductResponse)
                .collect(Collectors.toList());
    }

    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
        if(product.getActive() == false) {
            productRepository.deleteById(id);
        } else {
            product.setActive(false);
            productRepository.save(product);
        }
    }

    public List<ProductResponse> searchProducts(String keyword) {
        return productRepository.searchProducts(keyword).stream()
                .map(this::mapToProductResponse)
                .collect(Collectors.toList());
    }

    private ProductResponse mapToProductResponse(Product product) {
        ProductResponse productResponse = new ProductResponse();
        productResponse.setId(product.getId());
        productResponse.setName(product.getName());
        productResponse.setDescription(product.getDescription());
        productResponse.setPrice(product.getPrice());
        productResponse.setStockQuantity(product.getStockQuantity());
        productResponse.setCategory(product.getCategory());
        productResponse.setImageUrl(product.getImageUrl());
        productResponse.setActive(product.getActive());

        return productResponse;
    }

    private void updateProductsFromRequest(Product product, ProductRequest productRequest) {
        if(productRequest.getName() != null) product.setName(productRequest.getName());
        if(productRequest.getDescription() != null)product.setDescription(productRequest.getDescription());
        if(productRequest.getPrice() != null) product.setPrice(productRequest.getPrice());
        if(productRequest.getStockQuantity() != null) product.setStockQuantity(productRequest.getStockQuantity());
        if(productRequest.getCategory() != null) product.setCategory(productRequest.getCategory());
        if(productRequest.getImageUrl() != null) product.setImageUrl(productRequest.getImageUrl());
    }
}
