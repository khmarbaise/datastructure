package com.soebes.code.datastructure;

import java.util.List;

public record ProductData(ProductId productId, String name, List<ProductCategory> categories) {
}
