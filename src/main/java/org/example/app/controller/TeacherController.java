package org.example.app.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.example.app.dto.BookedLessonDto;
import org.example.app.dto.FreeTimeDto;
import org.example.app.dto.PrizeDto;
import org.example.app.security.UserPrincipal;
import org.example.app.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.List;

@RestController
@Validated
public class TeacherController {
    private UserService userService;
    private PrizeService prizeService;
    private FreeTimeService freeTimeService;
    private CrossTimeRangeValidationService crossTimeRangeValidationService;
    private BookedLessonsService bookedLessonsService;

    @Autowired
    public TeacherController(UserService userService, PrizeService prizeService, FreeTimeService freeTimeService,
                             CrossTimeRangeValidationService crossTimeRangeValidationService, BookedLessonsService bookedLessonsService) {
        this.userService = userService;
        this.prizeService = prizeService;
        this.freeTimeService = freeTimeService;
        this.crossTimeRangeValidationService = crossTimeRangeValidationService;
        this.bookedLessonsService = bookedLessonsService;
    }

    @ApiOperation(value = "", authorizations = {@Authorization(value = "JWT")})
    @PostMapping("/teacher/setPrize")
    public ResponseEntity setPrize(@Valid @RequestBody PrizeDto prizeDto,
                                   @ApiIgnore @AuthenticationPrincipal UserPrincipal principal) {
        prizeService.setPrize(prizeDto, principal.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @ApiOperation(value = "", authorizations = {@Authorization(value = "JWT")})
    @PostMapping("/teacher/setFreeTime")
    public ResponseEntity setFreeTime(@Valid @RequestBody FreeTimeDto freeTimeDto,
                                      @ApiIgnore @AuthenticationPrincipal UserPrincipal principal) {
        crossTimeRangeValidationService.checkIfTimeRangeSlotIsUnique(freeTimeDto, principal.getUsername());
        freeTimeService.save(freeTimeDto, principal.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/approveBooking/{lessonId}")
    public ResponseEntity approveBooking(@PathVariable Long lessonId) {
        bookedLessonsService.approveLessonsBooking(lessonId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/declineBooking/{lessonId}")
    public ResponseEntity declineBooking(@PathVariable Long lessonId) {
        bookedLessonsService.declineLessonsBooking(lessonId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @ApiOperation(value = "", authorizations = {@Authorization(value = "JWT")})
    @GetMapping("/teacher/bookedLessons")
    public ResponseEntity<List<BookedLessonDto>> getAllBookedLessons(@ApiIgnore @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.status(HttpStatus.OK).body(bookedLessonsService.getByTeacherId(principal.getUser().getId()));
    }

    @ApiOperation(value = "", authorizations = {@Authorization(value = "JWT")})
    @GetMapping("/teacher/freeHours")
    public ResponseEntity<List<FreeTimeDto>> getAllFreeHours(@ApiIgnore @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.status(HttpStatus.OK).body(freeTimeService.getTeacherFreeTimes(principal.getUser().getId()));
    }

    @ApiOperation(value = "", authorizations = {@Authorization(value = "JWT")})
    @GetMapping("/teacher/prize")
    public ResponseEntity<PrizeDto> getPrize(@ApiIgnore @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.status(HttpStatus.OK).body(prizeService.getPrizeById(principal.getUser().getPrize().getId()));
    }
}



