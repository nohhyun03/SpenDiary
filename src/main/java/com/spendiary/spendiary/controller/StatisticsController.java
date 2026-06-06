package com.spendiary.spendiary.controller;

import com.spendiary.spendiary.dto.StatisticsResponse;
import com.spendiary.spendiary.entity.Transaction;
import com.spendiary.spendiary.entity.User;
import com.spendiary.spendiary.service.StatisticsService;
import com.spendiary.spendiary.service.TransactionService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;
    private final TransactionService transactionService;

    @GetMapping("/statistics")
    public String statisticsForm(
            @RequestParam(value = "startDate", required = false) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) LocalDate endDate,
            HttpSession session, Model model) {

        User loginUser = (User) session.getAttribute("loginUser");

        if (startDate == null || endDate == null) {
            YearMonth currentMonth = YearMonth.now();
            startDate = currentMonth.atDay(1);
            endDate = currentMonth.atEndOfMonth();
        }

        try {
            StatisticsResponse stats = statisticsService.getStatisticsByPeriod(loginUser, startDate, endDate);
            model.addAttribute("stats", stats);

            List<Transaction> transactions = transactionService.getTransactionsByPeriod(loginUser, startDate, endDate);
            model.addAttribute("transactions", transactions);

        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());

            model.addAttribute("stats", StatisticsResponse.builder()
                    .totalIncome(0)
                    .totalExpense(0)
                    .totalBalance(0)
                    .build());
        }

        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);

        session.setAttribute("returnTarget", "statistics");
        return "statistics";
    }
}
