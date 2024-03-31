package ru.practicum.admin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.admin.dto.NewUserDto;

import javax.validation.Valid;

@RestController
@RequestMapping("/admin")
@Slf4j
public class AdminController {
    @PostMapping("/users")
    public NewUserDto createUser(@RequestBody @Valid NewUserDto newUserDto) {
        log.info("POST /admin/users: {}", newUserDto);

        return newUserDto;
    }
}
