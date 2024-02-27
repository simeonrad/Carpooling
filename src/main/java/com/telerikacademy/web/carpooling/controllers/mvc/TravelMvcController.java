package com.telerikacademy.web.carpooling.controllers.mvc;

import com.telerikacademy.web.carpooling.models.*;
import com.telerikacademy.web.carpooling.services.TravelService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
@Controller
@RequestMapping("travels")
public class TravelMvcController {

    private final TravelService travelService;
@Autowired
    public TravelMvcController(TravelService travelService) {
        this.travelService = travelService;
    }

    @GetMapping("/search-travels")
    public String filterUsers(@ModelAttribute("filterOptions") FilterTravelDto filterTravelDto, Model model){
            FilterTravelOptions filterTravelOptions = new FilterTravelOptions(filterTravelDto.getAuthor(),
                    filterTravelDto.getStartPoint(), filterTravelDto.getEndPoint(),
                    filterTravelDto.getDepartureTime(), filterTravelDto.getFreeSpots(),
                    filterTravelDto.getTravelStatus(), filterTravelDto.getSortBy(),
                    filterTravelDto.getSortOrder());
            List<Travel> travels = travelService.get(filterTravelOptions);
            model.addAttribute("filterOptions", filterTravelDto);
            model.addAttribute("travels", travels);
            return "searchTravelView";
    }
}
