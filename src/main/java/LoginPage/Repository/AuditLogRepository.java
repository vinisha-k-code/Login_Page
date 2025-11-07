package LoginPage.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import LoginPage.model.AuditLog;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog,Long>{

}
