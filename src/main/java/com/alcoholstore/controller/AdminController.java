package com.alcoholstore.controller;

import com.alcoholstore.model.Order;
import com.alcoholstore.model.Product;
import com.alcoholstore.model.Review;
import com.alcoholstore.model.User;
import com.alcoholstore.service.OrderService;
import com.alcoholstore.service.ProductService;
import com.alcoholstore.service.ReviewService;
import com.alcoholstore.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ReviewService reviewService;

    // Проверка на администратора
    private boolean checkAdmin(HttpSession session) {
        Boolean isAdmin = (Boolean) session.getAttribute("isAdmin");
        return isAdmin != null && isAdmin;
    }

    // Главная панель администратора
    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null || !user.isAdmin()) {
            redirectAttributes.addFlashAttribute("error", "Доступ запрещен. Требуются права администратора.");
            return "redirect:/dashboard";
        }

        try {
            // Статистика для дашборда
            long totalUsers = userService.getTotalUsersCount();
            List<Product> allProducts = productService.getAllProducts();
            long totalProducts = allProducts != null ? allProducts.size() : 0;

            List<Order> recentOrders = orderService.getRecentOrders(10);

            // Рассчитываем общую выручку
            BigDecimal totalRevenue = BigDecimal.ZERO;
            if (recentOrders != null) {
                for (Order order : recentOrders) {
                    if (order != null && order.getTotalAmount() != null) {
                        totalRevenue = totalRevenue.add(order.getTotalAmount());
                    }
                }
            }

            model.addAttribute("totalUsers", totalUsers);
            model.addAttribute("totalProducts", totalProducts);
            model.addAttribute("recentOrders", recentOrders);
            model.addAttribute("totalRevenue", totalRevenue);

        } catch (Exception e) {
            // Если есть ошибки, устанавливаем значения по умолчанию
            model.addAttribute("totalUsers", 0);
            model.addAttribute("totalProducts", 0);
            model.addAttribute("recentOrders", List.of());
            model.addAttribute("totalRevenue", BigDecimal.ZERO);
        }

        return "admin/dashboard";
    }

    // Управление товарами
    @GetMapping("/products")
    public String products(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!checkAdmin(session)) {
            redirectAttributes.addFlashAttribute("error", "Доступ запрещен. Требуются права администратора.");
            return "redirect:/dashboard";
        }

        try {
            List<Product> products = productService.getAllProducts();
            model.addAttribute("products", products != null ? products : List.of());
        } catch (Exception e) {
            model.addAttribute("products", List.of());
        }
        return "admin/products";
    }

    // Форма добавления нового товара
    @GetMapping("/products/new")
    public String showAddProductForm(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!checkAdmin(session)) {
            redirectAttributes.addFlashAttribute("error", "Доступ запрещен. Требуются права администратора.");
            return "redirect:/dashboard";
        }

        model.addAttribute("product", new Product());
        model.addAttribute("isEdit", false);
        return "admin/product-form";
    }

    // Форма редактирования товара
    @GetMapping("/products/edit/{id}")
    public String showEditProductForm(@PathVariable Long id, Model model,
                                      HttpSession session, RedirectAttributes redirectAttributes) {
        if (!checkAdmin(session)) {
            redirectAttributes.addFlashAttribute("error", "Доступ запрещен. Требуются права администратора.");
            return "redirect:/dashboard";
        }

        try {
            Product product = productService.getProductById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Товар не найден: " + id));
            model.addAttribute("product", product);
            return "admin/product-edit";
        } catch (Exception e) {
            return "redirect:/admin/products?error=Товар не найден";
        }
    }

    // Сохранение товара (обновление)
    @PostMapping("/products/update/{id}")
    public String updateProduct(@PathVariable Long id,
                                @RequestParam String name,
                                @RequestParam String description,
                                @RequestParam BigDecimal price,
                                @RequestParam(required = false) Double alcoholContent,
                                @RequestParam(required = false) String countryOfOrigin,
                                @RequestParam(required = false) Integer volumeMl,
                                @RequestParam(required = false) Integer inStock,
                                @RequestParam(required = false) String imageUrl,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        if (!checkAdmin(session)) {
            redirectAttributes.addFlashAttribute("error", "Доступ запрещен. Требуются права администратора.");
            return "redirect:/dashboard";
        }

        try {
            Product product = productService.getProductById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Товар не найден"));

            product.setName(name);
            product.setDescription(description);
            product.setPrice(price);
            product.setAlcoholContent(alcoholContent);
            product.setCountryOfOrigin(countryOfOrigin);
            product.setVolumeMl(volumeMl);
            product.setInStock(inStock != null ? inStock : 0);
            product.setImageUrl(imageUrl);

            productService.saveProduct(product);

            redirectAttributes.addFlashAttribute("successMessage", "Товар успешно обновлен!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при обновлении: " + e.getMessage());
        }

        return "redirect:/admin/products";
    }

    // Сохранение товара (создание или обновление)
    @PostMapping("/products/save")
    public String saveProduct(@ModelAttribute Product product,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        if (!checkAdmin(session)) {
            redirectAttributes.addFlashAttribute("error", "Доступ запрещен. Требуются права администратора.");
            return "redirect:/dashboard";
        }

        productService.saveProduct(product);
        redirectAttributes.addFlashAttribute("successMessage",
                product.getId() == null ? "Товар успешно добавлен!" : "Товар успешно обновлен!");
        return "redirect:/admin/products";
    }

    // Удаление товара
    @PostMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable Long id,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        if (!checkAdmin(session)) {
            redirectAttributes.addFlashAttribute("error", "Доступ запрещен. Требуются права администратора.");
            return "redirect:/dashboard";
        }

        try {
            productService.deleteProduct(id);
            redirectAttributes.addFlashAttribute("successMessage", "Товар успешно удален!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при удалении: " + e.getMessage());
        }

        return "redirect:/admin/products";
    }

    // Управление пользователями
    @GetMapping("/users")
    public String users(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!checkAdmin(session)) {
            redirectAttributes.addFlashAttribute("error", "Доступ запрещен. Требуются права администратора.");
            return "redirect:/dashboard";
        }

        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "admin/users";
    }

    // Блокировка/разблокировка пользователя (вместо toggleUserStatus)
    @PostMapping("/users/toggle-admin/{id}")
    public String toggleAdminStatus(@PathVariable Long id,
                                    HttpSession session,
                                    RedirectAttributes redirectAttributes) {
        if (!checkAdmin(session)) {
            redirectAttributes.addFlashAttribute("error", "Доступ запрещен. Требуются права администратора.");
            return "redirect:/dashboard";
        }

        try {
            User user = userService.getUserByIdOrThrow(id);
            user.setIsAdmin(!user.getIsAdmin()); // Используем setIsAdmin
            userService.saveUser(user);

            redirectAttributes.addFlashAttribute("successMessage",
                    user.getIsAdmin() ? "Пользователь стал администратором!" : "Пользователь лишен прав администратора!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка: " + e.getMessage());
        }

        return "redirect:/admin/users";
    }

    // Управление заказами
    @GetMapping("/orders")
    public String orders(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!checkAdmin(session)) {
            redirectAttributes.addFlashAttribute("error", "Доступ запрещен. Требуются права администратора.");
            return "redirect:/dashboard";
        }

        List<Order> orders = orderService.getAllOrders();
        model.addAttribute("orders", orders);
        return "admin/orders";
    }

    // Изменение статуса заказа
    @PostMapping("/orders/update-status/{id}")
    public String updateOrderStatus(@PathVariable Long id,
                                    @RequestParam String status,
                                    HttpSession session,
                                    RedirectAttributes redirectAttributes) {
        if (!checkAdmin(session)) {
            redirectAttributes.addFlashAttribute("error", "Доступ запрещен. Требуются права администратора.");
            return "redirect:/dashboard";
        }

        try {
            orderService.updateOrderStatus(id, status);
            redirectAttributes.addFlashAttribute("successMessage", "Статус заказа обновлен!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка: " + e.getMessage());
        }

        return "redirect:/admin/orders";
    }

    // Просмотр деталей заказа
    @GetMapping("/orders/{id}")
    public String viewOrder(@PathVariable Long id,
                            Model model,
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {
        if (!checkAdmin(session)) {
            redirectAttributes.addFlashAttribute("error", "Доступ запрещен. Требуются права администратора.");
            return "redirect:/dashboard";
        }

        try {
            Order order = orderService.getOrderById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Заказ не найден: " + id));
            model.addAttribute("order", order);
            return "admin/order-details";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Заказ не найден: " + e.getMessage());
            return "redirect:/admin/orders";
        }
    }

    // Управление отзывами
    @GetMapping("/reviews")
    public String reviews(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!checkAdmin(session)) {
            redirectAttributes.addFlashAttribute("error", "Доступ запрещен. Требуются права администратора.");
            return "redirect:/dashboard";
        }

        try {
            List<Review> unapprovedReviews = reviewService.getUnapprovedReviews();
            model.addAttribute("unapprovedReviews", unapprovedReviews);
        } catch (Exception e) {
            model.addAttribute("unapprovedReviews", List.of());
        }
        return "admin/reviews";
    }

    @PostMapping("/reviews/approve/{id}")
    public String approveReview(@PathVariable Long id,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        if (!checkAdmin(session)) {
            redirectAttributes.addFlashAttribute("error", "Доступ запрещен. Требуются права администратора.");
            return "redirect:/dashboard";
        }

        try {
            reviewService.approveReview(id);
            redirectAttributes.addFlashAttribute("successMessage", "Отзыв одобрен");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка: " + e.getMessage());
        }
        return "redirect:/admin/reviews";
    }

    @PostMapping("/reviews/reject/{id}")
    public String rejectReview(@PathVariable Long id,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        if (!checkAdmin(session)) {
            redirectAttributes.addFlashAttribute("error", "Доступ запрещен. Требуются права администратора.");
            return "redirect:/dashboard";
        }

        try {
            reviewService.rejectReview(id);
            redirectAttributes.addFlashAttribute("successMessage", "Отзыв отклонен");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка: " + e.getMessage());
        }
        return "redirect:/admin/reviews";
    }
}