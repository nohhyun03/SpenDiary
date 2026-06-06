package com.spendiary.spendiary.controller;

import com.spendiary.spendiary.dto.CalendarDay;
import com.spendiary.spendiary.dto.StatisticsResponse;
import com.spendiary.spendiary.entity.Transaction;
import com.spendiary.spendiary.entity.TransactionType;
import com.spendiary.spendiary.entity.User;
import com.spendiary.spendiary.service.StatisticsService;
import com.spendiary.spendiary.service.TransactionService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final TransactionService transactionService;
    private final StatisticsService statisticsService;

    @GetMapping("/")
    public String home(HttpSession session, Model model, @RequestParam(value = "date", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate targetDate){
        User loginUser = (User) session.getAttribute("loginUser");
        LocalDate selectedDate = (targetDate != null) ? targetDate : LocalDate.now();

        int year = selectedDate.getYear();
        int month = selectedDate.getMonthValue();

        StatisticsResponse stats = statisticsService.getMonthlyStatistics(loginUser, year, month);
        model.addAttribute("stats", stats);

        List<Transaction> transactions = transactionService.getTransactionsByDate(loginUser, selectedDate);
        model.addAttribute("transactions", transactions);
        model.addAttribute("selectedDate", selectedDate);

        YearMonth ym = YearMonth.of(year, month);
        LocalDate firstDay = ym.atDay(1);
        int startDayOfWeek = firstDay.getDayOfWeek().getValue();
        int offset = (startDayOfWeek == 7) ? 0 : startDayOfWeek;
        LocalDate calendarStartDate = firstDay.minusDays(offset);
        LocalDate calendarEndDate = calendarStartDate.plusDays(41);

        List<Transaction> calendarTxs = transactionService.getTransactionsByPeriod(loginUser, calendarStartDate, calendarEndDate);

        List<CalendarDay> calendarList = generateCalendar(year, month, selectedDate, calendarTxs);
        model.addAttribute("calendarList", calendarList);

        model.addAttribute("prevMonth", selectedDate.minusMonths(1).withDayOfMonth(1));
        model.addAttribute("nextMonth", selectedDate.plusMonths(1).withDayOfMonth(1));
        session.setAttribute("returnTarget", "home");
        return "home";
    }

    private List<CalendarDay> generateCalendar(int year, int month, LocalDate selectedDate, List<Transaction> calendarTxs) {
        List<CalendarDay> days = new ArrayList<>();
        YearMonth ym = YearMonth.of(year, month);
        LocalDate firstDay = ym.atDay(1);

        int startDayOfWeek = firstDay.getDayOfWeek().getValue();
        int offset = (startDayOfWeek == 7) ? 0 : startDayOfWeek;

        LocalDate current = firstDay.minusDays(offset);

        for (int i = 0; i < 42; i++) {
            int dailyIncome = 0;
            int dailyExpense = 0;

            for (Transaction tx : calendarTxs) {
                if (tx.getTransactionDate().equals(current)) {
                    if (tx.getType() == TransactionType.INCOME) {
                        dailyIncome += tx.getAmount();
                    } else {
                        dailyExpense += tx.getAmount();
                    }
                }
            }

            days.add(CalendarDay.builder()
                    .day(current.getDayOfMonth())
                    .date(current)
                    .isCurrentMonth(current.getMonthValue() == month)
                    .hasData(dailyIncome > 0 || dailyExpense > 0)
                    .isSelected(current.equals(selectedDate))
                    .incomeAmount(dailyIncome)
                    .expenseAmount(dailyExpense)
                    .build());

            current = current.plusDays(1);
        }
        return days;
    }
}
