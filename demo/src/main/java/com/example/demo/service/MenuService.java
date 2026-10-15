package com.example.demo.service;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.MenuItem;
import com.example.demo.repository.InMemoryMenuRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Service providing business logic for restaurant menu management.
 */
@Service
public class MenuService {

    private final InMemoryMenuRepository repository;

    public MenuService(InMemoryMenuRepository repository) {
        this.repository = repository;
    }

    /**
     * Get all menu items with optional filtering and sorting.
     */
    public List<MenuItem> getAllMenuItems(
            String category,
            Boolean available,
            Double minPrice,
            Double maxPrice,
            String search,
            String name,
            String sort) {

        Stream<MenuItem> stream = repository.findAll().stream();

        // Filter by category
        if (category != null && !category.trim().isEmpty()) {
            stream = stream.filter(item -> item.getCategory() != null &&
                    item.getCategory().equalsIgnoreCase(category.trim()));
        }

        // Filter by availability
        if (available != null) {
            stream = stream.filter(item -> Objects.equals(item.getAvailable(), available));
        }

        // Filter by minPrice
        if (minPrice != null) {
            stream = stream.filter(item -> item.getPrice() != null && item.getPrice() >= minPrice);
        }

        // Filter by maxPrice
        if (maxPrice != null) {
            stream = stream.filter(item -> item.getPrice() != null && item.getPrice() <= maxPrice);
        }

        // Filter by exact or partial name
        if (name != null && !name.trim().isEmpty()) {
            String lowerName = name.trim().toLowerCase();
            stream = stream.filter(item -> item.getName() != null &&
                    item.getName().toLowerCase().contains(lowerName));
        }

        // Search in name or description
        if (search != null && !search.trim().isEmpty()) {
            String query = search.trim().toLowerCase();
            stream = stream.filter(item ->
                    (item.getName() != null && item.getName().toLowerCase().contains(query)) ||
                    (item.getDescription() != null && item.getDescription().toLowerCase().contains(query)) ||
                    (item.getCategory() != null && item.getCategory().toLowerCase().contains(query))
            );
        }

        // Sorting
        if (sort != null && !sort.trim().isEmpty()) {
            String[] parts = sort.split(",");
            String field = parts[0].trim().toLowerCase();
            boolean ascending = parts.length < 2 || !parts[1].trim().equalsIgnoreCase("desc");

            Comparator<MenuItem> comparator = switch (field) {
                case "price", "cost" -> Comparator.comparing(
                        MenuItem::getPrice, Comparator.nullsLast(Comparator.naturalOrder()));
                case "name", "title" -> Comparator.comparing(
                        MenuItem::getName, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));
                case "category" -> Comparator.comparing(
                        MenuItem::getCategory, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));
                default -> Comparator.comparing(
                        MenuItem::getId, Comparator.nullsLast(Comparator.naturalOrder()));
            };

            if (!ascending) {
                comparator = comparator.reversed();
            }
            stream = stream.sorted(comparator);
        }

        return stream.collect(Collectors.toList());
    }

    /**
     * Retrieve a menu item by ID.
     */
    public MenuItem getMenuItemById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MenuItem", id));
    }

    /**
     * Retrieve a menu item by string identifier.
     */
    public MenuItem getMenuItemById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MenuItem", id));
    }

    /**
     * Create a new menu item.
     */
    public MenuItem createMenuItem(MenuItem item) {
        if (item == null) {
            throw new IllegalArgumentException("Menu item body cannot be null or empty");
        }
        return repository.save(item);
    }

    /**
     * Fully update an existing menu item.
     */
    public MenuItem updateMenuItem(Long id, MenuItem updatedItem) {
        if (updatedItem == null) {
            throw new IllegalArgumentException("Updated menu item cannot be null");
        }
        MenuItem existing = getMenuItemById(id);
        updatedItem.setId(id);
        return repository.save(updatedItem);
    }

    /**
     * Fully update an existing menu item using string ID.
     */
    public MenuItem updateMenuItem(String id, MenuItem updatedItem) {
        if (updatedItem == null) {
            throw new IllegalArgumentException("Updated menu item cannot be null");
        }
        MenuItem existing = getMenuItemById(id);
        try {
            Long numericId = Long.parseLong(id);
            updatedItem.setId(numericId);
        } catch (NumberFormatException ignored) {
            // Keep existing id
            updatedItem.setId(existing.getId());
        }
        return repository.save(updatedItem);
    }

    /**
     * Partially update an existing menu item.
     */
    public MenuItem patchMenuItem(Long id, MenuItem partialItem) {
        MenuItem existing = getMenuItemById(id);
        existing.updateFrom(partialItem);
        return repository.save(existing);
    }

    /**
     * Partially update an existing menu item via Map.
     */
    public MenuItem patchMenuItem(Long id, Map<String, Object> updates) {
        MenuItem existing = getMenuItemById(id);
        if (updates == null || updates.isEmpty()) {
            return existing;
        }

        updates.forEach((key, value) -> {
            switch (key.toLowerCase()) {
                case "name", "itemname", "item_name", "title" -> {
                    if (value != null) existing.setName(value.toString());
                }
                case "description", "desc", "details" -> {
                    if (value != null) existing.setDescription(value.toString());
                }
                case "price", "cost", "amount" -> {
                    if (value instanceof Number n) {
                        existing.setPrice(n.doubleValue());
                    } else if (value != null) {
                        existing.setPrice(Double.parseDouble(value.toString()));
                    }
                }
                case "category", "type" -> {
                    if (value != null) existing.setCategory(value.toString());
                }
                case "available", "isavailable", "is_available", "instock" -> {
                    if (value instanceof Boolean b) {
                        existing.setAvailable(b);
                    } else if (value != null) {
                        existing.setAvailable(Boolean.parseBoolean(value.toString()));
                    }
                    existing.getAdditionalProperties().remove("available");
                    existing.getAdditionalProperties().remove("isAvailable");
                    existing.getAdditionalProperties().remove("is_available");
                    existing.getAdditionalProperties().remove("inStock");
                    existing.getAdditionalProperties().remove("in_stock");
                }
                case "imageurl", "image_url", "image" -> {
                    if (value != null) existing.setImageUrl(value.toString());
                }
                case "calories" -> {
                    if (value instanceof Number n) {
                        existing.setCalories(n.intValue());
                    } else if (value != null) {
                        existing.setCalories(Integer.parseInt(value.toString()));
                    }
                }
                case "id" -> {
                    // Ignore ID change on patch
                }
                default -> existing.setAdditionalProperty(key, value);
            }
        });

        return repository.save(existing);
    }

    /**
     * Delete a menu item by numeric ID.
     */
    public void deleteMenuItem(Long id) {
        boolean removed = repository.deleteById(id);
        if (!removed) {
            throw new ResourceNotFoundException("MenuItem", id);
        }
    }

    /**
     * Delete a menu item by string ID.
     */
    public void deleteMenuItem(String id) {
        boolean removed = repository.deleteById(id);
        if (!removed) {
            throw new ResourceNotFoundException("MenuItem", id);
        }
    }

    /**
     * Reset/clear all menu items.
     */
    public void clearAll() {
        repository.deleteAll();
    }

    /**
     * Get distinct categories across all menu items.
     */
    public Set<String> getDistinctCategories() {
        return repository.findAll().stream()
                .map(MenuItem::getCategory)
                .filter(c -> c != null && !c.trim().isEmpty())
                .collect(Collectors.toCollection(TreeSet::new));
    }
}
