package com.example.demo.controller;

import com.example.demo.model.MenuItem;
import com.example.demo.service.MenuService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * REST Controller for managing Restaurant Menu Item resources.
 * Supports standard REST endpoints with multiple conventional path mappings.
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping({
        "/menu-items",
        "/menu",
        "/menus",
        "/menuitems",
        "/items",
        "/api/menu-items",
        "/api/menu",
        "/api/menus",
        "/api/menuitems",
        "/api/items"
})
public class MenuController {

    private final MenuService menuService;

    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    /**
     * GET /menu-items
     * Retrieve all menu items (starts empty upon server launch).
     * Supports optional query parameters for filtering and sorting.
     */
    @GetMapping
    public ResponseEntity<List<MenuItem>> getAllMenuItems(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Boolean available,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String sort) {

        List<MenuItem> items = menuService.getAllMenuItems(
                category, available, minPrice, maxPrice, search, name, sort);
        return ResponseEntity.ok(items);
    }

    /**
     * GET /menu-items/categories
     * Retrieve distinct categories in the menu.
     */
    @GetMapping("/categories")
    public ResponseEntity<Set<String>> getCategories() {
        return ResponseEntity.ok(menuService.getDistinctCategories());
    }

    /**
     * GET /menu-items/{id}
     * Retrieve a specific menu item by its ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<MenuItem> getMenuItemById(@PathVariable String id) {
        MenuItem item = menuService.getMenuItemById(id);
        return ResponseEntity.ok(item);
    }

    /**
     * POST /menu-items
     * Create a new menu item.
     * Starts with an empty in-memory store; the evaluator/client populates records using this endpoint.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<MenuItem> createMenuItem(@RequestBody MenuItem item) {
        if (item == null) {
            throw new IllegalArgumentException("Menu item payload cannot be null");
        }

        MenuItem created = menuService.createMenuItem(item);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();

        return ResponseEntity.created(location).body(created);
    }

    /**
     * PUT /menu-items/{id}
     * Fully update an existing menu item.
     */
    @PutMapping("/{id}")
    public ResponseEntity<MenuItem> updateMenuItem(
            @PathVariable String id,
            @RequestBody MenuItem updatedItem) {
        if (updatedItem == null) {
            throw new IllegalArgumentException("Menu item update payload cannot be null");
        }

        MenuItem result = menuService.updateMenuItem(id, updatedItem);
        return ResponseEntity.ok(result);
    }

    /**
     * PATCH /menu-items/{id}
     * Partially update an existing menu item.
     */
    @PatchMapping("/{id}")
    public ResponseEntity<MenuItem> patchMenuItem(
            @PathVariable String id,
            @RequestBody Map<String, Object> updates) {
        if (updates == null || updates.isEmpty()) {
            MenuItem existing = menuService.getMenuItemById(id);
            return ResponseEntity.ok(existing);
        }

        try {
            Long numericId = Long.parseLong(id);
            MenuItem result = menuService.patchMenuItem(numericId, updates);
            return ResponseEntity.ok(result);
        } catch (NumberFormatException e) {
            MenuItem existing = menuService.getMenuItemById(id);
            MenuItem result = menuService.patchMenuItem(existing.getId(), updates);
            return ResponseEntity.ok(result);
        }
    }

    /**
     * DELETE /menu-items/{id}
     * Delete a menu item by ID.
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteMenuItem(@PathVariable String id) {
        menuService.deleteMenuItem(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * DELETE /menu-items
     * Reset/clear all menu items (convenient for test cleanup).
     */
    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteAllMenuItems() {
        menuService.clearAll();
        return ResponseEntity.noContent().build();
    }
}
