package sk.uteg.springdatatest.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sk.uteg.springdatatest.api.model.CampaignSummary;
import sk.uteg.springdatatest.api.model.OptionSummary;
import sk.uteg.springdatatest.api.model.QuestionSummary;
import sk.uteg.springdatatest.db.model.*;
import sk.uteg.springdatatest.repository.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CampaignService {

    @Autowired
    private CampaignRepository campaignRepository;

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private OptionRepository optionRepository;

    @Autowired
    private AnswerRepository answerRepository;

    public CampaignSummary getCampaignSummary(UUID campaignId) {
        try {
            Optional  campaign1 = campaignRepository.findById(campaignId);
//            if (!campaign1.isPresent())
            Campaign campaign = (Campaign) campaign1.get();
            List<Feedback> feedbacks = feedbackRepository.findByCampaign(campaign);

            CampaignSummary campaignSummary = new CampaignSummary();
            campaignSummary.setTotalFeedbacks(feedbacks.size());

            List<QuestionSummary> questionSummaries = campaign.getQuestions().stream()
                    .map(this::buildQuestionSummary)
                    .collect(Collectors.toList());

            campaignSummary.setQuestionSummaries(questionSummaries);

            return campaignSummary;
        } catch (Exception e) {
            return getDefaultCampaignSummary();
        }
    }

    private CampaignSummary getDefaultCampaignSummary() {
        CampaignSummary defaultSummary = new CampaignSummary();
        // Set default values or handle the error state as needed
        return defaultSummary;
    }



    private QuestionSummary buildQuestionSummary(Question question) {
        QuestionSummary questionSummary = new QuestionSummary();
        questionSummary.setName(question.getText());
        questionSummary.setType(question.getType());

        if (question.getType() == QuestionType.RATING) {
            calculateRatingAverage(question, questionSummary);
        } else if (question.getType() == QuestionType.CHOICE) {
            calculateOptionOccurrences(question, questionSummary);
        }

        return questionSummary;
    }

    private void calculateRatingAverage(Question question, QuestionSummary questionSummary) {
        List<Option> options = question.getOptions();
        if (!options.isEmpty()) {
            BigDecimal ratingSum = BigDecimal.valueOf(options.stream()
                    .mapToDouble(option -> getAverageRatingFromAnswers(option.getId()))
                    .sum());
            BigDecimal ratingAverage = ratingSum.divide(BigDecimal.valueOf(options.size()), 2, BigDecimal.ROUND_HALF_UP);
            questionSummary.setRatingAverage(ratingAverage);
        } else {
            questionSummary.setRatingAverage(BigDecimal.ZERO);
        }
    }

    private double getAverageRatingFromAnswers(UUID optionId) {
        List<Answer> answers = answerRepository.findBySelectedOptionsId(optionId);
        if (!answers.isEmpty()) {
            return answers.stream().mapToInt(Answer::getRatingValue).average().orElse(0.0);
        }
        return 0.0;
    }

    private void calculateOptionOccurrences(Question question, QuestionSummary questionSummary) {
        List<Option> options = question.getOptions();
        Map<String, Integer> optionOccurrences = new HashMap<>();

        options.forEach(option -> {
            int occurrences = answerRepository.countBySelectedOptions(option);
            optionOccurrences.put(option.getText(), occurrences);
        });

        List<OptionSummary> optionSummaries = optionOccurrences.entrySet().stream()
                .map(entry -> {
                    OptionSummary optionSummary = new OptionSummary();
                    optionSummary.setText(entry.getKey());
                    optionSummary.setOccurrences(entry.getValue());
                    return optionSummary;
                })
                .collect(Collectors.toList());

        questionSummary.setOptionSummaries(optionSummaries);
    }
}