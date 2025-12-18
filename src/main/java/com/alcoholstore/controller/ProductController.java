package com.alcoholstore.controller;

import com.alcoholstore.model.Product;
import com.alcoholstore.service.CartService;
import com.alcoholstore.service.CatalogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private CatalogService catalogService;

    @Autowired
    private CartService cartService;

    // Страница деталей товара
    @GetMapping("/{id}")
    public String productDetail(@PathVariable Long id,
                                @AuthenticationPrincipal UserDetails userDetails,
                                Model model) {
        Optional<Product> productOpt = catalogService.getProductById(id);

        if (productOpt.isEmpty()) {
            return "redirect:/catalog";
        }

        Product product = productOpt.get();
        model.addAttribute("product", product);

        // Количество товаров в корзине
        if (userDetails != null) {
            int cartItemsCount = cartService.getCartItemCount(userDetails.getUsername());
            model.addAttribute("cartItemsCount", cartItemsCount);
        } else {
            model.addAttribute("cartItemsCount", 0);
        }

        return "product-detail";
    }

    // Быстрое добавление в корзину
    @PostMapping("/{id}/add-to-cart")
    public String addToCart(@PathVariable Long id,
                            @RequestParam(defaultValue = "1") Integer quantity,
                            @AuthenticationPrincipal UserDetails userDetails,
                            RedirectAttributes redirectAttributes) {

        if (userDetails == null) {
            redirectAttributes.addFlashAttribute("error", "Для добавления в корзину необходимо войти в систему");
            return "redirect:/login";
        }

        try {
            cartService.addToCart(userDetails.getUsername(), id, quantity);
            redirectAttributes.addFlashAttribute("success", "Товар добавлен в корзину");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при добавлении в корзину: " + e.getMessage());
        }

        return "redirect:/catalog";
    }
}