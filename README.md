# Dawaa - eCommerce REST API

Dawaa is an eCommerce REST API built with Spring Boot. It provides endpoints for managing products, users, and shopping
carts in an online pharmacy.

## Table of Contents

- [Features](#features)
- [Technologies](#technologies)
- [Getting Started](#getting-started)
- [API Endpoints](#api-endpoints)
- [Authentication](#authentication)
- [Error Handling](#error-handling)
- [Contributing](#contributing)
- [License](#license)

## Features

- CRUD operations for products
- User management (registration, authentication, profile update)
- Shopping cart functionality (add to cart, remove from cart)
- Product filtering by category and price range

## Technologies

- Java
- Spring Boot
- Spring Web
- Spring Data JPA
- Spring Security
- MySQL
- Maven
- JWT (JSON Web Tokens)

## Getting Started

1. Clone the repository:

   ```shell
   git clone https://github.com/your-username/dawaa.git
   ```
2. Navigate to the project directory:

   ```shell
   cd dawaa
   ```
3. Configure the database connection by updating the application.properties file with your MySQL database credentials:
   'application.yaml'
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/dawaa
   spring.datasource.username=root
   spring.datasource.password=password
   ```
4. Build the project using Maven:

   ```shell
    mvn spring-boot:run
   ```

The application will be accessible at http://localhost:8080.

## API Endpoints

- POST /register/customer: Register a new user as a customer.

- POST /register/admin: Register a new user as an admin.

- POST /login: Authenticate a user and obtain an access token.

- GET /products: Get a list of all products.

- GET /products/search: Search for a specific product by part of its name or the description.

- POST /products/create: Create a new product.

- PUT /products/update: Update an existing product.

- DELETE /products/delete: Delete a product.

- GET /products/by-category: Filter Products by categories.

- GET /cart/products: Get the current user's shopping cart.

- POST /cart/add: Add a product to the shopping cart.

- DELETE /cart/delete: Delete a product from the shopping cart.

Please refer to the API documentation or code for the complete list of endpoints and their usage.

## Authentication

Dawaa uses token-based authentication. When a user logs in successfully, a JWT (JSON Web Token) is generated and
returned in the response.
This token should be included in the **`Authorization`** header for subsequent requests that require authentication.

 ```css
   Authorization: Bearer {token}
```

## Error Handling
Dawaa handles errors and exceptions using proper HTTP status codes and JSON error responses. Each error response contains a message describing the error for better understanding and troubleshooting.

## Contributing
Contributions to Dawaa are welcome! If you find any issues or want to add new features, please submit a pull request. Make sure to follow the existing coding style and include appropriate tests.
1. Fork the project
2. Create your feature branch  (**`git checkout -b feature/your-feature`**)
3. Commit your changes  (**`git commit -m 'Add some feature'`**)
4. Push to the branch  (**`git push origin feature/your-feature`**)
5. Open a pull request

## License
This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.