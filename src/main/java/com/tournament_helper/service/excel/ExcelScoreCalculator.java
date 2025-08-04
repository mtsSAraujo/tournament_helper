package com.tournament_helper.service.excel;

import org.apache.commons.math3.util.Pair;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcelScoreCalculator {

    private static final Map<Integer, Integer> POSITION_POINTS = new HashMap<>();

    static {
        POSITION_POINTS.put(1, 20);
        POSITION_POINTS.put(2, 15);
        POSITION_POINTS.put(3, 12);
        POSITION_POINTS.put(4, 10);
        POSITION_POINTS.put(5, 8);
        POSITION_POINTS.put(6, 6);
        POSITION_POINTS.put(7, 4);
        POSITION_POINTS.put(8, 2);
        POSITION_POINTS.put(9, 1);
        POSITION_POINTS.put(10, 0);
        POSITION_POINTS.put(11, 0);
        POSITION_POINTS.put(12, 0);
    }

    public static void updateTotalSheet(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            System.out.println("Arquivo não encontrado: " + filePath);
            return;
        }

        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Map<String, Integer> teamScores = new HashMap<>();
            Map<String, Double> playerKdaTotal = new HashMap<>();
            Map<String, Integer> playerKdaCount = new HashMap<>();

            for (Sheet sheet : workbook) {
                if (sheet.getSheetName().startsWith("Match ")) {
                    processMatchSheet(sheet, teamScores);

                    int numRows = sheet.getPhysicalNumberOfRows();
                    for (int i = 1; i < numRows; i++) {
                        Row row = sheet.getRow(i);
                        if (row == null) continue;

                        String playerName = row.getCell(4).getStringCellValue(); // PLAYER
                        double kda = row.getCell(8).getNumericCellValue(); // KDA

                        playerKdaTotal.put(playerName, playerKdaTotal.getOrDefault(playerName, 0.0) + kda);
                        playerKdaCount.put(playerName, playerKdaCount.getOrDefault(playerName, 0) + 1);
                    }
                }
            }

            // Criar ou limpar a aba 'Total'
            Sheet totalSheet = workbook.getSheet("Total");
            if (totalSheet == null) {
                totalSheet = workbook.createSheet("Total");
            } else {
                int lastRow = totalSheet.getLastRowNum();
                for (int i = lastRow; i >= 0; i--) {
                    Row row = totalSheet.getRow(i);
                    if (row != null) totalSheet.removeRow(row);
                }
            }

            // Cabeçalho
            Row headerRow = totalSheet.createRow(0);
            headerRow.createCell(0).setCellValue("TAG");
            headerRow.createCell(1).setCellValue("PONTOS");
            headerRow.createCell(3).setCellValue("PLAYER");
            headerRow.createCell(4).setCellValue("KDA");

            List<Map.Entry<String, Integer>> sortedTeamScores = new ArrayList<>(teamScores.entrySet());
            sortedTeamScores.sort((a, b) -> Integer.compare(b.getValue(), a.getValue()));

            // Escrever times
            int rowNum = 1;
            for (Map.Entry<String, Integer> entry : sortedTeamScores) {
                Row row = totalSheet.createRow(rowNum++);
                row.createCell(0).setCellValue(entry.getKey()); // TAG
                row.createCell(1).setCellValue(entry.getValue()); // PONTOS
            }

            // Calcular KDA médio e ordenar
            Map<String, Double> averageKdaMap = new HashMap<>();
            for (String player : playerKdaTotal.keySet()) {
                double totalKda = playerKdaTotal.get(player);
                int count = playerKdaCount.get(player);
                double average = totalKda / count;
                averageKdaMap.put(player, average);
            }

            List<Map.Entry<String, Double>> sortedKdaList = new ArrayList<>(averageKdaMap.entrySet());
            sortedKdaList.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

            rowNum ++;

            for (Map.Entry<String, Double> entry : sortedKdaList) {
                Row row = totalSheet.createRow(rowNum++);
                row.createCell(3).setCellValue(entry.getKey());
                row.createCell(4).setCellValue(entry.getValue());
            }

            // Ajustar colunas
            totalSheet.autoSizeColumn(0);
            totalSheet.autoSizeColumn(1);
            totalSheet.autoSizeColumn(3);
            totalSheet.autoSizeColumn(4);

            // Salvar
            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                workbook.write(fos);
            }

            System.out.println("Planilha 'Total' atualizada com sucesso!");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static void processMatchSheet(Sheet sheet, Map<String, Integer> teamScores) {
        Map<String, Integer> teamKills = new HashMap<>();
        Map<String, Integer> teamPosition = new HashMap<>();

        int numRows = sheet.getPhysicalNumberOfRows();

        for (int i = 1; i < numRows; i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            int position = (int) row.getCell(0).getNumericCellValue();
            String teamTag = row.getCell(1).getStringCellValue();
            int kills = (int) row.getCell(5).getNumericCellValue();

            teamKills.put(teamTag, teamKills.getOrDefault(teamTag, 0) + kills);
            teamPosition.putIfAbsent(teamTag, position);
        }

        // Calcular pontuação final de cada time
        for (String team : teamPosition.keySet()) {
            int positionPoints = POSITION_POINTS.getOrDefault(teamPosition.get(team), 0);
            int killPoints = teamKills.getOrDefault(team, 0);

            int totalPoints = positionPoints + killPoints;

            // Somar pontuação do time ao total acumulado
            teamScores.put(team, teamScores.getOrDefault(team, 0) + totalPoints);
        }
    }
}
