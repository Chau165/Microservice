package com.group1.echoservice.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/internal")
public class EchoController {

    @GetMapping(value = "/echo", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> echo(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @RequestHeader(value = "X-User-Role", required = false) String userRole
    ) {
        Map<String, Object> res = new LinkedHashMap<>();
        res.put("xUserId", userId);
        res.put("xUserRole", userRole);
        return res;
    }
}
