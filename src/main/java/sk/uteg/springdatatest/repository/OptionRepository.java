package sk.uteg.springdatatest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sk.uteg.springdatatest.db.model.Option;

import java.util.UUID;

public interface OptionRepository extends JpaRepository<Option, UUID> {
}
