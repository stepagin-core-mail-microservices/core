package ru.stepagin.core.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.stepagin.core.entity.ImageEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ImageRepository extends JpaRepository<ImageEntity, UUID> {
    @Query("select i from ImageEntity i where upper(i.owner.login) = upper(:login) "
            + "AND i.size BETWEEN :sizeFrom AND :sizeTo "
            + "AND i.creationDate BETWEEN :dateFrom AND :dateTo "
            + "AND cast(i.id as string) LIKE %:id%"
    )
    List<ImageEntity> findByOwnerAndFilters(@Param("login") String login,
                                            @Param("dateFrom") LocalDateTime dateFrom,
                                            @Param("dateTo") LocalDateTime dateTo,
                                            @Param("sizeFrom") Long sizeFrom,
                                            @Param("sizeTo") Long sizeTo,
                                            @Param("id") String id,
                                            Pageable pageable);


    @Query("select i from ImageEntity i where i.size BETWEEN :sizeFrom AND :sizeTo "
            + "AND i.creationDate BETWEEN :dateFrom AND :dateTo "
            + "AND cast(i.id as string) LIKE %:id%"
    )
    List<ImageEntity> findByFilters(@Param("dateFrom") LocalDateTime dateFrom,
                                    @Param("dateTo") LocalDateTime dateTo,
                                    @Param("sizeFrom") Long sizeFrom,
                                    @Param("sizeTo") Long sizeTo,
                                    @Param("id") String id,
                                    Pageable pageable);


}
