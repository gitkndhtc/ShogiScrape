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
    private static final String datePattern = "(\\d{1,2}月.*\\d{1,2}日)";
    private String year;
    private String month;
    private String day;

    public List<GameResult> parseResultsOnShogiAssoc(String resultPage, String targetGameDate) {
        String[] currentDate = Pattern.compile("[年月日]").split(targetGameDate);
        this.year = currentDate[0] + "年";
        this.month = currentDate[1] + "月";
        this.day = currentDate[2] + "日";

        String resultsPattern =
                "<tr>" +
                        "<td>(<a.*\">)?([^<]*)(</a>)?</td>" +
                        "<td.*>(.)</td>" +
                        "<td>(<a.*\">)?([^<]*)(</a>)?</td>" +
                        "<td>(<a.*\">)?([^<]*)(</a>)?</td>" +
                        "<td.*>(.)</td>" +
                        "<td>.*</td>" +
                "</tr>";

        String gameDate = "";
        List<GameResult> gameResults = new ArrayList<>();

        List<String> resultPageRows = Arrays.asList(resultPage.split("\n"));

        for (String resultPageRow : resultPageRows) {
            Matcher dateResult =
                    Pattern.compile(datePattern).matcher(resultPageRow);
            if (dateResult.find()) {
                gameDate = convertGameDate(dateResult.group(1));
                continue;
            } else {
                Matcher matchingResult =
                        Pattern.compile(resultsPattern).matcher(resultPageRow);
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
        String resultsPatternForNHK =
                        "<td>" + gameDate + "...</td>" +
                        ".+?<td>(.)</td>" +
                        ".+?<td>.*?" + gameResultTable.getFirstMover() + ".*?</td>" +
                        ".+?<td>.*?" + gameResultTable.getSecondMover() + ".*?</td>" +
                        ".+?<td>(.)</td>";
        GameResultTable updatedGameResultTable = gameResultTable;
        String resultPageRow = resultPage.replaceAll("\n","");
        Matcher matchingResult =
                Pattern.compile(resultsPatternForNHK)
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
        String resultsPatternForGalaxy =
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
                Pattern.compile(resultsPatternForGalaxy)
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
    public GameResultTable parseResultsOnQueenTournament(String resultPage, GameResultTable gameResultTable) {
        String resultsPatternForQueen =
                "<td.+?</td>" +
                "<td.+?" +
                    gameResultTable.getFirstMover() + ".*?" +
                    gameResultTable.getSecondMover() + ".*?" +
                "</td>" +
                "<td.*?" +
                    gameResultTable.getGameDate()
                            .replaceAll("年|月","/").replaceAll("日","") +
                "</td>" +
                "<td.*?(△|▲)";

//<tr>
// <td align="center" class="fxs">1回戦</td>
// <td align="left" class="fxs">上田初美女流三段 vs 室谷由紀女流二段</td>
// <td align="center" class="fxs">2016/06/07</td>
// <td align="center" class="fxs">2016/07/30</td>
// <td align="left" class="fxs">△上田初美女流三段<br />勝ち</td>
// <td align="center" class="fxs"><a href="javascript:go_kifu(38,'k',1,3)">棋譜</a></td>
// </tr>
//<tr class="even"><td align="center" class="fxs">1回戦</td><td align="left" class="fxs">香川愛生女流三段 vs 中村真梨花女流三段</td><td align="center" class="fxs">2016/06/07</td><td align="center" class="fxs">2016/08/06</td><td align="left" class="fxs">△香川愛生女流三段<br />勝ち</td><td align="center" class="fxs"><a href="javascript:go_kifu(38,'k',1,8)">棋譜</a></td></tr><tr><td align="center" class="fxs">1回戦</td><td align="left" class="fxs">村田智穂女流二段 vs 渡部　愛女流初段</td><td align="center" class="fxs">2016/06/08</td><td align="center" class="fxs">2016/08/13</td><td align="left" class="fxs">▲渡部　愛女流初段<br />勝ち</td><td align="center" class="fxs"><a href="javascript:go_kifu(38,'k',1,7)">棋譜</a></td></tr><tr class="even"><td align="center" class="fxs">1回戦</td><td align="left" class="fxs">石高澄恵女流二段 vs 伊藤沙恵女流二段</td><td align="center" class="fxs">2016/06/08</td><td align="center" class="fxs">2016/08/20</td><td align="left" class="fxs">△伊藤沙恵女流二段<br />勝ち</td><td align="center" class="fxs"><a href="javascript:go_kifu(38,'k',1,6)">棋譜</a></td></tr><tr><td align="center" class="fxs">1回戦</td><td align="left" class="fxs">加藤桃子女王女流王座 vs 千葉涼子女流四段</td><td align="center" class="fxs">2016/06/09</td><td align="center" class="fxs">2016/08/27</td><td align="left" class="fxs">▲加藤桃子女王女流王座<br />勝ち</td><td align="center" class="fxs"><a href="javascript:go_kifu(38,'k',1,4)">棋譜</a></td></tr><tr class="even"><td align="center" class="fxs">1回戦</td><td align="left" class="fxs">山口絵美菜女流２級 vs 熊倉紫野女流初段</td><td align="center" class="fxs">2016/06/09</td><td align="center" class="fxs">2016/09/03</td><td align="left" class="fxs">▲熊倉紫野女流初段<br />勝ち</td><td align="center" class="fxs"><a href="javascript:go_kifu(38,'k',1,5)">棋譜</a></td></tr><tr><td align="center" class="fxs">1回戦</td><td align="left" class="fxs">小野ゆかりアマ vs 里見咲紀女流２級</td><td align="center" class="fxs">2016/06/29</td><td align="center" class="fxs">2016/09/10</td><td align="left" class="fxs">▲小野ゆかりアマ<br />勝ち</td><td align="center" class="fxs"><a href="javascript:go_kifu(38,'k',1,2)">棋譜</a></td></tr><tr class="even"><td align="center" class="fxs">1回戦</td><td align="left" class="fxs">岩根　忍女流三段 vs 北村桂香女流初段</td><td align="center" class="fxs">2016/07/01</td><td align="center" class="fxs">2016/09/17</td><td align="left" class="fxs">△岩根　忍女流三段<br />勝ち</td><td align="center" class="fxs"><a href="javascript:go_kifu(38,'k',1,1)">棋譜</a></td></tr><tr><td align="center" class="fxs">2回戦</td><td align="left" class="fxs">伊藤沙恵女流二段 vs 熊倉紫野女流初段</td><td align="center" class="fxs">2016/07/13</td><td align="center" class="fxs">2016/09/24</td><td align="left" class="fxs">△伊藤沙恵女流二段<br />勝ち</td><td align="center" class="fxs"><a href="javascript:go_kifu(38,'k',2,3)">棋譜</a></td></tr><tr class="even"><td align="center" class="fxs">2回戦</td><td align="left" class="fxs">加藤桃子女王女流王座 vs 上田初美女流三段</td><td align="center" class="fxs">2016/07/20</td><td align="center" class="fxs">2016/10/01</td><td align="left" class="fxs">▲上田初美女流三段<br />勝ち</td><td align="center" class="fxs"><a href="javascript:go_kifu(38,'k',2,2)">棋譜</a></td></tr><tr><td align="center" class="fxs">2回戦</td><td align="left" class="fxs">小野ゆかりアマ vs 岩根　忍女流三段</td><td align="center" class="fxs">2016/07/29</td><td align="center" class="fxs">2016/10/08</td><td align="left" class="fxs">▲岩根　忍女流三段<br />勝ち</td><td align="center" class="fxs"><a href="javascript:go_kifu(38,'k',2,1)">棋譜</a></td></tr><tr class="even"><td align="center" class="fxs">2回戦</td><td align="left" class="fxs">香川愛生女流三段 vs 渡部　愛女流初段</td><td align="center" class="fxs">2016/07/29</td><td align="center" class="fxs">2016/10/15</td><td align="left" class="fxs">▲香川愛生女流三段<br />勝ち</td><td align="center" class="fxs"><a href="javascript:go_kifu(38,'k',2,4)">棋譜</a></td></tr><tr><td align="center" class="fxs">準決勝</td><td align="left" class="fxs">上田初美女流三段 vs 岩根　忍女流三段</td><td align="center" class="fxs">2016/08/16</td><td align="center" class="fxs">2016/10/22</td><td align="left" class="fxs">△上田初美女流三段<br />勝ち</td><td align="center" class="fxs"><a href="javascript:go_kifu(38,'k',3,1)">棋譜</a></td></tr><tr class="even"><td align="center" class="fxs">準決勝</td><td align="left" class="fxs">香川愛生女流三段 vs 伊藤沙恵女流二段</td><td align="center" class="fxs">2016/08/16</td><td align="center" class="fxs">2016/10/29</td><td align="left" class="fxs">△香川愛生女流三段<br />勝ち</td><td align="center" class="fxs"><a href="javascript:go_kifu(38,'k',3,2)">棋譜</a></td></tr><tr><td align="center" class="fxs">挑戦者決定戦</td><td align="left" class="fxs">香川愛生女流三段 vs 上田初美女流三段</td><td align="center" class="fxs">2016/08/30</td><td align="center" class="fxs">2016/11/05</td><td align="left" class="fxs">△香川愛生女流三段<br />勝ち</td><td align="center" class="fxs"><a href="javascript:go_kifu(38,'k',4,1)">棋譜</a></td></tr>
// <tr class="even">
// <td align="center" class="fxs">三番勝負<br />第1局</td>
// <td align="left" class="fxs">里見香奈女流王将 vs 香川愛生女流三段</td>
// <td align="center" class="fxs">2016/10/01</td>
// <td align="center" class="fxs">2016/11/12</td>
// <td align="left" class="fxs">△里見香奈女流王将<br />勝ち</td>
// <td align="center" class="fxs"><a href="javascript:go_kifu(38,'k',5,1)">棋譜</a></td>
// </tr>
// <tr><td align="center" class="fxs">三番勝負<br />第2局</td><td align="left" class="fxs">里見香奈女流王将 vs 香川愛生女流三段</td><td align="center" class="fxs">2016/10/11</td><td align="center" class="fxs">2016/11/19</td><td align="left" class="fxs"><br /></td><td align="center" class="fxs"></td></tr>

        GameResultTable updatedGameResultTable = gameResultTable;
        String resultPageRow = resultPage.replaceAll("\n","");
        Matcher matchingResult =
                Pattern.compile(resultsPatternForQueen)
                        .matcher(resultPageRow);

        if (matchingResult.find()) {
            updatedGameResultTable = new GameResultTable(
                    gameResultTable.getFirstMover(),
                    convertResultCharacter2(matchingResult.group(1),true),
                    gameResultTable.getSecondMover(),
                    convertResultCharacter2(matchingResult.group(1),false),
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

    private String convertResultCharacter2(String resultCharacter, boolean firstMover) {
        switch (resultCharacter) {
            case "△":
                return firstMover ? "勝" : "負";
            case "▲":
                return firstMover ? "負" : "勝";
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
