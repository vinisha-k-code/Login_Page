package LoginPage.Controller;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import LoginPage.Repository.AuditLogRepository;
import LoginPage.Repository.UserRepository;
import LoginPage.Service.LoginAttemptService;
import LoginPage.Service.RegisterService;
import LoginPage.dto.LoginRequest;
import LoginPage.dto.RegisterRequest;
import LoginPage.model.AuditLog;
import LoginPage.model.User;
import LoginPage.security.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
  private final AuthenticationManager authManager;
  private final JwtUtil jwtUtil;
  private final UserRepository userRepo;
  private final LoginAttemptService attemptService;
  private final AuditLogRepository auditRepo;
   
  public AuthController(AuthenticationManager authManager, JwtUtil jwtUtil, UserRepository userRepo,
                        LoginAttemptService attemptService, AuditLogRepository auditRepo) 
  {
    this.authManager = authManager; 
    this.jwtUtil = jwtUtil; 
    this.userRepo = userRepo; 
    this.attemptService = attemptService; 
    this.auditRepo = auditRepo;
  }
 
  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody LoginRequest req, HttpServletRequest httpReq){
    String username = req.getUsername();
 
    Optional<User> maybeUser = userRepo.findByUsername(username);
    if(maybeUser.isPresent() && maybeUser.get().isAccountLocked())
    {
      // try to auto-unlock if time expired
      if(attemptService.unlockIfTimeExpired(maybeUser.get()))
      {
        // continue
      } else 
      {
        return ResponseEntity.status(HttpStatus.LOCKED).body(Map.of("error","Account locked. Try later."));
      }
    }
 
    try {
      UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, req.getPassword());
      Authentication auth = authManager.authenticate(token);
 
      UserDetails ud = (UserDetails) auth.getPrincipal();
      String jwt = jwtUtil.generateToken(ud);
 
      // success: reset attempts
      attemptService.loginSucceeded(username);
 
      // audit
      AuditLog a = new AuditLog(); a.setUsername(username); a.setAction("LOGIN_SUCCESS"); a.setIpAddress(httpReq.getRemoteAddr()); a.setCreatedAt(Instant.now());
      auditRepo.save(a);
 
      return ResponseEntity.ok(Map.of("token", jwt));
    } catch (BadCredentialsException ex){
      attemptService.loginFailed(username);
      AuditLog a = new AuditLog(); a.setUsername(username); a.setAction("LOGIN_FAILURE"); a.setIpAddress(httpReq.getRemoteAddr()); a.setCreatedAt(Instant.now());
      auditRepo.save(a);
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error","Invalid credentials"));
    }
  }
 
  @PostMapping("/logout")
  public ResponseEntity<?> logout(@RequestHeader(name="Authorization", required=false) String authHeader, HttpServletRequest req){
    String username = "unknown";
    if(authHeader != null && authHeader.startsWith("Bearer ")){
      String token = authHeader.substring(7);
      if(jwtUtil.validateToken(token)) username = jwtUtil.getUsernameFromToken(token);
    }
    AuditLog a = new AuditLog(); a.setUsername(username); a.setAction("LOGOUT"); a.setIpAddress(req.getRemoteAddr()); a.setCreatedAt(Instant.now());
    auditRepo.save(a);
 
    // optionally blacklist token here
 
    return ResponseEntity.ok(Map.of("msg","logged out"));
  }
}
