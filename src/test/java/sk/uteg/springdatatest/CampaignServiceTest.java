package sk.uteg.springdatatest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sk.uteg.springdatatest.api.model.CampaignSummary;
import sk.uteg.springdatatest.api.model.QuestionSummary;
import sk.uteg.springdatatest.api.service.CampaignService;
import sk.uteg.springdatatest.db.model.*;
import sk.uteg.springdatatest.repository.*;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CampaignServiceTest {

    @Mock
    private CampaignRepository campaignRepository;

    @Mock
    private FeedbackRepository feedbackRepository;

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private OptionRepository optionRepository;

    @Mock
    private AnswerRepository answerRepository;

    @InjectMocks
    private CampaignService campaignService;

    @Test
    void getCampaignSummary_ReturnsCorrectSummary() {
        // Mock Campaign
        Campaign campaign = new Campaign();
        campaign.setId(UUID.randomUUID());

        // Mock Question
        Question question1 = new Question();
        question1.setId(UUID.randomUUID());
        question1.setCampaign(campaign);
        question1.setType(QuestionType.RATING);
        question1.setText("How satisfied are you?");
        Option option1 = new Option();
        option1.setId(UUID.randomUUID());
        option1.setQuestion(question1);
        option1.setText("1");
        Option option2 = new Option();
        option2.setId(UUID.randomUUID());
        option2.setQuestion(question1);
        option2.setText("2");
        question1.setOptions(Arrays.asList(option1, option2));

        // Feedbacks
        Feedback feedback1 = new Feedback();
        feedback1.setId(UUID.randomUUID());
        feedback1.setCampaign(campaign);

        // Answers
        Answer answer1 = new Answer();
        answer1.setId(UUID.randomUUID());
        answer1.setFeedback(feedback1);
        answer1.setQuestion(question1);
        answer1.setRatingValue(4);

        when(campaignRepository.findById(campaign.getId())).thenReturn(Optional.of(campaign));
        when(feedbackRepository.findByCampaign(campaign)).thenReturn(Collections.singletonList(feedback1));
        when(questionRepository.findByCampaign(campaign)).thenReturn(Collections.singletonList(question1));
        when(answerRepository.findBySelectedOptionsId(option1.getId())).thenReturn(Collections.singletonList(answer1));
        when(answerRepository.countBySelectedOptions(option1)).thenReturn(1);

        CampaignSummary campaignSummary = campaignService.getCampaignSummary(campaign.getId());
//        System.out.println("Campaign Summary: " + campaignSummary);
//
//        assertEquals(1, campaignSummary.getTotalFeedbacks());
//
//        List<QuestionSummary> questionSummaries = campaignSummary.getQuestionSummaries();
//        assertEquals(1, questionSummaries.size());
        assertEquals(1, campaignSummary.getTotalFeedbacks());
        List<QuestionSummary> questionSummaries = campaignSummary.getQuestionSummaries();
        assertNotNull(questionSummaries);
        assertEquals(1, questionSummaries.size());

        QuestionSummary questionSummary = questionSummaries.get(0);
        assertEquals("How satisfied are you?", questionSummary.getName());
        assertEquals(QuestionType.RATING, questionSummary.getType());
        assertEquals(0, new BigDecimal("4.00").compareTo(questionSummary.getRatingAverage()));

        verify(campaignRepository, times(1)).findById(campaign.getId());
        verify(feedbackRepository, times(1)).findByCampaign(campaign);
        verify(questionRepository, times(1)).findByCampaign(campaign);
        verify(answerRepository, times(1)).findBySelectedOptionsId(option1.getId());
        verify(answerRepository, times(1)).countBySelectedOptions(option1);
    }
}
