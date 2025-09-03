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

-- Comments for Spaghetti Carbonara (Recipe ID: 1)
INSERT INTO comments (text, recipe_id, user_id, created_at, updated_at) VALUES
('Amazing recipe! Turned out delicious, just like in Italy!', 1, 3, '2024-08-30 14:30:00', '2024-08-30 14:30:00'),
('Classic! I always cook exactly according to this recipe. Thank you!', 1, 4, '2024-08-31 09:15:00', '2024-08-31 09:15:00'),
('Tried it for the first time - absolutely amazing! Will definitely cook again.', 1, 2, '2024-09-02 18:45:00', '2024-09-02 18:45:00');

-- Comments for Chicken Tikka Masala (Recipe ID: 2)
INSERT INTO comments (text, recipe_id, user_id, created_at, updated_at) VALUES
('Incredibly aromatic dish! The spices are perfectly balanced.', 2, 4, '2024-08-31 16:20:00', '2024-08-31 16:20:00'),
('My husband is delighted! I''ve been cooking it for the third time in a row.', 2, 3, '2024-09-01 12:10:00', '2024-09-01 12:10:00'),
('Great recipe for getting acquainted with Indian cuisine. Highly recommend!', 2, 1, '2024-09-02 20:30:00', '2024-09-02 20:30:00');

-- Comments for Chocolate Chip Cookies (Recipe ID: 3)
INSERT INTO comments (text, recipe_id, user_id, created_at, updated_at) VALUES
('The kids are absolutely thrilled! The cookies turned out soft and aromatic.', 3, 2, '2024-08-31 11:45:00', '2024-08-31 11:45:00'),
('Perfect recipe for beginners! Everything worked out on the first try.', 3, 4, '2024-09-01 15:30:00', '2024-09-01 15:30:00'),
('The best chocolate chip cookies I''ve ever made!', 3, 1, '2024-09-02 19:15:00', '2024-09-02 19:15:00');

-- Comments for Caesar Salad (Recipe ID: 4)
INSERT INTO comments (text, recipe_id, user_id, created_at, updated_at) VALUES
('Fresh and light salad! Perfect for healthy eating.', 4, 2, '2024-08-30 13:00:00', '2024-08-30 13:00:00'),
('Classic Caesar! The dressing turned out absolutely perfect.', 4, 3, '2024-09-01 17:45:00', '2024-09-01 17:45:00'),
('Great recipe for a summer lunch. Very refreshing!', 4, 1, '2024-09-02 21:00:00', '2024-09-02 21:00:00');

-- Comments for Beef Stir Fry (Recipe ID: 5)
INSERT INTO comments (text, recipe_id, user_id, created_at, updated_at) VALUES
('Quick, tasty and healthy! Perfect for weekdays.', 5, 3, '2024-08-31 19:30:00', '2024-08-31 19:30:00'),
('The meat turned out very tender, and the vegetables are crispy. Super!', 5, 4, '2024-09-01 14:20:00', '2024-09-01 14:20:00'),
('Simple recipe, but the result exceeded all expectations!', 5, 2, '2024-09-02 16:50:00', '2024-09-02 16:50:00');

-- =====================================================
-- Notes:
-- 1. Password for all users is 'password'
-- 2. Admin user: admin@mykitchen.com (username: admin)
-- 3. Regular users: chef_john, cooking_mom, food_lover
-- =====================================================
