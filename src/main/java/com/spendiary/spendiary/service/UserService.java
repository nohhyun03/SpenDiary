package com.spendiary.spendiary.service;

import com.spendiary.spendiary.dto.LoginRequest;
import com.spendiary.spendiary.dto.SignupRequest;
import com.spendiary.spendiary.entity.User;
import com.spendiary.spendiary.repository.CategoryRepository;
import com.spendiary.spendiary.repository.TransactionRepository;
import com.spendiary.spendiary.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final CategoryService categoryService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User join(SignupRequest request) {
        validateDuplicateLoginId(request.getLoginId());
        validatePassword(request);

        User user = User.builder()
                .loginId(request.getLoginId())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .build();

        User savedUser = userRepository.save(user);

        categoryService.createDefaultCategory(savedUser);

        return savedUser;
    }

    public User login(LoginRequest request) {
        User user = userRepository.findByLoginId(request.getLoginId())
                .orElseThrow(() ->
                        new IllegalArgumentException("아이디 또는 비밀번호가 올바르지 않습니다.")
                );

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("아이디 또는 비밀번호가 올바르지 않습니다.");
        }
        return user;
    }

    @Transactional
    public void delete(String loginId) {
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow();

        transactionRepository.deleteAllByAuthor(user);

        categoryRepository.deleteAllByAuthor(user);

        userRepository.delete(user);
    }

    private void validatePassword(SignupRequest request) {
        if (!request.getPassword().equals(request.getPasswordConfirm())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
    }

    private void validateDuplicateLoginId(String loginId) {
        if (userRepository.existsByLoginId(loginId)) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }
    }

    public boolean isLoginIdDuplicated(String loginId) {
        return userRepository.existsByLoginId(loginId);
    }
}
