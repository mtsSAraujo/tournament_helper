package com.tournament_helper.service;

import com.tournament_helper.controller.OpggApi;
import com.tournament_helper.domain.Match;
import com.tournament_helper.domain.SearchedMatchDetails;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.List;

import static com.tournament_helper.service.excel.ExcelExporter.exportToExcel;

@Component
@RequiredArgsConstructor
public class TournamentUI {
    private final PlayerService playerService;
    private final MatchService matchService;
    private final OpggApi opggApi;

    private JFrame frame;
    private JTextField nameField;
    private JTextField tagField;
    private DefaultTableModel tableModel;
    private JTable matchTable;
    private JButton selectButton;

    private String userName;
    private List<Match> matches;

    @PostConstruct
    public void init() {
        SwingUtilities.invokeLater(this::initializeUI);
    }

    private void initializeUI() {
        frame = new JFrame("Painel Interativo");
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

        nameField = new JTextField(15);
        gbc.gridx = 0;
        gbc.gridy = 1;
        frame.add(nameField, gbc);

        JLabel tagLabel = new JLabel("Tag:");
        gbc.gridx = 1;
        gbc.gridy = 0;
        frame.add(tagLabel, gbc);

        tagField = new JTextField(4);
        gbc.gridx = 1;
        gbc.gridy = 1;
        frame.add(tagField, gbc);

        String[] colunas = {"Match Start", "Match End", "Hunter"};
        tableModel = new DefaultTableModel(colunas, 0);
        matchTable = new JTable(tableModel);
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

        selectButton = new JButton("Selecionar Partida");
        selectButton.setEnabled(false);
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        frame.add(selectButton, gbc);

        enterButton.addActionListener(this::onEnterButtonClick);
        selectButton.addActionListener(this::onSelectButtonClick);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void onEnterButtonClick(ActionEvent e) {
        String name = nameField.getText();
        String tag = tagField.getText();

        if (name.isEmpty() || tag.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Preencha todos os campos!", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        userName = name + "#" + tag;
        String playerId = playerService.findPlayerId(userName);
        matches = matchService.findAllMatches(playerId);

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
    }

    private void onSelectButtonClick(ActionEvent e) {
        int selectedRow = matchTable.getSelectedRow();
        if (selectedRow != -1 && matches != null && selectedRow < matches.size()) {
            Match selectedMatch = matches.get(matchTable.convertRowIndexToModel(selectedRow));
            String matchId = selectedMatch.getMatchId();
            exportToExcel(fetchMatchSelect(matchId), "matches.xlsx");
            JOptionPane.showMessageDialog(frame,
                    "Você selecionou a partida iniciada em: " +
                            new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(selectedMatch.getMatchStart()));
        }
    }

    private List<SearchedMatchDetails> fetchMatchSelect(String matchId) {
        return opggApi.findMatchById(matchId).stream().sorted(Comparator.comparing(SearchedMatchDetails::getPlacement)).toList();
    }
}

