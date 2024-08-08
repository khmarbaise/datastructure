package com.soebes.code.datastructure;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

/**
 * @author Karl Heinz Marbaise
 */
public class DataStructure {
  private final Map<ProductId, ProductData> products;
  private final Map<ProductCategoryId, Set<ProductId>> categoryIndex;
  private final ReadWriteLock readWriteLock;

  public DataStructure() {
    this.products = new ConcurrentHashMap<>();
    this.categoryIndex = new ConcurrentHashMap<>();
    this.readWriteLock = new ReentrantReadWriteLock();
  }

  public Map<ProductId, ProductData> getProducts() {
    return products;
  }

  public Map<ProductCategoryId, Set<ProductId>> getCategoryIndex() {
    return categoryIndex;
  }

  public ProductData addProduct(ProductData productData) {
    readWriteLock.writeLock().lock();
    try {
      var result = products.putIfAbsent(productData.productId(), productData);
      productData.categories().forEach(pc -> categoryIndex.computeIfAbsent(pc.id(), k -> new HashSet<>()).add(productData.productId()));
      return result;
    } finally {
      readWriteLock.writeLock().unlock();
    }
  }

  public ProductData removeProduct(ProductData productData) {
    readWriteLock.writeLock().lock();
    try {
      var removedProduct = products.remove(productData.productId());
      removedProduct.categories().forEach(prod -> categoryIndex.remove(prod.id()));
      return removedProduct;
    } finally {
      readWriteLock.writeLock().unlock();
    }
  }

  public Set<ProductId> productIdsByCategory(ProductCategory category) {
    readWriteLock.readLock().lock();
    try {
      return this.categoryIndex.getOrDefault(category.id(), Set.of());
    } finally {
      readWriteLock.readLock().unlock();
    }
  }

  public Set<ProductData> productsByCategory(ProductCategory category) {
    readWriteLock.readLock().lock();
    try {
      return this.categoryIndex.getOrDefault(category.id(), Set.of()).stream()
          .map(products::get)
          .collect(Collectors.toSet());
    } finally {
      readWriteLock.readLock().unlock();
    }
  }

}
