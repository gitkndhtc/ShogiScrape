package com.example.parser;

import com.example.GameResult;
import com.example.domain.GameResultTable;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class GameResultParserImpl implements GameResultParser {
    private static final String skipIndicator = "9999年12月31日";
    private static final String datePatternString = "(\\d{1,2}月.*\\d{1,2}日)";
    private static final String resultsPatternString =
            "<tr>" +
                    "<td>(<a.*\">)?([^<]*)(</a>)?</td>" +
                    "<td.*>(.)</td>" +
                    "<td>(<a.*\">)?([^<]*)(</a>)?</td>" +
                    "<td>(<a.*\">)?([^<]*)(</a>)?</td>" +
                    "<td.*>(.)</td>" +
                    "<td>.*</td>" +
            "</tr>";
//<tr>
//    <td>順位戦A級 </td>
//    <td class="tac">●</td>
//    <td><a href="/player/pro/208.html">行方尚史</a></td>
//    <td><a href="/player/pro/175.html">羽生善治</a></td>
//    <td class="tac">○</td>
//    <td><a href="http://www.meijinsen.jp/" target="_blank">名人戦棋譜速報</a>・<a href="http://www.shogi.or.jp/mobile/">携帯中継</a></td>
//</tr>
    private String year;
    private String month;
    private String day;

    public List<GameResult> parseResultsOnShogiAssoc(String resultPage, String targetGameDate) {
        String[] currentDate = Pattern.compile("[年月日]").split(targetGameDate);
        this.year = currentDate[0] + "年";
        this.month = currentDate[1] + "月";
        this.day = currentDate[2] + "日";

        String gameDate = "";
        List<GameResult> gameResults = new ArrayList<>();

        List<String> resultPageRows = Arrays.asList(resultPage.split("\n"));

        for (String resultPageRow : resultPageRows) {
            Matcher dateResult =
                    Pattern.compile(datePatternString).matcher(resultPageRow);
            if (dateResult.find()) {
                gameDate = convertGameDate(dateResult.group(1));
                continue;
            } else {
                Matcher matchingResult =
                        Pattern.compile(resultsPatternString).matcher(resultPageRow);
                if (matchingResult.find()) {
                    gameResults.add(new GameResult(
                                    matchingResult.group(6),
                                    convertResultCharacter(matchingResult.group(4)),
                                    matchingResult.group(9),
                                    convertResultCharacter(matchingResult.group(11)),
                                    matchingResult.group(2).trim(),
                                    gameDate
                            )
                    );
                }
            }
        }
        return gameResults.stream()
                .filter(gameResult -> targetGameDate
                        == skipIndicator ? true : gameResult.getGameDate().contains(targetGameDate))
                .collect(Collectors.toList());
    }

    @Override
    public GameResultTable parseResultsOnNHKCup(String resultPage, GameResultTable gameResultTable) {
        int index = gameResultTable.getGameDate().indexOf("年") + 1;
        String gameDate = gameResultTable.getGameDate().substring(index);
        String resultsPatternStringForNHK =
                        "<td>" + gameDate + "...</td>" +
                        ".+?<td>(.)</td>" +
                        ".+?<td>.*?" + gameResultTable.getFirstMover() + ".*?</td>" +
                        ".+?<td>.*?" + gameResultTable.getSecondMover() + ".*?</td>" +
                        ".+?<td>(.)</td>";
        GameResultTable updatedGameResultTable = gameResultTable;
        String resultPageRow = resultPage.replaceAll("\n","");
        Matcher matchingResult =
                Pattern.compile(resultsPatternStringForNHK)
                .matcher(resultPageRow);

        if (matchingResult.find()) {
            updatedGameResultTable = new GameResultTable(
                    gameResultTable.getFirstMover(),
                    convertResultCharacter(matchingResult.group(1)),
                    gameResultTable.getSecondMover(),
                    convertResultCharacter(matchingResult.group(2)),
                    gameResultTable.getTournamentName(),
                    gameResultTable.getGameDate(),
                    null
            );
        }

        return updatedGameResultTable;
    }

    @Override
    public GameResultTable parseResultsOnGalaxyTournament(String resultPage, GameResultTable gameResultTable) {
        int beginIdx = gameResultTable.getGameDate().indexOf("月") + 1;
        int endIdx = gameResultTable.getGameDate().indexOf("日");
        String gameDate = gameResultTable.getGameDate().substring(beginIdx,endIdx);
        String resultsPatternStringForGalaxy =
                "<td>.*?" + gameDate + "日.*?</td>" +
                ".+?<td>.+?</td>" +
                ".+?<td>.+?</td>" +
                ".+?<td>(.)</td>" +
                ".+?<td>.*?" + gameResultTable.getFirstMover() + ".*?</td>" +
                ".+?<td>.*?" + gameResultTable.getSecondMover() + ".*?</td>" +
                ".+?<td>(.)</td>";
        GameResultTable updatedGameResultTable = gameResultTable;
        String resultPageRow = resultPage.replaceAll("\n","");
        Matcher matchingResult =
                Pattern.compile(resultsPatternStringForGalaxy)
                        .matcher(resultPageRow);

        if (matchingResult.find()) {
            updatedGameResultTable = new GameResultTable(
                    gameResultTable.getFirstMover(),
                    convertResultCharacter(matchingResult.group(1)),
                    gameResultTable.getSecondMover(),
                    convertResultCharacter(matchingResult.group(2)),
                    gameResultTable.getTournamentName(),
                    gameResultTable.getGameDate(),
                    null
            );
        }

        return updatedGameResultTable;
    }

    private String convertResultCharacter(String resultCharacter) {
        switch (resultCharacter) {
            case "○":
            case "□":
                return "勝";
            case "●":
            case "■":
                return "負";
            default:
                return "";
        }
    }

    private String convertGameDate(String gameDate) {
        List<String> gameDates = Arrays.asList(gameDate.split("・"));
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("M");
        String thisMonth = LocalDate.now().format(dateFormatter);
        dateFormatter = DateTimeFormatter.ofPattern("YYYY");
        String thisYear = LocalDate.now().format(dateFormatter);
        String lastYear = LocalDate.now().minusYears(1).format(dateFormatter);

        if(thisMonth.contentEquals("1")) {
            year = gameDates.get(0).contains("12") ? lastYear : thisYear;
        } else {
            year = thisYear + "年";
        }

        if (gameDates.size() == 1) {
            return year + gameDate;
        } else {
            String firstDay = year + gameDates.get(0) + "日";
            String secondDay =
                    gameDates.get(1).contains("月") ? year + gameDates.get(1) : year + month + gameDates.get(1);
            return firstDay + "," + secondDay;
        }
    }
}
