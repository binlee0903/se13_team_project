package org.se13.view.ranking;

import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TableRow;

import java.util.List;
import java.util.Optional;

import org.se13.SE13Application;
import org.se13.sqlite.ranking.Ranking;
import org.se13.sqlite.ranking.RankingRepository;
import org.se13.sqlite.ranking.RankingRepositoryImpl;
import org.se13.view.base.BaseController;
import org.se13.view.nav.AppScreen;

public class RankingScreenController extends BaseController {
    public RankingScreenController() {
        this.rankingRepository = new RankingRepositoryImpl();
    }

    @Override
    public void onCreate() {
        positionColumn.setCellValueFactory(new PropertyValueFactory<>("position"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        scoreColumn.setCellValueFactory(new PropertyValueFactory<>("score"));
        isItemColumn.setCellValueFactory(new PropertyValueFactory<>("isItem"));
        diffColumn.setCellValueFactory(new PropertyValueFactory<>("diff"));
        int currentScore = score;
        int lastRankingScore = getLastRankingScore();

        if (currentScore > lastRankingScore) {
            showNicknameInputUI(); // 닉네임 입력 화면을 표시하는 메서드
            loadRanking();
        } else {
            loadRanking(); // 바로 랭킹 화면을 로드하는 메서드
        }
    }

    // 현재 점수를 설정하기 위한 메서드
    public void setArguments(int score, boolean isItem, String diff) {
        this.score = score;
        this.isItem = isItem;
        this.diff = diff;
    }

    // test를 위한 getter
    public int getScore() {
        return score;
    }

    public boolean getIsItem() {
        return isItem;
    }

    public String getDiff() {
        return diff;
    }

    public void loadRanking() {
        // 랭킹 데이터 로드
        List<Ranking> rankingData = loadRankingData();

        ObservableList<RankingProperty> rankings;
        if (rankingData != null && !rankingData.isEmpty()) {
            rankings = convertToRankingsList(rankingData);
        } else {
            // 데이터가 없는 경우 빈 ObservableList를 설정
            rankings = FXCollections.observableArrayList();
        }
        tableView.setItems(rankings);
    }

    private List<Ranking> loadRankingData() {
        return rankingRepository.getRankingList();
    }

    private int getLastRankingScore() {
        // 랭킹 데이터 가져오기
        List<Ranking> rankingData = loadRankingData();
        if (rankingData == null || rankingData.isEmpty()) {
            return 0; // 랭킹 데이터가 없는 경우
        }
        // 랭킹 데이터가 10개 미만인 경우
        if (rankingData.size() < 10) {
            return 0; // 랭킹 데이터가 10개 미만인 경우
        }
        // 마지막 랭킹 항목의 점수를 가져오기
        Number scoreObject = rankingData.getLast().getScore();
        return scoreObject.intValue();
    }

    private void showNicknameInputUI() {
        // TextInputDialog 생성
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("닉네임 입력");
        dialog.setHeaderText(null); // 헤더 텍스트는 사용하지 않음
        dialog.setContentText("닉네임을 입력해주세요:");

        // 사용자가 OK 버튼을 누를 때까지 대화 상자를 표시하고 입력 결과를 기다림
        Optional<String> result = dialog.showAndWait();

        result.ifPresent(nickname -> {
            // 랭킹 테이블에 인서트
            rankingRepository.insertRanking(nickname, score, isItem, diff);
            // 랭킹 데이터 로드
            List<Ranking> rankingData = loadRankingData();
            // id가 autoincrement로 설정되어 있으므로 마지막 id를 가져옴
            int lastId = rankingData.stream().mapToInt(Ranking::getId).max().orElse(0);
            tableView.setRowFactory(tv -> new TableRow<RankingProperty>() {
                @Override
                protected void updateItem(RankingProperty item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item != null && item.getId() == lastId) {
                        // 조건을 만족하는 row의 배경색을 변경
                        setStyle("-fx-background-color: yellow;");
                    } else {
                        setStyle("");
                    }
                }
            });
        });
    }

    private ObservableList<RankingProperty> convertToRankingsList(List<Ranking> rawData) {
        ObservableList<RankingProperty> rankings = FXCollections.observableArrayList();
        int rank = 1; // 순위를 나타내는 변수 초기화

        for (Ranking entry : rawData) {
            if (entry.getName() == null || entry.getScore() == 0) {
                continue;
            }
            int id = entry.getId();
            String name = entry.getName();
            int score = entry.getScore();
            boolean isItem = entry.isItem();
            String diff = entry.getDiff();
            rankings.add(new RankingProperty(id, rank, name, score, isItem, diff));
            rank++; // 다음 순위로 업데이트
        }

        return rankings;
    }

    @FXML
    private void handleHomeButtonAction() {
        // Turn into a start screen
        SE13Application.navController.navigate(AppScreen.START);
    }

    @FXML
    private void handleTetrisButtonAction() {
        // Turn into a tetris screen
        SE13Application.navController.navigate(AppScreen.LEVEL_SELECT);
    }

    private final RankingRepository rankingRepository;

    private int score; // 최종 점수
    private boolean isItem; // 아이템
    private String diff; // 난이도

    @FXML
    private Button homeButton;
    @FXML
    private Button tetrisButton;
    @FXML
    private TableView<RankingProperty> tableView;
    @FXML
    private TableColumn<RankingProperty, Number> positionColumn;
    @FXML
    private TableColumn<RankingProperty, String> nameColumn;
    @FXML
    private TableColumn<RankingProperty, Number> scoreColumn;
    @FXML
    private TableColumn<RankingProperty, Boolean> isItemColumn;
    @FXML
    private TableColumn<RankingProperty, String> diffColumn;
}
