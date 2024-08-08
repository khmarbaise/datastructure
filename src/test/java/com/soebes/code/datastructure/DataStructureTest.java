package com.soebes.code.datastructure;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Karl Heinz Marbaise
 */
class DataStructureTest {

  private DataStructure dataStructure;
  private List<ProductCategory> productCategorieslist;
  private List<ProductData> products;

  @BeforeEach
  void beforeEach() {
    this.dataStructure = new DataStructure();
    this.productCategorieslist = LongStream.rangeClosed(1, 100)
        .mapToObj(cnt -> new ProductCategory(new ProductCategoryId(cnt), "pc%d".formatted(cnt)))
        .toList();
    this.products = IntStream.rangeClosed(1, 30)
        .mapToObj(cnt -> {
          var productId = new ProductId(cnt);
          var categories = productCategorieslist.subList(cnt, 2 * cnt);
          return new ProductData(productId, "ProductName%d".formatted(cnt), categories);
        }).toList();
  }

  @Test
  void name1() {
    var p1 = products.getFirst();
    dataStructure.addProduct(p1);
    assertThat(dataStructure.getProducts()).containsExactly(Map.entry(p1.productId(), p1));
    print();
  }

  @Test
  void index1() {
    var p1 = products.getFirst();
    dataStructure.addProduct(p1);
    assertThat(dataStructure.productIdsByCategory(p1.categories().get(0))).containsExactly(p1.productId());
    print();
  }

  @Test
  void name2() {
    var p1 = products.get(0);
    var p2 = products.get(1);
    dataStructure.addProduct(p1);
    dataStructure.addProduct(p2);
    assertThat(dataStructure.getProducts()).containsExactly(Map.entry(p1.productId(), p1), Map.entry(p2.productId(), p2));
    print();
  }

  @Test
  void name3() {
    var p1 = products.get(0);
    var p2 = products.get(1);
    var p3 = products.get(2);
    dataStructure.addProduct(p1);
    dataStructure.addProduct(p2);
    dataStructure.addProduct(p3);
    assertThat(dataStructure.getProducts()).containsExactly(Map.entry(p1.productId(), p1), Map.entry(p2.productId(), p2), Map.entry(p3.productId(), p3));
    print();
  }

  @Test
  void removeProductMustUpdateCategoryIndex() {
    var p1 = products.get(0);
    var p2 = products.get(1);
    var p3 = products.get(2);
    dataStructure.addProduct(p1);
    dataStructure.addProduct(p2);
    dataStructure.addProduct(p3);

    assertThat(dataStructure.removeProduct(p3)).isEqualTo(p3);

    assertThat(dataStructure.getProducts()).containsExactly(Map.entry(p1.productId(), p1), Map.entry(p2.productId(), p2));

    var pc2 = new ProductCategoryId(2);
    var pc3 = new ProductCategoryId(3);
    var pId1 = new ProductId(1);
    var pId2 = new ProductId(2);
    var pcE2 = Map.entry(pc2, Set.of(pId1));
    var pcE3 = Map.entry(pc3, Set.of(pId2));
    assertThat(dataStructure.getCategoryIndex()).containsExactly(pcE2, pcE3);
    print();
  }

  void print() {
    System.out.println("--- Products ----");
    dataStructure.getProducts().forEach((key, value) -> {
      System.out.println("Key: " + key + " ProductName:" + value.name());
      value.categories().forEach(c -> System.out.println("  " + c));
    });

    System.out.println("--- Index ----");
    dataStructure.getCategoryIndex().forEach((key, value) -> {
      System.out.println("Key: " + key);
      value.forEach(c -> System.out.println(" " + c));
    });
  }
}