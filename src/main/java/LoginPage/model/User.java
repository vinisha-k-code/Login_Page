package LoginPage.model;

import java.time.*;
import java.util.*;

import jakarta.persistence.*;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User {
  @Id 
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(unique=true)
  private String username;
  private String password; // BCrypt hash
 
  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(name = "user_roles", 
  joinColumns = @JoinColumn(name = "user_id"),
  inverseJoinColumns = @JoinColumn(name = "role_id"))
  private Set<Role> roles = new HashSet<>();
 
  private int failedAttempts;
  private boolean accountLocked;
  private Instant lockTime;
  public User(Long id, String username, String password, Set<Role> roles, int failedAttempts, boolean accountLocked,
		Instant lockTime) 
  {
	super();
	this.id = id;
	this.username = username;
	this.password = password;
	this.roles = roles;
	this.failedAttempts = failedAttempts;
	this.accountLocked = accountLocked;
	this.lockTime = lockTime;
	}

	public User()
	{
		
	}

	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getPassword() {
		return password;
}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public Set<Role> getRoles() {
		return roles;
	}
	
	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}
	
	public int getFailedAttempts() {
		return failedAttempts;
	}
	
	public void setFailedAttempts(int failedAttempts) {
		this.failedAttempts = failedAttempts;
	}
	
	public boolean isAccountLocked() {
		return accountLocked;
	}
	
	public void setAccountLocked(boolean accountLocked) {
		this.accountLocked = accountLocked;
	}
	
	public Instant getLockTime() {
		return lockTime;
	}
	
	public void setLockTime(Instant lockTime) {
		this.lockTime = lockTime;
	}
  
}
