package LoginPage.model;

import java.time.Instant;

import jakarta.persistence.*;
import jakarta.persistence.Id;

@Entity
public class AuditLog {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String username;
	private String action; // LOGIN_SUCCESS, LOGIN_FAILURE, LOGOUT
	private Instant createdAt;
	public AuditLog(Long id, String username, String action, String ipAddress, Instant createdAt) {
		super();
		this.id = id;
		this.username = username;
		this.action = action;
		this.createdAt = createdAt;
	}
	
	public AuditLog()
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

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	


	public Instant getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Instant createdAt) {
		this.createdAt = createdAt;
	}
}
