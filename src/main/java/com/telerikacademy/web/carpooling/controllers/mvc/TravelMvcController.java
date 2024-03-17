package com.telerikacademy.web.carpooling.controllers.mvc;

import com.telerikacademy.web.carpooling.exceptions.*;
import com.telerikacademy.web.carpooling.helpers.AuthenticationHelper;
import com.telerikacademy.web.carpooling.helpers.TravelApplicationMapper;
import com.telerikacademy.web.carpooling.helpers.TravelMapper;
import com.telerikacademy.web.carpooling.models.*;
import com.telerikacademy.web.carpooling.models.dtos.CarDto;
import com.telerikacademy.web.carpooling.models.dtos.FilterTravelDto;
import com.telerikacademy.web.carpooling.models.dtos.TravelApplicationDto;
import com.telerikacademy.web.carpooling.models.dtos.TravelDto;
import com.telerikacademy.web.carpooling.models.enums.ApplicationStatus;
import com.telerikacademy.web.carpooling.repositories.contracts.StatusRepository;
import com.telerikacademy.web.carpooling.services.contracts.*;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.BindingResult;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("travels")
public class TravelMvcController {

    private final TravelService travelService;
    private final AuthenticationHelper authenticationHelper;
    private final TravelMapper travelMapper;
    private final TravelApplicationMapper travelApplicationMapper;
    private final TravelApplicationService travelApplicationService;
    private final StatusRepository statusRepository;
    private final TravelCommentService travelCommentService;
    private final EngineService engineService;
    private final MakeService makeService;
    private final ColourService colourService;
    private final CarService carService;
    private final UserService userService;

    @ModelAttribute("isAdmin")
    public boolean populateIsAdmin(HttpSession session) {
        boolean isAdmin = false;
        if (populateIsAuthenticated(session)) {
            User currentUser = (User) session.getAttribute("currentUser");
            if (currentUser.getRole().getName().equals("Admin")) {
                isAdmin = true;
            }
        }
        return isAdmin;
    }

    @ModelAttribute("isAuthenticated")
    public boolean populateIsAuthenticated(HttpSession session) {
        return session.getAttribute("currentUser") != null;
    }


    @Autowired
    public TravelMvcController(TravelService travelService, TravelApplicationService travelApplicationService, AuthenticationHelper authenticationHelper, TravelMapper travelMapper, TravelApplicationMapper travelApplicationMapper, StatusRepository statusRepository, TravelCommentService travelCommentService, EngineService engineService, MakeService makeService, ColourService colourService, CarService carService, UserService userService) {
        this.travelService = travelService;
        this.authenticationHelper = authenticationHelper;
        this.travelApplicationService = travelApplicationService;
        this.travelMapper = travelMapper;
        this.travelApplicationMapper = travelApplicationMapper;
        this.statusRepository = statusRepository;
        this.travelCommentService = travelCommentService;
        this.engineService = engineService;
        this.makeService = makeService;
        this.colourService = colourService;
        this.carService = carService;
        this.userService = userService;
    }


    @GetMapping("/search-travels")
    public String filterUsers(@ModelAttribute("filterOptions") FilterTravelDto filterTravelDto, Model model,
                              @RequestParam(defaultValue = "0", name = "travelPage") int travelPage,
                              @RequestParam(defaultValue = "5", name = "travelSize") int travelSize) {
        FilterTravelOptions filterTravelOptions = new FilterTravelOptions(filterTravelDto.getAuthor(),
                filterTravelDto.getStartPoint(), filterTravelDto.getEndPoint(),
                filterTravelDto.getDepartureTime(), filterTravelDto.getFreeSpots(),
                filterTravelDto.getTravelStatus(), filterTravelDto.getSortBy(),
                filterTravelDto.getSortOrder());
        Pageable travelsPageable = PageRequest.of(travelPage, travelSize);
        Page<Travel> travels = travelService.getMyTravels(filterTravelOptions, travelsPageable);
        model.addAttribute("filterOptions", filterTravelDto);
        model.addAttribute("travels", travels);
        model.addAttribute("now", LocalDateTime.now());
        return "searchTravelView";
    }

    @GetMapping("/create")
    public String showCreateTravelForm(Model model, HttpSession session) {
        try {
            User user = authenticationHelper.tryGetUser(session);
            userService.checkIfVerified(user);
            if (user.isBlocked()) {
                model.addAttribute("status", "Blocked users cannot create travels!");
                return "404-page";
            }
            model.addAttribute("createTravel", new TravelDto());
            model.addAttribute("userCars", user.getCars());
            return "createTravel";
        } catch (AuthenticationFailureException e) {
            return "redirect:/auth/login";
        } catch (UnauthorizedOperationException e) {
            model.addAttribute("status", e.getMessage());
            return "404-page";
        }
    }

    @PostMapping("/create")
    public String handleCreateTravel(@ModelAttribute("createTravel") TravelDto travelDto, BindingResult bindingResult, HttpSession session, Model model) {
        if (bindingResult.hasErrors()) {
            return "createTravel";
        }
        try {
            User user = authenticationHelper.tryGetUser(session);
            Travel travel = travelMapper.fromDto(travelDto);
            travel.setDriver(user);
            travel.setCar(carService.getById(travelDto.getCarId()));
            travelService.create(travel, user);
            travelCommentService.addOrUpdateComment(travel.getId(), travelDto.getComment());
            return "redirect:/travels/search-travels";
        } catch (Exception e) {
            model.addAttribute("404-page", "Error creating travel: " + e.getMessage());
            return "createTravel";
        }
    }

    @GetMapping("/car/create")
    public String showCreateCarForm(Model model, HttpSession session) {
        try {
            authenticationHelper.tryGetUser(session);
            model.addAttribute("CarDto", new CarDto());
            model.addAttribute("engines", engineService.getAll());
            return "createCar";
        } catch (AuthenticationFailureException e) {
            return "redirect:/auth/login";
        }

    }

    @PostMapping("/car/create")
    public String createCar(@ModelAttribute(name = "carDto") CarDto carDto, HttpSession session, Model model) {
        try {
            User user = authenticationHelper.tryGetUser(session);
            Car car = new Car();
            Colour colour = colourService.create(carDto.getColour());
            Make make = makeService.create(carDto.getMake());
            CarEngine engine = engineService.getByValue(carDto.getEngine());
            car.setColour(colour);
            car.setEngine(engine);
            car.setMake(make);
            car.setPlate(carDto.getPlate());
            carService.create(car, user);
            user.getCars().add(car);
            return "redirect:/travels/create";
        } catch (AuthenticationFailureException e) {
            return "redirect:/auth/login";
        } catch (UnauthorizedOperationException e) {
            model.addAttribute("status", e.getMessage());
            return "404-page";
        }
    }

    @GetMapping("/{id}")
    public String singleTravel(@PathVariable int id, Model model, HttpSession session) {
        try {
            User user = authenticationHelper.tryGetUser(session);
            Travel travel = travelService.getById(id);
            List<User> usersToFeedback = new ArrayList<>();
            try {
                List<User> approvedUsersInTravel = new ArrayList<>();
                for (TravelApplication application : travelApplicationService.getByTravelId(id)) {
                    if (application.getStatus().getStatus().equals(ApplicationStatus.APPROVED)) {
                        approvedUsersInTravel.add(application.getPassenger());
                    }
                }
                if (travel.getDriver().equals(user)) {
                    usersToFeedback.addAll(approvedUsersInTravel);
                }
                if (approvedUsersInTravel.contains(user)) {
                    usersToFeedback.add(travel.getDriver());
                }
            } catch (EntityNotFoundException ignored) {
            }
            model.addAttribute("travel", travel);
            model.addAttribute("now", LocalDateTime.now());
            model.addAttribute("usersToGiveFeedback", usersToFeedback);
            return "single-travel";
        } catch (AuthenticationFailureException e) {
            return "redirect:/auth/login";
        }
    }

    @GetMapping("/update/{id}")
    public String showUpdateTravelForm(@PathVariable("id") int id, Model model, HttpSession session) {
        try {
            User user = authenticationHelper.tryGetUser(session);
            Travel travel = travelService.getById(id);

            if (!travel.getDriver().equals(user)) {
                model.addAttribute("status", "Unauthorized access to update travel");
                return "404-page";
            }

            TravelDto travelDto = travelMapper.toDto(travel);
            model.addAttribute("updateTravel", travelDto);
            model.addAttribute("travelId", id);
            return "updateTravel";
        } catch (Exception e) {
            model.addAttribute("status", "Error finding travel: " + e.getMessage());
            return "404-page";
        }
    }


    @PostMapping("/update/{id}")
    public String handleUpdateTravel(@PathVariable("id") int id, @ModelAttribute("updateTravel") TravelDto travelDto, BindingResult bindingResult, HttpSession session, Model model) {
        if (bindingResult.hasErrors()) {
            return "updateTravel";
        }
        try {
            User user = authenticationHelper.tryGetUser(session);
            Travel existingTravel = travelService.getById(id);

            if (!existingTravel.getDriver().equals(user)) {
                model.addAttribute("status", "Unauthorized attempt to update travel");
                return "404-page";
            }
            existingTravel.setFreeSpots(travelDto.getFreeSpots());
            travelService.update(existingTravel, user);

            if (travelDto.getComment() != null && !travelDto.getComment().trim().isEmpty()) {
                travelCommentService.addOrUpdateComment(id, travelDto.getComment());
            } else {
                travelCommentService.deleteCommentByTravelId(id);
            }

            return "redirect:/travels/search-travels";
        } catch (Exception e) {
            model.addAttribute("status", "Error updating travel: " + e.getMessage());
            return "updateTravel";
        }
    }


    @GetMapping("/applications/{id}")
    public String travelApplications(@PathVariable int id, Model model, HttpSession session) {
        try {
            User user = authenticationHelper.tryGetUser(session);
            if (user.equals(travelService.getById(id).getDriver())) {
                List<TravelApplication> applications = new ArrayList<>();
                try {
                    applications = travelApplicationService.getByTravelId(id);
                } catch (EntityNotFoundException ignored) {
                }
                model.addAttribute("applications", applications);
                model.addAttribute("now", LocalDateTime.now());
                return "travel-applications-view";
            }
        } catch (AuthenticationFailureException e) {
            return "redirect:/auth/login";
        }
        return "redirect:/auth/login";
    }

    @PostMapping("/applications/approve/{id}")
    public String approveApplications(@PathVariable int id, Model model, HttpSession session) {
        try {
            User user = authenticationHelper.tryGetUser(session);
            TravelApplication travelApplication = travelApplicationService.getById(id);
            if (user.equals(travelService.getById(travelApplication.getTravel().getId()).getDriver())) {
                travelApplicationService.approve(user, travelApplication);
                return "redirect:/travels/applications/" + travelApplication.getTravel().getId();
            }
        } catch (AuthenticationFailureException e) {
            return "redirect:/auth/login";
        } catch (ForbiddenOperationException | UnauthorizedOperationException e) {
            model.addAttribute("status", e.getMessage());
            return "404-page";
        }
        return "redirect:/auth/login";
    }

    @PostMapping("/delete/{id}")
    public String deleteTravel(@PathVariable int id, Model model, HttpSession session) {
        try {
            User user = authenticationHelper.tryGetUser(session);
            Travel travel = travelService.getById(id);
            travelService.delete(travel, user);
            return "redirect:/travels/search-travels";
        } catch (AuthenticationFailureException e) {
            return "redirect:/auth/login";
        } catch (ForbiddenOperationException | UnauthorizedOperationException e) {
            model.addAttribute("status", e.getMessage());
            return "404-page";
        }
    }

    @PostMapping("/applications/decline/{id}")
    public String declineApplications(@PathVariable int id, Model model, HttpSession session) {
        try {
            User user = authenticationHelper.tryGetUser(session);
            TravelApplication travelApplication = travelApplicationService.getById(id);
            if (user.equals(travelService.getById(travelApplication.getTravel().getId()).getDriver())) {
                travelApplicationService.decline(user, travelApplication);
                return "redirect:/travels/applications/" + travelApplication.getTravel().getId();
            }
        } catch (AuthenticationFailureException e) {
            return "redirect:/auth/login";
        } catch (ForbiddenOperationException | UnauthorizedOperationException e) {
            model.addAttribute("status", e.getMessage());
            return "404-page";
        }
        return "redirect:/auth/login";
    }

    @PostMapping("/applications/cancel/{id}")
    public String cancelApplications(@PathVariable int id, Model model, HttpSession session) {
        try {
            User user = authenticationHelper.tryGetUser(session);
            TravelApplication travelApplication = travelApplicationService.getById(id);
            travelApplicationService.cancel(user, travelApplication);
            return "redirect:/profile/my-travel-applications";

        } catch (AuthenticationFailureException e) {
            return "redirect:/auth/login";
        } catch (ForbiddenOperationException | UnauthorizedOperationException | EntityNotFoundException e) {
            model.addAttribute("status", e.getMessage());
            return "404-page";
        }

    }

    @PostMapping("/complete/{id}")
    public String completeTravel(@PathVariable int id, Model model, HttpSession session) {
        try {
            User user = authenticationHelper.tryGetUser(session);
            Travel travel = travelService.getById(id);
            travelService.complete(user, travel);
            return "redirect:/travels/" + id;

        } catch (AuthenticationFailureException e) {
            return "redirect:/auth/login";
        } catch (ForbiddenOperationException | UnauthorizedOperationException | EntityNotFoundException e) {
            model.addAttribute("status", e.getMessage());
            return "404-page";
        }

    }

    @PostMapping("/cancel/{id}")
    public String cancelTravel(@PathVariable int id, Model model, HttpSession session) {
        try {
            User user = authenticationHelper.tryGetUser(session);
            Travel travel = travelService.getById(id);
            travelService.cancel(user, travel);
            return "redirect:/travels/" + id;

        } catch (AuthenticationFailureException e) {
            return "redirect:/auth/login";
        } catch (ForbiddenOperationException | UnauthorizedOperationException | EntityNotFoundException e) {
            model.addAttribute("status", e.getMessage());
            return "404-page";
        }

    }

    @GetMapping("/applications/create/{travelId}")
    public String showCreateApplicationForm(@PathVariable("travelId") int travelId, Model model, HttpSession session) {
        TravelApplicationDto applicationDto = new TravelApplicationDto();
        try {
            User currentUser = authenticationHelper.tryGetUser(session);
            if (currentUser.isBlocked()) {
                model.addAttribute("status", "Blocked users cannot apply for travels!");
                return "404-page";
            }
            userService.checkIfVerified(currentUser);
            Travel travel = travelService.getById(travelId);

            travelApplicationService.checkIfCreated(travel, currentUser);

            applicationDto.setTravelId(travelId);
            model.addAttribute("travel", travel);
            model.addAttribute("applicationDto", applicationDto);
        } catch (DuplicateExistsException | UnauthorizedOperationException e) {
            model.addAttribute("status", e.getMessage());
            return "404-page";
        }
        return "createTravelApplication";
    }

    @PostMapping("/applications/create/{travelId}")
    public String handleCreateApplication(@PathVariable("travelId") int travelId,
                                          @Valid @ModelAttribute("applicationDto") TravelApplicationDto applicationDto,
                                          BindingResult bindingResult, HttpSession session, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("travelId", travelId);
            return "createTravelApplication";
        }
        try {
            User currentUser = authenticationHelper.tryGetUser(session);
            applicationDto.setPassengerUsername(currentUser.getUsername());

            TravelApplication application = travelApplicationMapper.fromDto(applicationDto, currentUser);
            application.setPassenger(currentUser);
            application.setTravel(travelService.getById(travelId));

            application.setStatus(statusRepository.getByValue(ApplicationStatus.PENDING));

            travelApplicationService.create(application);
            return "redirect:/profile/my-travel-applications";
        } catch (Exception e) {
            model.addAttribute("status", "Error creating travel application: " + e.getMessage());
            model.addAttribute("travelId", travelId);
            return "404-page";
        }
    }

    @GetMapping("/applications/update/{applicationId}")
    public String showUpdateApplicationForm(@PathVariable("applicationId") int applicationId, Model model, HttpSession session) {
        try {
            User currentUser = authenticationHelper.tryGetUser(session);
            TravelApplication application = travelApplicationService.getById(applicationId);
            Travel travel = application.getTravel();

            if (!application.getPassenger().equals(currentUser)) {
                model.addAttribute("status", "Only authors can edit applications!");
                return "404-page";
            }

            TravelApplicationDto applicationDto = travelApplicationMapper.toDto(application);
            model.addAttribute("applicationDto", applicationDto);
            model.addAttribute("travel", travel);
            return "updateTravelApplication";
        } catch (Exception e) {
            model.addAttribute("status", "Error retrieving application: " + e.getMessage());
            return "404-page";
        }
    }

    @PostMapping("/applications/update/{applicationId}")
    public String handleUpdateApplication(@PathVariable("applicationId") int applicationId,
                                          @Valid @ModelAttribute("applicationDto") TravelApplicationDto applicationDto,
                                          BindingResult bindingResult, HttpSession session, Model model) {
        if (bindingResult.hasErrors()) {
            return "updateTravelApplication";
        }
        try {
            User currentUser = authenticationHelper.tryGetUser(session);

            applicationDto.setId(applicationId);
            TravelApplication application = travelApplicationMapper.fromDto(applicationDto, currentUser);
            application.setStatus(statusRepository.getByValue(ApplicationStatus.PENDING));
            travelApplicationService.update(application);

            return "redirect:/travels/search-travels";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error updating application: " + e.getMessage());
            return "updateTravelApplication";
        }
    }


}
