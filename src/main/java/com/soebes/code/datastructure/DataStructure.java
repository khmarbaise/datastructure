package com.soebes.code.datastructure;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
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
    var lock = readWriteLock.writeLock();
    try {
      lock.lock();
      var result = products.putIfAbsent(productData.productId(), productData);
      productData.categories().forEach(pc -> categoryIndex.computeIfAbsent(pc.id(), k -> new HashSet<>()).add(productData.productId()));
      return result;
    } finally {
      lock.unlock();
    }
  }

  public Optional<ProductData> removeProduct(ProductData productData) {
    var lock = readWriteLock.writeLock();
    try {
      lock.lock();
      var removedProduct = Optional.ofNullable(products.remove(productData.productId()));
      removedProduct.ifPresent(tpr -> tpr.categories().forEach(prod -> categoryIndex.remove(prod.id())));
      return removedProduct;
    } finally {
      lock.unlock();
    }
  }

  public Set<ProductId> productIdsByCategory(ProductCategory category) {
    var lock = readWriteLock.readLock();
    try {
      lock.lock();
      return this.categoryIndex.getOrDefault(category.id(), Set.of());
    } finally {
      lock.unlock();
    }
  }

  public Set<ProductData> productsByCategory(ProductCategory category) {
    try {
      readWriteLock.readLock().lock();
      return this.categoryIndex.getOrDefault(category.id(), Set.of()).stream()
          .map(products::get)
          .collect(Collectors.toSet());
    } finally {
      readWriteLock.readLock().unlock();
    }
  }

}
