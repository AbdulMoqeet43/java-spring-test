package sk.uteg.springdatatest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sk.uteg.springdatatest.db.model.Campaign;
import sk.uteg.springdatatest.db.model.Feedback;

import java.util.List;
import java.util.UUID;

public interface FeedbackRepository extends JpaRepository<Feedback, UUID> {
    List<Feedback> findByCampaign(Campaign campaign);
}
