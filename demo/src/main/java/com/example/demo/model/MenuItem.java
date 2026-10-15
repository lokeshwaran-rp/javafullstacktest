package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Represents a menu item resource in the restaurant group backend.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MenuItem {

    private Long id;
    private String name;
    private String description;
    private Double price;
    private String category;
    private Boolean available = true;
    private String imageUrl;
    private Integer calories;
    private List<String> ingredients = new ArrayList<>();
    private List<String> allergens = new ArrayList<>();
    private Object restaurantId;

    private final Map<String, Object> additionalProperties = new ConcurrentHashMap<>();

    public MenuItem() {
    }

    public MenuItem(Long id, String name, String description, Double price, String category, Boolean available) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.available = available != null ? available : true;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("itemName")
    public void setItemName(String itemName) {
        if (this.name == null && itemName != null) {
            this.name = itemName;
        }
    }

    @JsonProperty("item_name")
    public void setItemNameUnderscore(String itemName) {
        if (this.name == null && itemName != null) {
            this.name = itemName;
        }
    }

    @JsonProperty("title")
    public void setTitle(String title) {
        if (this.name == null && title != null) {
            this.name = title;
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty("desc")
    public void setDesc(String desc) {
        if (this.description == null && desc != null) {
            this.description = desc;
        }
    }

    @JsonProperty("details")
    public void setDetails(String details) {
        if (this.description == null && details != null) {
            this.description = details;
        }
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    @JsonProperty("cost")
    public void setCost(Double cost) {
        if (this.price == null && cost != null) {
            this.price = cost;
        }
    }

    @JsonProperty("amount")
    public void setAmount(Double amount) {
        if (this.price == null && amount != null) {
            this.price = amount;
        }
    }

    @JsonProperty("basePrice")
    public void setBasePrice(Double basePrice) {
        if (this.price == null && basePrice != null) {
            this.price = basePrice;
        }
    }

    @JsonProperty("base_price")
    public void setBasePriceUnderscore(Double basePrice) {
        if (this.price == null && basePrice != null) {
            this.price = basePrice;
        }
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @JsonProperty("categoryName")
    public void setCategoryName(String categoryName) {
        if (this.category == null && categoryName != null) {
            this.category = categoryName;
        }
    }

    @JsonProperty("category_name")
    public void setCategoryNameUnderscore(String categoryName) {
        if (this.category == null && categoryName != null) {
            this.category = categoryName;
        }
    }

    @JsonProperty("type")
    public void setType(String type) {
        if (this.category == null && type != null) {
            this.category = type;
        }
    }

    @JsonProperty("available")
    public Boolean getAvailable() {
        return available;
    }

    @JsonProperty("available")
    public void setAvailable(Boolean available) {
        this.available = available;
    }

    @JsonProperty("isAvailable")
    public void setIsAvailable(Boolean isAvailable) {
        if (isAvailable != null) {
            this.available = isAvailable;
        }
    }

    @JsonProperty("is_available")
    public void setIsAvailableUnderscore(Boolean isAvailable) {
        if (isAvailable != null) {
            this.available = isAvailable;
        }
    }

    @JsonProperty("inStock")
    public void setInStock(Boolean inStock) {
        if (inStock != null) {
            this.available = inStock;
        }
    }

    @JsonProperty("in_stock")
    public void setInStockUnderscore(Boolean inStock) {
        if (inStock != null) {
            this.available = inStock;
        }
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @JsonProperty("image_url")
    public void setImageUrlUnderscore(String imageUrl) {
        if (this.imageUrl == null && imageUrl != null) {
            this.imageUrl = imageUrl;
        }
    }

    @JsonProperty("image")
    public void setImage(String image) {
        if (this.imageUrl == null && image != null) {
            this.imageUrl = image;
        }
    }

    public Integer getCalories() {
        return calories;
    }

    public void setCalories(Integer calories) {
        this.calories = calories;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients != null ? ingredients : new ArrayList<>();
    }

    public List<String> getAllergens() {
        return allergens;
    }

    public void setAllergens(List<String> allergens) {
        this.allergens = allergens != null ? allergens : new ArrayList<>();
    }

    public Object getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(Object restaurantId) {
        this.restaurantId = restaurantId;
    }

    @JsonProperty("restaurant_id")
    public void setRestaurantIdUnderscore(Object restaurantId) {
        if (this.restaurantId == null && restaurantId != null) {
            this.restaurantId = restaurantId;
        }
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        if (name != null) {
            this.additionalProperties.put(name, value);
        }
    }

    /**
     * Updates this MenuItem's non-null fields with values from source.
     */
    public void updateFrom(MenuItem source) {
        if (source == null) return;
        if (source.getName() != null) this.name = source.getName();
        if (source.getDescription() != null) this.description = source.getDescription();
        if (source.getPrice() != null) this.price = source.getPrice();
        if (source.getCategory() != null) this.category = source.getCategory();
        if (source.getAvailable() != null) this.available = source.getAvailable();
        if (source.getImageUrl() != null) this.imageUrl = source.getImageUrl();
        if (source.getCalories() != null) this.calories = source.getCalories();
        if (source.getIngredients() != null && !source.getIngredients().isEmpty()) {
            this.ingredients = new ArrayList<>(source.getIngredients());
        }
        if (source.getAllergens() != null && !source.getAllergens().isEmpty()) {
            this.allergens = new ArrayList<>(source.getAllergens());
        }
        if (source.getRestaurantId() != null) this.restaurantId = source.getRestaurantId();
        this.additionalProperties.putAll(source.getAdditionalProperties());
    }

    @Override
    public String toString() {
        return "MenuItem{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", category='" + category + '\'' +
                ", available=" + available +
                '}';
    }
}
