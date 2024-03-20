package com.telerikacademy.web.carpooling;

import com.telerikacademy.web.carpooling.models.Travel;
import com.telerikacademy.web.carpooling.models.TravelComment;
import com.telerikacademy.web.carpooling.repositories.contracts.TravelCommentRepository;
import com.telerikacademy.web.carpooling.repositories.contracts.TravelRepository;
import com.telerikacademy.web.carpooling.services.TravelCommentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TravelCommentServiceTests {

    @Mock
    private TravelCommentRepository travelCommentRepository;

    @Mock
    private TravelRepository travelRepository;

    @InjectMocks
    private TravelCommentServiceImpl travelCommentService;

    private Travel travel;
    private TravelComment travelComment;

    @BeforeEach
    void setUp() {
        // Initialize your Travel and TravelComment objects here
        travel = new Travel();
        travel.setId(1);
        travelComment = new TravelComment();
        travelComment.setId(1);
        travelComment.setTravel(travel);
        travelComment.setComment("Great trip!");
    }

    @Test
    void whenAddOrUpdateCommentWithNewComment_thenSaveOrUpdateCommentCalled() {
        when(travelRepository.getById(travel.getId())).thenReturn(travel);
        when(travelCommentRepository.findByTravelId(travel.getId())).thenReturn(null); // Simulate no existing comment

        travelCommentService.addOrUpdateComment(travel.getId(), "New Comment");

        verify(travelCommentRepository).saveOrUpdateComment(any(TravelComment.class));
    }

    @Test
    void whenAddOrUpdateCommentWithExistingComment_thenSaveOrUpdateCommentCalled() {
        when(travelRepository.getById(travel.getId())).thenReturn(travel);
        when(travelCommentRepository.findByTravelId(travel.getId())).thenReturn(travelComment); // Return existing comment

        travelCommentService.addOrUpdateComment(travel.getId(), "Updated Comment");

        verify(travelCommentRepository).saveOrUpdateComment(travelComment);
        assert "Updated Comment".equals(travelComment.getComment());
    }

    @Test
    void whenFindByTravelId_thenReturnTravelComment() {
        when(travelCommentRepository.findByTravelId(travel.getId())).thenReturn(travelComment);

        TravelComment result = travelCommentService.findByTravelId(travel.getId());

        assert result.equals(travelComment);
    }

    @Test
    void whenDeleteCommentByTravelId_thenRepositoryDeleteCalled() {
        doNothing().when(travelCommentRepository).deleteCommentByTravelId(travel.getId());

        travelCommentService.deleteCommentByTravelId(travel.getId());

        verify(travelCommentRepository).deleteCommentByTravelId(travel.getId());
    }

}
