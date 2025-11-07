package LoginPage.Service;

import java.time.Duration;
import java.time.Instant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import LoginPage.Repository.UserRepository;
import LoginPage.model.User;

@Service
public class LoginAttemptService {
  private final UserRepository userRepo;
  @Value("${app.maxFailedAttempts}")
  private int maxFailed;
  @Value("${app.unlockTimeMinutes}")
  private int unlockMinutes;
 
  public LoginAttemptService(UserRepository repo){ this.userRepo = repo; }
 
  public void loginSucceeded(String username){
    userRepo.findByUsername(username).ifPresent(u -> { u.setFailedAttempts(0); u.setAccountLocked(false); u.setLockTime(null); userRepo.save(u); });
  }
 
  public void loginFailed(String username){
    userRepo.findByUsername(username).ifPresent(u -> {
      u.setFailedAttempts(u.getFailedAttempts()+1);
      if (u.getFailedAttempts() >= maxFailed) {
        u.setAccountLocked(true);
        u.setLockTime(Instant.now());
      }
      userRepo.save(u);
    });
  }
 
  public boolean unlockIfTimeExpired(User u){
    if (u.getLockTime() == null) 
    	return false;
    if (u.getLockTime().plus(Duration.ofMinutes(unlockMinutes)).isBefore(Instant.now()))
    {
      u.setAccountLocked(false); u.setFailedAttempts(0); u.setLockTime(null); userRepo.save(u); 
      return true;
    }
    return false;
  }
}