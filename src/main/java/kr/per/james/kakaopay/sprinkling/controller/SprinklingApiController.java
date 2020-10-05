package kr.per.james.kakaopay.sprinkling.controller;

import kr.per.james.kakaopay.sprinkling.constant.Headers;
import kr.per.james.kakaopay.sprinkling.dto.SprinklingAssignDto;
import kr.per.james.kakaopay.sprinkling.dto.SprinklingInquiryDto;
import kr.per.james.kakaopay.sprinkling.dto.SprinklingOrderDto;
import kr.per.james.kakaopay.sprinkling.service.SprinklingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.net.URI;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api/v1/sprinkling", produces = MediaType.APPLICATION_JSON_VALUE)
public class SprinklingApiController {

    private final SprinklingService sprinklingService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SprinklingOrderDto.Response> orderSprinkling(
            @RequestHeader(Headers.X_USER_ID) final Integer userId,
            @RequestHeader(Headers.X_ROOMS_ID) final String roomId,
            @RequestBody @Valid final SprinklingOrderDto.Request request) {

        final String token = sprinklingService.orderSprinkling(userId, roomId, request.getCount(), request.getAmount());
        final SprinklingOrderDto.Response response = SprinklingOrderDto.Response.builder().token(token).build();
        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(URI.create("/api/v1/sprinkling/tokens/" + token));
        return new ResponseEntity<>(response, httpHeaders, HttpStatus.CREATED);
    }

    @PutMapping(value = "/tokens/{token}")
    public ResponseEntity<SprinklingAssignDto.Response> assignSprinkling(
            @RequestHeader(Headers.X_USER_ID) final Integer userId,
            @RequestHeader(Headers.X_ROOMS_ID) final String roomId,
            @PathVariable String token) {

        final BigDecimal amount = sprinklingService.assignSprinkling(userId, roomId, token);
        final SprinklingAssignDto.Response response = SprinklingAssignDto.Response.builder().amount(amount).build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping(value = "/tokens/{token}")
    public ResponseEntity<SprinklingInquiryDto.Response> inquireSprinkling(
            @RequestHeader(Headers.X_USER_ID) final Integer userId,
            @RequestHeader(Headers.X_ROOMS_ID) final String roomId,
            @PathVariable String token) {

        final SprinklingInquiryDto.Response response = sprinklingService.inquireSprinkling(userId, roomId, token);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
