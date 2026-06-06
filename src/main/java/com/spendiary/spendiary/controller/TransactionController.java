package com.spendiary.spendiary.controller;

import com.spendiary.spendiary.dto.TransactionRequest;
import com.spendiary.spendiary.entity.Category;
import com.spendiary.spendiary.entity.Transaction;
import com.spendiary.spendiary.entity.TransactionType;
import com.spendiary.spendiary.entity.User;
import com.spendiary.spendiary.service.CategoryService;
import com.spendiary.spendiary.service.TransactionService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/transaction")
public class TransactionController {

    private final TransactionService transactionService;
    private final CategoryService categoryService;

    @GetMapping("/create")
    public String createForm(HttpSession session, Model model, @RequestParam(value = "date", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        User loginUser = (User) session.getAttribute("loginUser");
        List<Category> incomeCategoryList = categoryService.getCategoriesByType(loginUser, TransactionType.INCOME);
        List<Category> expenseCategoryList = categoryService.getCategoriesByType(loginUser, TransactionType.EXPENSE);

        model.addAttribute("incomeCategoryList", incomeCategoryList);
        model.addAttribute("expenseCategoryList", expenseCategoryList);

        LocalDate selectedDate = (date != null) ? date : LocalDate.now();
        model.addAttribute("selectedDate", selectedDate);
        return "transaction_form";
    }

    @PostMapping("/create")
    public String create(TransactionRequest request, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        transactionService.createTransaction(loginUser, request);
        return "redirect:/?date=" + request.getTransactionDate();
    }

    @GetMapping("/update/{transactionId}")
    public String updateForm(@PathVariable("transactionId") Long transactionId, HttpSession session, Model model){
        User loginUser = (User) session.getAttribute("loginUser");
        Transaction transaction = transactionService.getTransactionById(transactionId);

        transactionService.validateTransactionOwnership(transaction, loginUser);

        List<Category> incomeCategoryList = categoryService.getCategoriesByType(loginUser, TransactionType.INCOME);
        List<Category> expenseCategoryList = categoryService.getCategoriesByType(loginUser, TransactionType.EXPENSE);

        model.addAttribute("transaction", transaction); //기존 데이터 전달
        model.addAttribute("incomeCategoryList", incomeCategoryList);
        model.addAttribute("expenseCategoryList", expenseCategoryList);

        return "transaction_form";
    }

    @PostMapping("/update/{transactionId}")
    public String update(@PathVariable("transactionId") Long transactionId, TransactionRequest request, HttpSession session){
        User loginUser = (User) session.getAttribute("loginUser");

        transactionService.updateTransaction(transactionId, loginUser, request);

        String returnTarget = (String) session.getAttribute("returnTarget");

        if ("statistics".equals(returnTarget)) {
            return "redirect:/statistics";
        } else if ("search".equals(returnTarget)) {
            return "redirect:/search";
        } else {
            return "redirect:/";
        }
    }

    @PostMapping("/delete/{transactionId}")
    public String delete(@PathVariable("transactionId") Long transactionId, HttpSession session){
        User loginUser = (User) session.getAttribute("loginUser");
        transactionService.deleteTransaction(transactionId, loginUser);

        String returnTarget = (String) session.getAttribute("returnTarget");

        if ("statistics".equals(returnTarget)) {
            return "redirect:/statistics";
        } else if ("search".equals(returnTarget)) {
            return "redirect:/search";
        } else {
            return "redirect:/";
        }
    }
}
