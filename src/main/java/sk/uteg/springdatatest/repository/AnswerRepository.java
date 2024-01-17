package sk.uteg.springdatatest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sk.uteg.springdatatest.db.model.Answer;
import sk.uteg.springdatatest.db.model.Option;

import java.util.List;
import java.util.UUID;

public interface AnswerRepository extends JpaRepository<Answer, UUID> {
    @Query("SELECT a FROM Answer a JOIN a.selectedOptions o WHERE o.id = :optionId")
    List<Answer> findBySelectedOptionsId(@Param("optionId") UUID optionId);

    int countBySelectedOptions(@Param("option") Option option);
}
