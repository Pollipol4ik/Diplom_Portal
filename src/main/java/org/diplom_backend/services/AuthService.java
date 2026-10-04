package org.diplom_backend.services;


import lombok.RequiredArgsConstructor;
import org.diplom_backend.exceptions.AccountAlreadyExistException;
import org.diplom_backend.exceptions.BannedAccountException;
import org.diplom_backend.exceptions.LoginFailException;
import org.diplom_backend.model.Account;
import org.diplom_backend.model.Role;
import org.diplom_backend.repositories.AccountRepository;
import org.diplom_backend.security.JwtService;
import org.springframework.data.util.Pair;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AccountRepository accountRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;

    /**
     * Регулярное выражение требований к паролю — идентично проверкам на фронтенде (RegisterPage / EditProfilePage):
     * - минимум 8 символов
     * - хотя бы одна заглавная латинская или кириллическая буква
     * - хотя бы одна строчная латинская или кириллическая буква
     * - хотя бы одна цифра
     */
    public static final java.util.regex.Pattern PASSWORD_PATTERN =
            java.util.regex.Pattern.compile(
                    "^(?=.*[A-ZА-ЯЁ])(?=.*[a-zа-яё])(?=.*\\d).{8,}$"
            );

    /** Проверяет пароль по общим требованиям безопасности. */
    public static void validatePasswordStrength(String password) {
        if (password == null || !PASSWORD_PATTERN.matcher(password).matches()) {
            throw new IllegalArgumentException(
                    "Пароль должен содержать не менее 8 символов, включать хотя бы одну заглавную букву, одну строчную и одну цифру"
            );
        }
    }

    @Transactional
    public Pair<Account, String> login(String email, String password) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );

            Account account = accountRepository.findByEmail(email).orElseThrow(LoginFailException::new);

            if (Boolean.TRUE.equals(account.getIsBanned())) {
                throw new BannedAccountException();
            }
            return Pair.of(account, jwtService.generateToken(account));
        } catch (AuthenticationException e) {
            throw new LoginFailException();
        }
    }

    @Transactional
    public Pair<Account, String> register(String email, String nickname, String password) throws AccountAlreadyExistException {
        validatePasswordStrength(password);
        if (accountRepository.findByEmail(email).isPresent()) {
            throw new AccountAlreadyExistException("почтой", email);
        }
        if (accountRepository.findByNickname(nickname).isPresent()) {
            throw new AccountAlreadyExistException("ником", nickname);
        }
        var account = new Account();
        account.setEmail(email);
        account.setNickname(nickname);
        account.setPassword(passwordEncoder.encode(password));
        account.setRole(roleService.getRoleByName(Role.ROLE_USER));
        account.setIsBanned(false);
        account.setIsLagging(false);
        account.setCreatedAt(java.time.LocalDateTime.now());
        return Pair.of(account, jwtService.generateToken(accountRepository.save(account)));
    }

    @Transactional
    public void registerAdmin(Account account) throws AccountAlreadyExistException {
        if (accountRepository.findByEmail(account.getEmail()).isEmpty()) {
            account.setPassword(passwordEncoder.encode(account.getPassword()));
            accountRepository.save(account);
        }
    }
}