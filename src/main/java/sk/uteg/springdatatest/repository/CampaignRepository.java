package sk.uteg.springdatatest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sk.uteg.springdatatest.db.model.Campaign;

import java.util.UUID;

public interface CampaignRepository extends JpaRepository<Campaign, UUID> {
}
