package com.soebes.code.datastructure;

import java.util.List;

/**
 * @author Karl Heinz Marbaise
 */
public record ProductData(ProductId productId, String name, List<ProductCategory> categories) {
}
