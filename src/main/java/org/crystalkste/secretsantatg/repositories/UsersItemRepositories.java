package org.crystalkste.secretsantatg.repositories;

import org.crystalkste.secretsantatg.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface UsersItemRepositories extends JpaRepository<Users,String> {
    List<Users> findAll();
}
