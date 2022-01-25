package com.game.service;

import com.game.controller.PlayerOrder;
import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.repository.PlayerRepository;
import net.bytebuddy.agent.builder.AgentBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PlayerService {
    private final PlayerRepository playerRepository;

    @Autowired
    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public void deletePlayerByID(Long id)
    {
        playerRepository.deleteById(id);
    }

    public boolean existsById(Long id)
    {
        return playerRepository.existsById(id);
    }

    public Optional<Player> findById(Long id)
    {
        Optional<Player> player = playerRepository.findById(id);
        return player;
    }

    public Integer countAll(String name, String title, Race race,
                     Profession profession, Long after, Long before,
                     Boolean banned,
                     Integer minExperience, Integer maxExperience,
                     Integer minLevel, Integer maxLevel)
    {
        List<Player> playersFound = playerRepository.findAll().stream().
                filter(player -> name == null || player.getName().contains(name)).
                filter(player -> title == null || player.getTitle().contains(title)).
                filter(player -> race == null || player.getRace().equals(race)).
                filter(player -> profession == null || player.getProfession().equals(profession)).
                filter(player -> after == null || player.getBirthday().getTime() > after).
                filter(player -> before == null || player.getBirthday().getTime() < before).
                filter(player -> banned == null || player.isBanned().equals(banned)).
                filter(player -> minExperience == null || player.getExperience() >= minExperience).
                filter(player -> maxExperience    == null || player.getExperience() <= maxExperience).
                filter(player -> minLevel == null || player.getLevel() >= minLevel).
                filter(player -> maxLevel == null || player.getLevel() <= maxLevel).
                collect(Collectors.toList());
        return playersFound.size();
    }
    public Player updatePlayer(Long id, Player player) {
        Player playerToUpdate = playerRepository.findById(id).get();
        if (player.getName() != null)
            playerToUpdate.setName(player.getName());
        if (player.getTitle() != null)
            playerToUpdate.setTitle(player.getTitle());
        if (player.getRace() != null)
            playerToUpdate.setRace(player.getRace());
        if (player.getProfession() != null)
            playerToUpdate.setProfession(player.getProfession());
        if (player.getBirthday() != null)
            playerToUpdate.setBirthday(player.getBirthday());
        if (player.isBanned() != null)
            playerToUpdate.setBanned(player.isBanned());
        if (player.getExperience() != null)
        {
            playerToUpdate.setExperience(player.getExperience());
            playerToUpdate.setLevel();
            playerToUpdate.setUntilNextLevel();
        }
        playerRepository.save(playerToUpdate);
        return playerToUpdate;
    }

    public Player createPlayer(Player player)
    {
        player.setLevel();
        player.setUntilNextLevel();
        return playerRepository.save(player);
    }

    public List<Player> getPagedPlayers(String name, String title, Race race,
                                        Profession profession, Long after, Long before,
                                        Boolean banned,
                                        Integer minExperience, Integer maxExperience,
                                        Integer minLevel, Integer maxLevel,
                                        PlayerOrder playerOrder, Integer pageNumber,
                                        Integer pageSize
    )
    {
        if (pageSize == null)
            pageSize = 3;
        if (pageNumber == null)
            pageNumber = 0;
        final PlayerOrder order;
        if (playerOrder == null)
            order = PlayerOrder.ID;
        else
            order = playerOrder;
        Sort sort = Sort.by(order.getFieldName());
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize, sort);
        List<Player> players = playerRepository.findAll().stream().
                sorted(((player1, player2) -> {
                    if (PlayerOrder.LEVEL.equals(order))
                    {
                        return player1.getLevel().compareTo(player2.getLevel());
                    }
                    if (PlayerOrder.BIRTHDAY.equals(order))
                    {
                        return player1.getBirthday().compareTo(player2.getBirthday());
                    }
                    if (PlayerOrder.EXPERIENCE.equals(order))
                    {
                        return player1.getExperience().compareTo(player2.getExperience());
                    }
                    if (PlayerOrder.NAME .equals(order))
                    {
                        return player1.getName().compareTo(player2.getName());
                    }
                    return player1.getId().compareTo(player2.getId());
                } )).filter(player -> name == null || player.getName().contains(name)).
                filter(player -> title == null || player.getTitle().contains(title)).
                filter(player -> race == null || player.getRace().equals(race)).
                filter(player -> profession == null || player.getProfession().equals(profession)).
                filter(player -> after == null || player.getBirthday().getTime() > after).
                filter(player -> before == null || player.getBirthday().getTime() < before).
                filter(player -> banned == null || player.isBanned().equals(banned)).
                filter(player -> minExperience == null || player.getExperience() >= minExperience).
                filter(player -> maxExperience    == null || player.getExperience() <= maxExperience).
                filter(player -> minLevel == null || player.getLevel() >= minLevel).
                filter(player -> maxLevel == null || player.getLevel() <= maxLevel).
                skip(pageSize * pageNumber).
                limit(pageSize).collect(Collectors.toList());
        return players;
    }
}
