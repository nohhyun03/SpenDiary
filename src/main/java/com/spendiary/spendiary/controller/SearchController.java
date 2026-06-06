package com.spendiary.spendiary.controller;

import com.spendiary.spendiary.entity.Category;
import com.spendiary.spendiary.entity.Transaction;
import com.spendiary.spendiary.entity.TransactionType;
import com.spendiary.spendiary.entity.User;
import com.spendiary.spendiary.service.CategoryService;
import com.spendiary.spendiary.service.TransactionService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class SearchController {

    private final CategoryService categoryService;
    private final TransactionService transactionService;

    @GetMapping("/search")
    public String searchForm(@RequestParam(value = "categoryId", required = false) Long categoryId,
                             @RequestParam(value = "keyword", required = false) String keyword,
                             HttpSession session, Model model) {
        User loginUser = (User) session.getAttribute("loginUser");

        List<Category> expenseCategories = categoryService.getCategoriesByType(loginUser, TransactionType.EXPENSE);
        List<Category> incomeCategories = categoryService.getCategoriesByType(loginUser, TransactionType.INCOME);

        model.addAttribute("expenseCategories", expenseCategories);
        model.addAttribute("incomeCategories", incomeCategories);

        List<Transaction> searchResults = transactionService.searchTransactions(loginUser, categoryId, keyword);
        model.addAttribute("searchResults", searchResults);
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedCategoryId", categoryId);

        session.setAttribute("returnTarget", "search");
        return "search";
    }
}
