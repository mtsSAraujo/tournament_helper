package com.tournament_helper;

import com.tournament_helper.domain.Match;
import com.tournament_helper.service.MatchService;
import com.tournament_helper.service.PlayerService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

@SpringBootApplication
public class TournamentHelperApplication {

    public static void main(String[] args) {
        System.setProperty("java.awt.headless", "false");

        ApplicationContext context = SpringApplication.run(TournamentHelperApplication.class, args);

        PlayerService playerService = context.getBean(PlayerService.class);
        MatchService matchService = context.getBean(MatchService.class);

        startInterface(playerService, matchService);
    }

    private static void startInterface(PlayerService playerService, MatchService matchService) {
        JFrame frame = new JFrame("Painel Interativo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel nameLabel = new JLabel("Nome de Usuário:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        frame.add(nameLabel, gbc);

        JTextField nameField = new JTextField(15);
        gbc.gridx = 0;
        gbc.gridy = 1;
        frame.add(nameField, gbc);

        JLabel tagLabel = new JLabel("Tag:");
        gbc.gridx = 1;
        gbc.gridy = 0;
        frame.add(tagLabel, gbc);

        JTextField tagField = new JTextField(4);
        gbc.gridx = 1;
        gbc.gridy = 1;
        frame.add(tagField, gbc);

        String[] colunas = {"Match Start", "Match End", "Hunter"};
        DefaultTableModel tableModel = new DefaultTableModel(colunas, 0);
        JTable matchTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(matchTable);
        scrollPane.setPreferredSize(new Dimension(550, 200));

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        frame.add(scrollPane, gbc);

        JButton enterButton = new JButton("Buscar Partidas");
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        frame.add(enterButton, gbc);

        JButton selectButton = new JButton("Selecionar Partida");
        selectButton.setEnabled(false);
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        frame.add(selectButton, gbc);

        enterButton.addActionListener(e -> {
            String name = nameField.getText();
            String tag = tagField.getText();

            if (name.isEmpty() || tag.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Preencha todos os campos!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String userName = name + "#" + tag;

            String playerId = playerService.findPlayerId(userName);
            List<Match> matches = matchService.findAllMatches(playerId);

            tableModel.setRowCount(0);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

            if (matches == null || matches.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Nenhuma partida encontrada!", "Aviso", JOptionPane.INFORMATION_MESSAGE);
                selectButton.setEnabled(false);
            } else {
                for (Match match : matches) {
                    String matchStart = match.getMatchStart() != null ? dateFormat.format(match.getMatchStart()) : "N/A";
                    String matchEnd = match.getMatchEnd() != null ? dateFormat.format(match.getMatchEnd()) : "N/A";
                    String hunter = match.getHeroAssetId() != null ? match.getHeroAssetId().toString() : "N/A";

                    tableModel.addRow(new Object[]{matchStart, matchEnd, hunter});
                }
                selectButton.setEnabled(true);
            }
        });

        selectButton.addActionListener(e -> {
            int selectedRow = matchTable.getSelectedRow();
            if (selectedRow != -1) {
                String matchStart = (String) tableModel.getValueAt(selectedRow, 0);
                JOptionPane.showMessageDialog(frame, "Você selecionou a partida iniciada em: " + matchStart);
            }
        });

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
