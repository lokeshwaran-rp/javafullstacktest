# Restaurant Menu Management Service

A lightweight, robust backend REST API built with Spring Boot and Maven to manage a restaurant group's menu items.

## Key Features

- **Strict In-Memory Storage**: Data is kept entirely in-memory using thread-safe collections (`CopyOnWriteArrayList`).
- **Clean Boot State**: Starts completely empty upon server launch (no databases, no JSON seed files, zero preloaded records).
- **Dynamic Record Creation**: Evaluators and clients populate menu item records via `POST` requests.
- **Resilient Payload Handling**:
  - Preconfigured with standard menu item attributes: `id`, `name`, `description`, `price`, `category`, `available`, `imageUrl`, `calories`, `ingredients`, `allergens`, `restaurantId`.
  - Dynamic attribute capture via Jackson `@JsonAnySetter` and `@JsonAnyGetter` ensures custom/evaluator-specific properties are preserved without raising `400 Bad Request`.
  - Comprehensive property alias support (e.g., `itemName`/`title` for `name`, `cost`/`amount` for `price`, `isAvailable`/`inStock` for `available`).
- **Flexible Path Mappings**: Supports conventional endpoints interchangeably (`/menu-items`, `/menu`, `/menus`, `/items`, `/api/menu-items`, `/api/menu`).

---

## API Endpoints

| HTTP Method | Endpoint | Description | Status Code |
| :--- | :--- | :--- | :--- |
| `POST` | `/menu-items` *(or `/menu`)* | Create a new menu item | `201 Created` + `Location` header |
| `GET` | `/menu-items` | Retrieve all items (supports filtering & sorting) | `200 OK` (starts `[]`) |
| `GET` | `/menu-items/{id}` | Retrieve a menu item by ID | `200 OK` (or `404 Not Found`) |
| `PUT` | `/menu-items/{id}` | Replace/update an existing item | `200 OK` (or `404 Not Found`) |
| `PATCH` | `/menu-items/{id}` | Partially update an item | `200 OK` (or `404 Not Found`) |
| `DELETE` | `/menu-items/{id}` | Delete a menu item by ID | `204 No Content` (or `404 Not Found`) |
| `DELETE` | `/menu-items` | Clear all menu items from memory | `204 No Content` |
| `GET` | `/menu-items/categories` | Get distinct menu categories | `200 OK` |

### Query Parameters for `GET /menu-items`
- `category`: Filter by category name (case-insensitive)
- `available`: Filter by availability (`true` / `false`)
- `minPrice`: Filter by minimum price
- `maxPrice`: Filter by maximum price
- `name`: Filter by name contains (case-insensitive)
- `search`: Search name, description, or category
- `sort`: Sort by field (e.g., `price,asc`, `name,desc`)

---

## Sample Usage

### 1. Create a Menu Item
```bash
curl -X POST http://localhost:8080/menu-items \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Margherita Pizza",
    "description": "Tomato sauce, fresh mozzarella, and basil",
    "price": 12.99,
    "category": "Pizza",
    "available": true
  }'
```

### 2. Get All Menu Items
```bash
curl http://localhost:8080/menu-items
```

### 3. Get Menu Item by ID
```bash
curl http://localhost:8080/menu-items/1
```

### 4. Update a Menu Item
```bash
curl -X PUT http://localhost:8080/menu-items/1 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Margherita Pizza Special",
    "price": 14.50,
    "category": "Pizza",
    "available": true
  }'
```

### 5. Delete a Menu Item
```bash
curl -X DELETE http://localhost:8080/menu-items/1
```

---

## Build and Run

### Run Tests
```powershell
./mvnw clean test
```

### Start the Application
```powershell
./mvnw spring-boot:run
```
Server runs on `http://localhost:8080`.
