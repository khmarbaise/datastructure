package com.soebes.code.datastructure;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

class DataStructuresThreadIT {

  private List<ProductData> products;
  private List<ProductCategory> productCategorieslist;

  @BeforeEach
  void beforeEach() {
    this.productCategorieslist = LongStream.rangeClosed(1, 80_000)
        .mapToObj(cnt -> new ProductCategory(new ProductCategoryId(cnt), "pc%d".formatted(cnt)))
        .toList();
    this.products = IntStream.rangeClosed(1, 40_000)
        .mapToObj(cnt -> {
          var productId = new ProductId(cnt);
          var categories = productCategorieslist.subList(cnt, 2 * cnt);
          return new ProductData(productId, "ProductName%d".formatted(cnt), categories);
        }).toList();

  }

  @Test
  void testingProductsAdding() throws InterruptedException {
    var dataStructure = new DataStructure();
    Thread t1 = Thread.ofVirtual().start(() -> {
      for (int i = 0; i < 2_000; i++) {
        dataStructure.addProduct(products.get(i));
        dataStructure.removeProduct(products.get(i));
      }
    });
    Thread t2 = Thread.ofVirtual().start(() -> {
      for (int i = 0; i < 2_000; i++) {
        dataStructure.addProduct(products.get(i + 2_000));
        dataStructure.removeProduct(products.get(i));
      }
    });
    Thread t3 = Thread.ofVirtual().start(() -> {
      for (int i = 0; i < 2_000; i++) {
        dataStructure.addProduct(products.get(i + 4_000));
        dataStructure.removeProduct(products.get(i));
      }
    });
    Thread t4 = Thread.ofVirtual().start(() -> {
      for (int i = 0; i < 2_000; i++) {
        dataStructure.addProduct(products.get(i + 6_000));
        dataStructure.removeProduct(products.get(i));
      }
    });

//    t1.start();
//    t2.start();
//    t3.start();
//    t4.start();
    t1.join();
    t2.join();
    t3.join();
    t4.join();
  }
}
