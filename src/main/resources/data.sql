-- =====================================================
-- MyKitchen Hub - Test Data
-- =====================================================

-- Users
INSERT INTO users (username, email, password, role, created_at, updated_at) VALUES
('admin', 'admin@mykitchen.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'ADMIN', NOW(), NOW()),
('chef_john', 'john.chef@mykitchen.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'USER', NOW(), NOW()),
('cooking_mom', 'mom.cooking@mykitchen.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'USER', NOW(), NOW()),
('food_lover', 'food.lover@mykitchen.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'USER', NOW(), NOW());

-- Recipes
INSERT INTO recipes (title, description, image_url, tag, created_by, created_at, updated_at) VALUES
('Spaghetti Carbonara', 'Classic Italian pasta dish with eggs, cheese, pancetta, and black pepper. A simple yet delicious recipe that comes together in minutes.', 'https://res.cloudinary.com/my-kitchen-hub/image/upload/v1756816189/11973-spaghetti-carbonara-ii-DDMFS-4x3-6edea51e421e4457ac0c3269f3be5157_kmwegc.jpg', 'Italian, Pasta, Quick', 2, NOW(), NOW()),
('Chicken Tikka Masala', 'Creamy and flavorful Indian curry with tender chicken pieces in a rich tomato-based sauce. Served with basmati rice and naan bread.', 'https://res.cloudinary.com/my-kitchen-hub/image/upload/v1756838719/chicken-tikka-masala-for-the-grill-recipe-hero-2_1-cb493f49e30140efbffec162d5f2d1d7_wfq164.jpg', 'Indian, Curry, Chicken', 2, NOW(), NOW()),
('Chocolate Chip Cookies', 'Soft and chewy homemade chocolate chip cookies with a perfect golden brown exterior. A classic dessert that everyone loves.', 'https://res.cloudinary.com/my-kitchen-hub/image/upload/v1756838782/BAKERY-STYLE-CHOCOLATE-CHIP-COOKIES-9-637x637-1_abgiyb.jpg', 'Dessert, Baking, Chocolate', 3, NOW(), NOW()),
('Caesar Salad', 'Fresh romaine lettuce with Caesar dressing, croutons, and parmesan cheese. A light and refreshing salad perfect for any meal.', 'https://res.cloudinary.com/my-kitchen-hub/image/upload/v1756838862/caesar-salad_qmmrvv.jpg', 'Salad, Healthy, Vegetarian', 4, NOW(), NOW()),
('Beef Stir Fry', 'Quick and easy stir-fried beef with colorful vegetables in a savory sauce. Perfect for busy weeknights.', 'https://res.cloudinary.com/my-kitchen-hub/image/upload/v1756838918/Easy-Beef-Stir-Fry-S2_mvwpqt.jpg', 'Asian, Quick, Beef', 2, NOW(), NOW());

-- Ingredients for Spaghetti Carbonara
INSERT INTO ingredients (name, amount, unit, recipe_id) VALUES
('Spaghetti', 400.0, 'g', 1),
('Eggs', 4.0, 'pieces', 1),
('Pancetta', 150.0, 'g', 1),
('Parmesan Cheese', 100.0, 'g', 1),
('Black Pepper', 2.0, 'tsp', 1),
('Salt', 1.0, 'tsp', 1);

-- Ingredients for Chicken Tikka Masala
INSERT INTO ingredients (name, amount, unit, recipe_id) VALUES
('Chicken Breast', 600.0, 'g', 2),
('Yogurt', 200.0, 'ml', 2),
('Tomato Sauce', 400.0, 'ml', 2),
('Heavy Cream', 200.0, 'ml', 2),
('Garam Masala', 2.0, 'tsp', 2),
('Ginger', 2.0, 'tbsp', 2),
('Garlic', 4.0, 'cloves', 2),
('Onion', 2.0, 'pieces', 2);

-- Ingredients for Chocolate Chip Cookies
INSERT INTO ingredients (name, amount, unit, recipe_id) VALUES
('All-Purpose Flour', 250.0, 'g', 3),
('Butter', 200.0, 'g', 3),
('Sugar', 150.0, 'g', 3),
('Brown Sugar', 150.0, 'g', 3),
('Eggs', 2.0, 'pieces', 3),
('Vanilla Extract', 2.0, 'tsp', 3),
('Chocolate Chips', 300.0, 'g', 3),
('Baking Soda', 1.0, 'tsp', 3);

-- Ingredients for Caesar Salad
INSERT INTO ingredients (name, amount, unit, recipe_id) VALUES
('Romaine Lettuce', 2.0, 'heads', 4),
('Parmesan Cheese', 100.0, 'g', 4),
('Croutons', 100.0, 'g', 4),
('Lemon Juice', 3.0, 'tbsp', 4),
('Olive Oil', 4.0, 'tbsp', 4),
('Garlic', 2.0, 'cloves', 4),
('Anchovy Paste', 1.0, 'tsp', 4),
('Dijon Mustard', 1.0, 'tsp', 4);

-- Ingredients for Beef Stir Fry
INSERT INTO ingredients (name, amount, unit, recipe_id) VALUES
('Beef Sirloin', 500.0, 'g', 5),
('Broccoli', 300.0, 'g', 5),
('Bell Peppers', 2.0, 'pieces', 5),
('Carrots', 200.0, 'g', 5),
('Soy Sauce', 4.0, 'tbsp', 5),
('Ginger', 1.0, 'tbsp', 5),
('Garlic', 3.0, 'cloves', 5),
('Vegetable Oil', 3.0, 'tbsp', 5);

-- Shopping Lists
INSERT INTO shopping_lists (name, generated_by, generated_from_recipe, created_at, updated_at) VALUES
('Weekly Groceries', 2, NULL, NOW(), NOW()),
('Carbonara Ingredients', 2, 'Spaghetti Carbonara', NOW(), NOW()),
('Baking Supplies', 3, 'Chocolate Chip Cookies', NOW(), NOW()),
('Healthy Eating', 4, 'Caesar Salad', NOW(), NOW());

-- Shopping List Items
INSERT INTO list_items (name, amount, unit, is_checked, shopping_list_id) VALUES
-- Weekly Groceries
('Milk', 2.0, 'L', false, 1),
('Bread', 2.0, 'loaves', false, 1),
('Eggs', 12.0, 'pieces', false, 1),
('Bananas', 1.0, 'kg', false, 1),
('Chicken Breast', 1.0, 'kg', false, 1),

-- Carbonara Ingredients
('Spaghetti', 400.0, 'g', false, 2),
('Pancetta', 150.0, 'g', false, 2),
('Parmesan Cheese', 100.0, 'g', false, 2),
('Eggs', 4.0, 'pieces', false, 2),

-- Baking Supplies
('All-Purpose Flour', 500.0, 'g', false, 3),
('Butter', 250.0, 'g', false, 3),
('Chocolate Chips', 300.0, 'g', false, 3),
('Vanilla Extract', 1.0, 'bottle', false, 3),

-- Healthy Eating
('Romaine Lettuce', 2.0, 'heads', false, 4),
('Cherry Tomatoes', 200.0, 'g', false, 4),
('Cucumber', 1.0, 'piece', false, 4),
('Olive Oil', 1.0, 'bottle', false, 4);

-- =====================================================
-- Notes:
-- 1. Password for all users is 'password' (bcrypt encoded)
-- 2. Admin user: admin@mykitchen.com (username: admin)
-- 3. Regular users: chef_john, cooking_mom, food_lover
-- 4. All timestamps are set to current time
-- 5. Recipe IDs start from 1, User IDs start from 1
-- =====================================================
