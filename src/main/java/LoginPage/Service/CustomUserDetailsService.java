package LoginPage.Service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import LoginPage.Repository.UserRepository;
import LoginPage.model.User;

@Service
public class CustomUserDetailsService implements UserDetailsService {
  private final UserRepository repo;
  public CustomUserDetailsService(UserRepository repo){ 
	  this.repo = repo; 
	  }
 
  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User u = repo.findByUsername(username).orElseThrow(()-> new UsernameNotFoundException("Not found"));
 
    // If account locked and not expired, throw DisabledException so authentication fails earlier
    if(u.isAccountLocked()) throw new LockedException("Account locked");
 
    List<GrantedAuthority> auths = u.getRoles().stream().map(r-> new SimpleGrantedAuthority(r.getName())).collect(Collectors.toList());
    return new org.springframework.security.core.userdetails.User(u.getUsername(), u.getPassword(), true, true, true, !u.isAccountLocked(), auths);
  }
}
