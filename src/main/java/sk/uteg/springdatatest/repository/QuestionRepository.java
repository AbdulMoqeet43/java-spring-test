package sk.uteg.springdatatest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sk.uteg.springdatatest.db.model.Campaign;
import sk.uteg.springdatatest.db.model.Question;

import java.util.List;
import java.util.UUID;

public interface QuestionRepository extends JpaRepository<Question, UUID> {

    List<Question> findByCampaign(Campaign campaign);
}
