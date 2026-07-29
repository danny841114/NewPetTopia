package petTopia.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;

import petTopia.model.user.Admin;

public interface AdminRepository extends JpaRepository<Admin, Integer> {
}