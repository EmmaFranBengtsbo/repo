package com.game.controller;

import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/rest")
public class PlayerController{

    private final PlayerService playerService;

    @Autowired
    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @GetMapping("/players/{id}")
    public ResponseEntity<Player> getPlayerById(@PathVariable("id") long id)
    {
        if (id <= 0)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        else if (!playerService.existsById(id))
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        else
        {
            Optional<Player> player = playerService.findById(id);
            Player pl = player.get();
            return new ResponseEntity<>(pl, HttpStatus.OK);
        }
    }

    @DeleteMapping("/players/{id}")
    public String deletePlayerById(@PathVariable("id") long id)
    {
        if (id <= 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "bad request");
        else if (!playerService.existsById(id))
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "entity not found");
        else playerService.deletePlayerByID(id);
       return "redirect:/players";
    }

    @PostMapping("/players/{id}")
    public ResponseEntity<Player> updatePlayer(@PathVariable("id") long id,
                                               @RequestBody Player player)
    {
        if (id <= 0)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        if (!playerService.existsById(id))
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        if (((player.getName() == null)) &&
                (player.getTitle() == null) &&
                (player.getRace() == null) &&
                (player.getExperience() == null) &&
                (player.getBirthday() == null) &&
                (player.isBanned() == null) &&
                (player.getExperience() == null))
        {
            Player actual = playerService.findById(id).get();
            return new ResponseEntity<>(actual, HttpStatus.OK);
        }
        if (player.getName() != null && (player.getName().isEmpty() || player.getName().length() > 12))
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        if ((player.getExperience() != null) && ((player.getExperience() < 0) ||
                (player.getExperience() > 10000000L)))
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        if (player.getBirthday() != null && (player.getBirthday().getTime() < 0))
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);

        Player updatedPlayer = playerService.updatePlayer(id, player);
        return new ResponseEntity<>(updatedPlayer, HttpStatus.OK);
    }

    @GetMapping("/players/count")
    public ResponseEntity<Integer> getCount(
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "title", required = false) String title,
            @RequestParam(name = "after", required = false) Long after,
            @RequestParam(name = "before", required = false) Long before,
            @RequestParam(name = "minExperience", required = false) Integer minExperience,
            @RequestParam(name = "maxExperience", required = false) Integer maxExperience,
            @RequestParam(name = "minLevel", required = false) Integer minLevel,
            @RequestParam(name = "maxLevel", required = false) Integer maxLevel,
            @RequestParam(name = "race", required = false) Race race,
            @RequestParam(name = "profession", required = false) Profession profession,
            @RequestParam(name = "banned", required = false) Boolean banned
    )
    {
        Integer count = playerService.countAll(name, title, race, profession,
                after, before, banned, minExperience, maxExperience,
                minLevel, maxLevel);
        return new ResponseEntity<>(count, HttpStatus.OK);
    }

    @PostMapping("/players")
    public ResponseEntity<Player> createPlayer(@RequestBody Player player)
    {
        if ((player.getName() == null) || (player.getName().isEmpty())
                || (player.getName().length() > 12)
                || (player.getTitle() == null)
                || (player.getTitle().isEmpty())
                || (player.getTitle().length() > 30)
                || (player.getExperience() == null)
                || (player.getExperience() < 0)
                || (player.getExperience() > 10000000L)
                || (player.getBirthday() == null)
                || (player.getBirthday().getTime() < 0)/*
                || (player.getBirthday().getYear() < 2000)
                || (player.getBirthday().getYear() > 3000)*/)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        else {
            Player savedPlayer = playerService.createPlayer(player);
            return new ResponseEntity<>(player, HttpStatus.OK);
        }
    }

    @GetMapping("/players")
    public ResponseEntity<List<Player>> getFilteredPlayers(
            @RequestParam(name = "name", required = false) String name,
    @RequestParam(name = "title", required = false) String title,
    @RequestParam(name = "race", required = false) Race race,
    @RequestParam(name = "profession", required = false) Profession profession,
    @RequestParam(name = "after", required = false) Long after,
    @RequestParam(name = "before", required = false) Long before,
    @RequestParam(name = "banned", required = false) Boolean banned,
    @RequestParam(name = "minExperience", required = false) Integer minExperience,
    @RequestParam(name = "maxExperience", required = false) Integer maxExperience,
    @RequestParam(name = "minLevel", required = false) Integer minLevel,
    @RequestParam(name = "maxLevel", required = false) Integer maxLevel,
    @RequestParam(name = "order", required = false) PlayerOrder order,
    @RequestParam(name = "pageNumber", required = false) Integer pageNumber,
    @RequestParam(name = "pageSize", required = false) Integer pageSize
        )
    {
        List<Player> players = playerService.getPagedPlayers(name, title, race,
                profession,after, before, banned,minExperience, maxExperience, minLevel,
                maxLevel, order, pageNumber, pageSize);
        return new ResponseEntity<>(players, HttpStatus.OK);
    }
}
