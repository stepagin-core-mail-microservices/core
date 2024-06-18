package ru.stepagin.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import ru.stepagin.core.entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    @Query("select u from UserEntity u where upper(u.login) = upper(?1)")
    UserEntity findByLogin(String login);

    @Transactional
    @Modifying
    @Query("update UserEntity u set u.blocked = true where u.id = ?1")
    void blockById(Long id);

    @Transactional
    @Modifying
    @Query("update UserEntity u set u.blocked = false where u.id = ?1")
    void unblockById(Long id);

    @Query("select (count(u) > 0) from UserEntity u where upper(u.login) = upper(?1)")
    boolean existsByLogin(String login);

}
