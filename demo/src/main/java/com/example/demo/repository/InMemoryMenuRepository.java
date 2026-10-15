package com.example.demo.repository;

import com.example.demo.model.MenuItem;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

/**
 * In-memory repository keeping restaurant menu items in a thread-safe List.
 * Starts completely empty with no seed data, databases, or JSON files.
 */
@Repository
public class InMemoryMenuRepository {

    // In-memory list storage (starts empty)
    private final List<MenuItem> menuItems = new CopyOnWriteArrayList<>();
    private final AtomicLong idSequence = new AtomicLong(0);

    /**
     * Retrieve all menu items currently in memory.
     */
    public List<MenuItem> findAll() {
        return Collections.unmodifiableList(new ArrayList<>(menuItems));
    }

    /**
     * Find menu item by Long ID.
     */
    public Optional<MenuItem> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return menuItems.stream()
                .filter(item -> Objects.equals(item.getId(), id))
                .findFirst();
    }

    /**
     * Find menu item by string identifier (handles numeric or string representations).
     */
    public Optional<MenuItem> findById(String id) {
        if (id == null) {
            return Optional.empty();
        }
        try {
            Long numericId = Long.parseLong(id);
            return findById(numericId);
        } catch (NumberFormatException e) {
            return menuItems.stream()
                    .filter(item -> item.getId() != null && Objects.equals(item.getId().toString(), id))
                    .findFirst();
        }
    }

    /**
     * Save or update a menu item in memory.
     */
    public MenuItem save(MenuItem item) {
        if (item == null) {
            throw new IllegalArgumentException("Menu item cannot be null");
        }

        if (item.getId() == null) {
            item.setId(idSequence.incrementAndGet());
        } else {
            // Keep the atomic sequence ahead of any explicitly supplied ID
            idSequence.accumulateAndGet(item.getId(), Math::max);
        }

        // Check if an item with this ID already exists
        for (int i = 0; i < menuItems.size(); i++) {
            MenuItem existing = menuItems.get(i);
            if (Objects.equals(existing.getId(), item.getId())) {
                menuItems.set(i, item);
                return item;
            }
        }

        menuItems.add(item);
        return item;
    }

    /**
     * Delete a menu item by ID.
     * @return true if an item was removed, false otherwise
     */
    public boolean deleteById(Long id) {
        if (id == null) {
            return false;
        }
        return menuItems.removeIf(item -> Objects.equals(item.getId(), id));
    }

    /**
     * Delete a menu item by string ID.
     */
    public boolean deleteById(String id) {
        if (id == null) {
            return false;
        }
        try {
            Long numericId = Long.parseLong(id);
            return deleteById(numericId);
        } catch (NumberFormatException e) {
            return menuItems.removeIf(item -> item.getId() != null && Objects.equals(item.getId().toString(), id));
        }
    }

    /**
     * Clear all menu items from in-memory storage.
     */
    public void deleteAll() {
        menuItems.clear();
        idSequence.set(0);
    }

    /**
     * Returns count of items currently in memory.
     */
    public long count() {
        return menuItems.size();
    }

    /**
     * Checks if item exists by ID.
     */
    public boolean existsById(Long id) {
        return findById(id).isPresent();
    }
}
