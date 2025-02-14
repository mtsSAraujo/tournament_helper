package com.tournament_helper;

import com.tournament_helper.common.utils.ExcelExporter;
import com.tournament_helper.domain.Match;
import com.tournament_helper.domain.SearchedMatchDetails;
import com.tournament_helper.service.MatchService;
import com.tournament_helper.service.PlayerService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.List;

import static com.tournament_helper.utils.InterfaceGeneration.startInterface;

@SpringBootApplication
public class TournamentHelperApplication {

    public static void main(String[] args) {
        System.setProperty("java.awt.headless", "false");

        ApplicationContext context = SpringApplication.run(TournamentHelperApplication.class, args);

        PlayerService playerService = context.getBean(PlayerService.class);
        MatchService matchService = context.getBean(MatchService.class);

        startInterface(playerService, matchService);
    }
}
