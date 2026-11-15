# Security Starter Toolkit (`com.walter.toolkit:security-starter`)

A reusable, zero-boilerplate Spring Boot 4 starter providing stateless JWT authentication, brute-force rate limiting, AOP audit logging, custom password validation, and standardized JSON exception handling.

## Features

* **Stateless JWT Security:** HS256 token generation and validation (`JwtService`), request filter (`JwtAuthenticationFilter`), and login controller (`POST /api/auth/login`).
* **Brute-Force Protection:** Thread-safe, in-memory IP rate limiting (`RateLimitFilter`) protecting `/api/auth/**` endpoints.
* **AOP Audit Logging:** Annotation-driven audit trail (`@Auditable`) with pluggable storage (`AuditSink`).
* **Validation & Error Contracts:** Universal password strength validator (`@StrongPassword`) and global exception handling (`GlobalExceptionHandler`) returning consistent JSON errors (`ApiException`).
* **Browser Hardening:** Configurable CORS (`CorsProperties`) and strict Content-Security-Policy (CSP) headers.

---

## 1. Installation

### Option A: Local Maven Repository
Clone this repository and install it to your local `.m2` cache:

```bash
mvn clean install
```

Add the dependency to your target project's `pom.xml`:

```xml
<dependency>
    <groupId>com.walter.toolkit</groupId>
    <artifactId>security-starter</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

### Option B: Via JitPack (GitHub)
Add the JitPack repository and dependency to your `pom.xml`:

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependency>
    <groupId>com.github.walter-bubble</groupId>
    <artifactId>security-starter</artifactId>
    <version>main-SNAPSHOT</version>
</dependency>
```

---

## 2. Configuration (`application.properties`)

Configure your JWT secret and optional overrides in your target Spring Boot application's `application.properties`:

```properties
# Required: Base64-encoded 256-bit (32+ byte) secret key
toolkit.security.jwt.secret=\${JWT_SECRET}

# Optional JWT overrides (defaults shown)
toolkit.security.jwt.issuer=backend-toolkit
toolkit.security.jwt.access-token-expiration=900
toolkit.security.jwt.clock-skew-seconds=30

# Rate Limiting (defaults shown)
toolkit.security.rate-limit.enabled=true
toolkit.security.rate-limit.max-requests=10
toolkit.security.rate-limit.window-seconds=60

# CORS Allowed Origins (defaults shown)
toolkit.security.cors.allowed-origins=http://localhost:3000,http://localhost:5173
```

---

## 3. Application Integration

### Step 1: Implement `SecurityUser` on Your User Entity
```java
@Entity
public class User implements SecurityUser {
    private String emailAddress;
    private String password;
    // ... fields & getters

    @Override
    public Collection<String> getRoles() {
        return List.of("ROLE_USER");
    }
}
```

### Step 2: Provide a `UserDetailsService` Bean
```java
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) {
        User user = userRepository.findByEmailAddress(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return new CustomUserDetails(user);
    }
}
```

---

## 4. Usage Examples

### Password Validation
```java
public record RegisterRequest(
    @NotBlank @Email String emailAddress,
    @NotBlank @StrongPassword String password
) {}
```

### Audit Logging
Annotate any sensitive service or controller method to automatically record actor, action, status, and timestamp:
```java
@Auditable(action = "PROCESS_PAYMENT")
public void processPayment(PaymentRequest request) {
    // Business logic
}
```
*(Optional)* Register a custom `AuditSink` `@Bean` to persist `AuditEvent` records to your database instead of console logs.
