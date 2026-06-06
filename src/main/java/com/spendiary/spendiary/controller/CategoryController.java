package com.spendiary.spendiary.controller;

import com.spendiary.spendiary.dto.CategoryRequest;
import com.spendiary.spendiary.entity.Category;
import com.spendiary.spendiary.entity.TransactionType;
import com.spendiary.spendiary.entity.User;
import com.spendiary.spendiary.service.CategoryService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/category")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/manage")
    public String manageForm(HttpSession session, Model model){
        User loginUser = (User) session.getAttribute("loginUser");

        List<Category> incomeCategoryList = categoryService.getCategoriesByType(loginUser, TransactionType.INCOME);
        List<Category> expenseCategoryList = categoryService.getCategoriesByType(loginUser, TransactionType.EXPENSE);

        model.addAttribute("incomeCategoryList", incomeCategoryList);
        model.addAttribute("expenseCategoryList", expenseCategoryList);

        return "category_form";
    }

    @PostMapping("/create")
    public String create(CategoryRequest request, HttpSession session, RedirectAttributes redirectAttributes){
        User loginUser = (User) session.getAttribute("loginUser");
        try {
            categoryService.createCustomCategory(loginUser, request);
        } catch (IllegalArgumentException e){
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/category/manage";
    }

    @PostMapping("/update/{categoryId}")
    public String update(@PathVariable("categoryId") Long categoryId, CategoryRequest request, HttpSession session, RedirectAttributes redirectAttributes){
        User loginUser = (User) session.getAttribute("loginUser");
        try {
            categoryService.updateCategoryName(categoryId, request, loginUser);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/category/manage";
    }

    @PostMapping("/delete/{categoryId}")
    public String delete(@PathVariable("categoryId") Long categoryId, HttpSession session){
        User loginUser = (User) session.getAttribute("loginUser");
        categoryService.deleteCategory(categoryId, loginUser);
        return "redirect:/category/manage";
    }
}
